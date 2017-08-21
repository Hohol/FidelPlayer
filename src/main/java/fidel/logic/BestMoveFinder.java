package fidel.logic;

import fidel.common.*;
import fidel.logic.evaluators.Evaluator;

import java.util.ArrayList;
import java.util.List;

import static fidel.common.Command.*;
import static fidel.common.Direction.DIRS;
import static fidel.common.TileType.*;

public class BestMoveFinder {

    final Cell exit;
    final LevelType levelType;
    final GameParameters gameParameters;
    final Simulator simulator;
    final Evaluator evaluator;
    final int requiredXp;

    final int timeout;

    List<Command> bestMoves = null;
    double bestEvaluation = Double.NEGATIVE_INFINITY;
    MoveGameState bestState = null;
    final List<Command> curMoves = new ArrayList<>();
    long start;
    final int[][] visited;
    int curVisited;

    public BestMoveFinder(GameState gameState, GameParameters gameParameters, Evaluator evaluator, int timeout) {
        this.gameParameters = gameParameters;
        levelType = gameState.levelType;
        simulator = new Simulator(gameState, gameParameters);
        visited = new int[gameState.board.height][gameState.board.width];
        this.evaluator = evaluator;
        exit = evaluator.getExit(gameState.board);
        requiredXp = gameState.xp +
                (gameState.levelType == LevelType.LEVEL_15_XP ? 15 :
                        gameState.levelType == LevelType.BEFORE_DRAGON ? 50 :
                                0);
        this.timeout = timeout;
    }

    MovesAndEvaluation findBestMoves(GameState gameState, boolean swapped) {
        if (swapped) {
            curMoves.add(ENTER);
        }
        start = System.currentTimeMillis();
        MoveGameState initialGameState = new MoveGameState(gameState, gameParameters);
        try {
            dfs(initialGameState);
        } catch (TimeoutException e) {
            //System.out.println("timeout");
        }
        //System.out.println(bestState == null ? "path not found" : bestState.ps);
        double evaluation = bestState == null ?
                Double.NEGATIVE_INFINITY :
                evaluator.evaluate(bestState, bestMoves);
        return new MovesAndEvaluation(bestMoves, evaluation, bestState);
    }

    private void dfs(MoveGameState gameState) {
        if (Thread.interrupted()) {
            throw new TimeoutException();
        }
        PlayerState ps = gameState.ps;
        Board board = gameState.board;
        Cell cur = gameState.cur;
        if (evaluator.updateOnEachMove() || evaluator.finished(gameState, exit)) {
            double evaluation = evaluator.evaluate(gameState, curMoves);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestState = gameState;
                bestMoves = new ArrayList<>(curMoves);
                //System.out.println("cur best " + ps);
            }
            if (evaluator.returnImmediately()) {
                throw new TimeoutException(); // todo rework
            }
            if (!evaluator.updateOnEachMove()) {
                return;
            }
        }
        if (bestMoves != null && tooLate()) {
            throw new TimeoutException();
        }

        List<MoveAndState> moveAndStates = new ArrayList<>();

        boolean shouldHeal = false;
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!board.inside(to)) {
                continue;
            }
            if (!passableNow(ps, board, to, dir)) {
                continue;
            }

            MoveGameState newGameState = simulator.simulateMove(gameState, dir);
            if (newGameState == null) {
                continue;
            }
            if (newGameState.ps.hp < 0) {
                shouldHeal = true;
                continue;
            }
            if (gameState.round % 3 == 0 && !exitReachable(newGameState.board, to)) {
                continue;
            }
            addMove(moveAndStates, dir.command, newGameState);
        }

        if (curMoves.isEmpty() || curMoves.get(curMoves.size() - 1) != BARK) {
            addMove(moveAndStates, BARK, simulator.simulateBark(gameState));
        }
        if (shouldHeal) {
            addMove(moveAndStates, HEAL, simulator.simulateHeal(gameState));
            addMove(moveAndStates, SYRINGE, simulator.simulateSyringe(gameState));
        }
        addMove(moveAndStates, BOMB, simulator.simulateBomb(gameState));

        for (MoveAndState moveAndState : moveAndStates) {
            curMoves.add(moveAndState.move);
            dfs(moveAndState.gameState);
            pop(curMoves);
        }
    }

    private void addMove(List<MoveAndState> moveAndStates, Command move, MoveGameState state) {
        if (state != null) {
            moveAndStates.add(new MoveAndState(move, state));
        }
    }

    static class MoveAndState {
        final Command move;
        final MoveGameState gameState;

        MoveAndState(Command move, MoveGameState gameState) {
            this.move = move;
            this.gameState = gameState;
        }

        @Override
        public String toString() {
            return "MoveAndState{" +
                    "move=" + move +
                    ", gameState=" + gameState +
                    '}';
        }
    }

    private boolean tooLate() {
//        return false;
        return System.currentTimeMillis() - start > timeout;
    }

    private boolean exitReachable(Board board, Cell cur) {
        if (exit == null) {
            return true;
        }
        curVisited++;
        return dfsCheckPath(board, cur);
    }

    private boolean dfsCheckPath(Board board, Cell cur) {
        if (cur.equals(exit)) {
            return true;
        }
        visited[cur.row][cur.col] = curVisited;
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!board.inside(to)) {
                continue;
            }
            if (!potentiallyPassable(board.get(to))) {
                continue;
            }
            if (visited[to.row][to.col] == curVisited) {
                continue;
            }
            if (dfsCheckPath(board, to)) {
                return true;
            }
        }
        return false;
    }


    private static boolean potentiallyPassable(TileType tile) {
        return tile != ENTRANCE && tile != VISITED && tile != CHEST && tile != WALL && tile != VOLCANO;
    }

    private boolean passableNow(PlayerState ps, Board board, Cell to, Direction dir) {
        TileType tile = board.get(to);
        if (!potentiallyPassable(tile)) {
            return false;
        }
        if (tile == RAISED_WALL) {
            return ps.buttonsPressed % 2 == 1;
        }
        if (tile == LOWERED_WALL) {
            return ps.buttonsPressed % 2 == 0;
        }
        if (levelType == LevelType.ROBODOG && ps.bossHp > 0 && board.getOpposite(to) == VISITED) {
            return false;
        }
        if (tile == PAW_RIGHT) {
            return dir == Direction.RIGHT;
        }
        if (tile == PAW_LEFT) {
            return dir == Direction.LEFT;
        }
        if (ps.bossHp > 0 && tile == EXIT) {
            return false;
        }
        if (ps.xp < requiredXp && to.equals(exit)) {
            return false;
        }
        if (tile == BOMBABLE_WALL) {
            return false;
        }
        return true;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }
}
