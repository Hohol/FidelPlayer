package fidel.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.Command;
import fidel.common.Direction;
import fidel.common.GameParameters;
import fidel.common.GameState;
import fidel.common.LevelType;
import fidel.common.TileType;
import static fidel.common.Command.*;
import static fidel.common.Direction.DIRS;
import static fidel.common.TileType.*;

public class BestMoveFinder {

    final Cell exit;
    final LevelType levelType;
    final GameParameters gameParameters;
    final int maxHp;
    final Simulator simulator;

    List<Command> bestMoves = null;
    double bestEvaluation = Double.NEGATIVE_INFINITY;
    PlayerState bestState = null;
    final List<Command> curMoves = new ArrayList<>();
    long start;

    public BestMoveFinder(GameState gameState, GameParameters gameParameters) {
        this.gameParameters = gameParameters;
        exit = gameState.board.findExit();
        levelType = gameState.levelType;
        maxHp = gameState.maxHp;
        simulator = new Simulator(levelType, exit);
    }

    public static List<Command> findBestMoves(GameState gameState, GameParameters gameParameters) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        MovesAndEvaluation first = new BestMoveFinder(gameState, gameParameters).findBestMoves0(gameState);
        gameState.swapGatesInPlace();
        MovesAndEvaluation second = new BestMoveFinder(gameState, gameParameters).findBestMoves0(gameState);
        if (first.evaluation >= second.evaluation) {
            return first.moves;
        } else {
            List<Command> r = new ArrayList<>();
            r.add(ENTER);
            r.addAll(second.moves);
            return r;
        }
    }

    private MovesAndEvaluation findBestMoves0(GameState gameState) {
        start = System.currentTimeMillis();
        MoveGameState moveGameState = new MoveGameState(
                gameState.board,
                new PlayerState(0, 0, 0, false, maxHp, 0,
                        maxHp, false, 0, 3, getInitialBossHp(levelType))
        );
        try {
            dfs(moveGameState, gameState.board.findEntrance(), 1);
        } catch (TimeoutException e) {
            System.out.println("timeout");
        }
        System.out.println(bestState);
        return new MovesAndEvaluation(bestMoves, evaluate(bestState, bestMoves));
    }

    private int getInitialBossHp(LevelType levelType) {
        if (levelType == LevelType.ALIENS) {
            return gameParameters.alienBossHp;
        }
        if (levelType == LevelType.ROBODOG) {
            return gameParameters.robodogMaxHp;
        }
        return 0;
    }

    private void dfs(MoveGameState gameState, Cell cur, int round) {
        PlayerState ps = gameState.ps;
        Board board = gameState.board;
        if (ps.hp < 0) {
            return;
        }
        if (!exitReachable(board, cur, exit)) {
            return;
        }
        if (finished(cur, ps)) {
            double evaluation = evaluate(ps, curMoves);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestState = ps;
                bestMoves = new ArrayList<>(curMoves);
                System.out.println("cur best " + ps);
            }
            return;
        }
        if (bestMoves != null && tooLate()) {
            throw new TimeoutException();
        }
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!board.inside(to)) {
                continue;
            }
            if (!passableNow(board.get(to), ps)) {
                continue;
            }

            MoveGameState newGameState = simulator.simulateMove(board, ps, round, dir, to);

            curMoves.add(dir.command);
            dfs(newGameState, to, round + 1);
            pop(curMoves);
        }

        MoveGameState afterBarking = simulator.simulateBark(board, cur, ps);
        if (afterBarking != null) {
            curMoves.add(BARK);
            dfs(afterBarking, cur,round + 1);
            pop(curMoves);
        }

    }

    private boolean finished(Cell cur, PlayerState ps) {
        if (ps.bossHp > 0) {
            return false;
        }
        return cur.equals(exit);
    }

    private boolean tooLate() {
        //return false;
        return System.currentTimeMillis() - start > 15000;
    }

    private static boolean exitReachable(Board board, Cell cur, Cell exit) {
        boolean[][] visited = new boolean[board.height][board.width]; // todo get rid of it (make thread local?)
        return dfs(board, cur, visited, exit);
    }

    private static boolean dfs(Board board, Cell cur, boolean[][] visited, Cell exit) {
        if (cur.equals(exit)) {
            return true;
        }
        visited[cur.row][cur.col] = true;
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!board.inside(to)) {
                continue;
            }
            if (!potentiallyPassable(board.get(to))) {
                continue;
            }
            if (visited[to.row][to.col]) {
                continue;
            }
            if (dfs(board, to, visited, exit)) {
                return true;
            }
        }
        return false;
    }


    private static boolean potentiallyPassable(TileType tile) {
        return tile != ENTRANCE && tile != VISITED && tile != CHEST && tile != WALL && tile != GNOME;
    }

    private static boolean passableNow(TileType tile, PlayerState ps) {
        if (!potentiallyPassable(tile)) {
            return false;
        }
        if (tile == RAISED_WALL) {
            return ps.buttonsPressed % 2 == 1;
        }
        if (tile == LOWERED_WALL) {
            return ps.buttonsPressed % 2 == 0;
        }
        return true;
    }

    private static double evaluate(PlayerState ps, List<Command> moves) {
        if (ps == null) {
            return Integer.MIN_VALUE;
        }
        return ps.gold * 10 + ps.xp - moves.size() / 1000.0;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }

}
