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
import static fidel.interaction.ExceptionHelper.tryy;

public class BMF {
    public static List<Command> findHighScoreMoves(GameState gameState, GameParameters gameParameters) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        if (gameState.levelType == LevelType.INTERMISSION2) {
            return Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT);
        }
        return findBestMoves(gameState, gameParameters, new HighScoreEvaluator());
    }

    public static List<Command> findSpeedRunMoves(GameState gameState, GameParameters gameParameters, int levelIndex) {
        if (gameState.levelType == LevelType.INTERMISSION1) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        if (gameState.levelType == LevelType.INTERMISSION2) {
            return Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT);
        }
        return findBestMoves(gameState, gameParameters, new SpeedRunEvaluator(levelIndex, gameState.levelType));
    }

    public static List<Command> findInvestigateChestMoves(GameState gameState, GameParameters gameParameters, Cell chestCell) {
        return findBestMoves(gameState, gameParameters, new InvestigateChestEvaluator(chestCell));
    }

    public static List<Command> investigateEggsMoves(GameState gameState, GameParameters gameParameters) {
        return findBestMoves(gameState, gameParameters, new InvestigateEggsEvaluator());
    }

    static List<Command> findBestMoves(GameState gameState, GameParameters gameParameters, Evaluator evaluator) {
        GameState secondGameState = gameState.swapGates();
        ExecutorService executor = Executors.newCachedThreadPool();

        Callable<MovesAndEvaluation> calcFirst = () -> new BestMoveFinder(gameState, gameParameters, evaluator).findBestMoves(gameState, false);
        Callable<MovesAndEvaluation> calcSecond = () -> new BestMoveFinder(secondGameState, gameParameters, evaluator).findBestMoves(secondGameState, true);

        List<Command> result;
        if (evaluator.returnImmediately()) {
            MovesAndEvaluation r = tryy(() -> executor.invokeAny(ImmutableList.of(calcFirst, calcSecond)));
            result = r.moves;
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
                result = first.moves;
            } else {
                result = second.moves;
            }
        }
        if (result == null) {
            throw new RuntimeException("no path found");
        }
        executor.shutdown();
        return result;
    }
}
