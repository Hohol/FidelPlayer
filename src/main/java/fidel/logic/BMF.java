package fidel.logic;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.ImmutableList;

import fidel.common.*;
import fidel.logic.evaluators.*;

import static fidel.common.Command.*;
import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.tryy;

public class BMF {
    private static final int NORMAL_TIMEOUT = 3000;
    private static final int SPEEDRUN_TIMEOUT = 1000;
    private static final int INVESTIGATION_TIMEOUT = 1000;

    public static List<Command> findSimpleHighScoreMoves(GameState gameState, GameParameters gameParameters) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        if (gameState.levelType == LevelType.INTERMISSION2) {
            return Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT);
        }
        return findBestMoves(gameState, gameParameters, new SimpleHighScoreEvaluator(), NORMAL_TIMEOUT);
    }

    public static List<Command> findHighScoreMoves(GameState gameState, GameParameters gameParameters, int levelIndex) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT, UP, RIGHT, RIGHT);
        }
        if (gameState.levelType == LevelType.INTERMISSION2) {
            return Arrays.asList(RIGHT, UP, BARK, UP, UP, UP, BARK, RIGHT, RIGHT, BARK, RIGHT, DOWN, DOWN, BARK, DOWN, LEFT, UP, UP);
        }
        Board board = gameState.board;
        boolean shouldUsePortal = board.contains(PORTAL) && gameState.gold + board.count(COIN) >= 6;
        HighScoreEvaluator evaluator = new HighScoreEvaluator(levelIndex, gameState.levelType, board.find(BOMBABLE_WALL), shouldUsePortal);
        return findBestMoves(gameState, gameParameters, evaluator, NORMAL_TIMEOUT);
    }

    public static List<Command> findSpeedRunMoves(GameState gameState, GameParameters gameParameters, int levelIndex) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        if (gameState.levelType == LevelType.INTERMISSION2) {
            return Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT);
        }
        SpeedRunEvaluator evaluator = new SpeedRunEvaluator(levelIndex, gameState.levelType);
        return findBestMoves(gameState, gameParameters, evaluator, SPEEDRUN_TIMEOUT);
    }

    public static List<Command> findInvestigateChestMoves(GameState gameState, GameParameters gameParameters, Cell chestCell) {
        return findBestMoves(gameState, gameParameters, new InvestigateChestEvaluator(chestCell), INVESTIGATION_TIMEOUT);
    }

    public static List<Command> investigateEggsMoves(GameState gameState, GameParameters gameParameters) {
        return findBestMoves(gameState, gameParameters, new InvestigateEggsEvaluator(), INVESTIGATION_TIMEOUT);
    }

    static List<Command> findBestMoves(GameState gameState, GameParameters gameParameters, Evaluator evaluator, int timeout) {
        GameState secondGameState = gameState.swapGates();
        ExecutorService executor = Executors.newCachedThreadPool();

        Callable<MovesAndEvaluation> calcFirst = () -> new BestMoveFinder(gameState, gameParameters, evaluator, timeout).findBestMoves(gameState, false);
        Callable<MovesAndEvaluation> calcSecond = () -> new BestMoveFinder(secondGameState, gameParameters, evaluator, timeout).findBestMoves(secondGameState, true);

        MovesAndEvaluation result;
        if (evaluator.returnImmediately()) {
            result = tryy(() -> executor.invokeAny(ImmutableList.of(calcFirst, calcSecond)));
        } else {
            Future<MovesAndEvaluation> firstFuture = executor.submit(calcFirst);
            Future<MovesAndEvaluation> secondFuture = executor.submit(calcSecond);
            MovesAndEvaluation first = tryy(() -> firstFuture.get());
            MovesAndEvaluation second = tryy(() -> secondFuture.get());

//            System.out.println(first.moves);
//            System.out.println(first.moveGameState);
//            System.out.println();
//            System.out.println(second.moveGameState);
//            System.out.println(second.moves);

            if (first.evaluation >= second.evaluation) {
                result = first;
            } else {
                result = second;
            }
        }
        if (result == null || result.moves == null) {
            throw new RuntimeException("no path found");
        }
        executor.shutdown();
        System.out.println(result.moveGameState);
        return result.moves;
    }
}
