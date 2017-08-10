package fidel.logic;

import fidel.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static fidel.common.Command.*;
import static fidel.common.Direction.DIRS;
import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.tryy;
import static java.lang.Math.*;

public class BestMoveFinder {

    final Cell exit;
    final LevelType levelType;
    final GameParameters gameParameters;
    final Simulator simulator;
    final Evaluator evaluator;

    List<Command> bestMoves = null;
    double bestEvaluation = Double.NEGATIVE_INFINITY;
    PlayerState bestState = null;
    final List<Command> curMoves = new ArrayList<>();
    long start;
    final int[][] visited;
    int curVisited;

    public BestMoveFinder(GameState gameState, GameParameters gameParameters, Evaluator evaluator) {
        this.gameParameters = gameParameters;
        exit = gameState.board.findExit();
        levelType = gameState.levelType;
        simulator = new Simulator(levelType, exit, gameParameters);
        visited = new int[gameState.board.height][gameState.board.width];
        this.evaluator = evaluator;
    }

    public static List<Command> findBestMoves(GameState gameState, GameParameters gameParameters) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        if (gameState.levelType == LevelType.INTERMISSION2) {
            return Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT);
        }
        Evaluator standardEvaluator = new Evaluator() {
            @Override
            public double evaluate(PlayerState ps, List<Command> moves) {
                if (ps == null) {
                    return Double.NEGATIVE_INFINITY;
                }
                return ps.gold * 10 + ps.xp - moves.size() / 1000.0;
            }

            @Override
            public boolean finished(Cell cur, PlayerState ps, Cell exit) {
                if (ps.bossHp > 0) {
                    return false;
                }
                return cur.equals(exit);
            }
        };
        return findBestMovesTmp(gameState, gameParameters, standardEvaluator);
    }

    public static List<Command> findInvestigateChestMoves(GameState gameState, GameParameters gameParameters, Cell chestCell) {
        Evaluator evaluator = new Evaluator() {
            @Override
            public double evaluate(PlayerState ps, List<Command> moves) {
                return -moves.size();
            }

            @Override
            public boolean finished(Cell cur, PlayerState ps, Cell exit) {
                return dist(cur, chestCell) == 1;
            }
        };
        return findBestMovesTmp(gameState, gameParameters, evaluator);
    }

    private static int dist(Cell a, Cell b) {
        return abs(a.row - b.row) + abs(a.col - b.col);
    }

    private static List<Command> findBestMovesTmp(GameState gameState, GameParameters gameParameters, Evaluator evaluator) {
        GameState secondGameState = gameState.swapGates();
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<MovesAndEvaluation> firstFuture
                = executor.submit(() -> new BestMoveFinder(gameState, gameParameters, evaluator).findBestMoves0(gameState));
        Future<MovesAndEvaluation> secondFuture
                = executor.submit(() -> new BestMoveFinder(secondGameState, gameParameters, evaluator).findBestMoves0(secondGameState));

        MovesAndEvaluation first = tryy(() -> firstFuture.get());
        MovesAndEvaluation second = tryy(() -> secondFuture.get());

        if (first.evaluation >= second.evaluation) {
            if (first.moves == null) {
                throw new RuntimeException("no path found");
            }
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
                new PlayerState(0, 0, 0, false, gameState.maxHp, 0,
                        gameState.maxHp, false, 0, 3, simulator.getInitialBossHp(levelType))
        );
        try {
            dfs(moveGameState, gameState.board.findEntrance(), 1);
        } catch (TimeoutException e) {
            System.out.println("timeout");
        }
        System.out.println(bestState);
        return new MovesAndEvaluation(bestMoves, evaluator.evaluate(bestState, bestMoves));
    }

    private void dfs(MoveGameState gameState, Cell cur, int round) {
        PlayerState ps = gameState.ps;
        Board board = gameState.board;
        if (ps.hp < 0) {
            return;
        }
        if (round % 3 == 0 && !exitReachable(board, cur, exit)) {
            return;
        }
        if (evaluator.finished(cur, ps, exit)) {
            double evaluation = evaluator.evaluate(ps, curMoves);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestState = ps;
                bestMoves = new ArrayList<>(curMoves);
                //System.out.println("cur best " + ps);
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
            if (!passableNow(ps, board, to)) {
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
            dfs(afterBarking, cur, round + 1);
            pop(curMoves);
        }

    }

    private boolean tooLate() {
//        return false;
        return System.currentTimeMillis() - start > 10000;
    }

    private boolean exitReachable(Board board, Cell cur, Cell exit) {
        curVisited++;
        return dfsCheckPath(board, cur, exit);
    }

    private boolean dfsCheckPath(Board board, Cell cur, Cell exit) {
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
            if (dfsCheckPath(board, to, exit)) {
                return true;
            }
        }
        return false;
    }


    private static boolean potentiallyPassable(TileType tile) {
        return tile != ENTRANCE && tile != VISITED && tile != CHEST && tile != WALL && tile != GNOME && tile != EGG;
    }

    private boolean passableNow(PlayerState ps, Board board, Cell to) {
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
        return true;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }
}
