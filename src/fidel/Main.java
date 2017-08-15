package fidel;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import fidel.common.*;
import fidel.interaction.GameStateReader;
import fidel.interaction.MoveMaker;
import fidel.logic.BestMoveFinder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fidel.common.Command.BARK;
import static fidel.common.Command.ENTER;
import static fidel.common.GameState.UNKNOWN_EGG_TIMING;
import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.fail;
import static fidel.interaction.ExceptionHelper.tryy;

public class Main {

    static boolean shouldFinishLevel = true;
    static final GameStateReader gameStateReader = new GameStateReader();
    static final GameParameters gameParameters = new GameParameters();
    static final MoveMaker moveMaker = new MoveMaker();

    public static void main(String[] args) {
        Stopwatch totalStopwatch = Stopwatch.createStarted();
        List<PerformanceStats> performanceStats = new ArrayList<>();
        int levelIndex = 0;
        while (true) {
            levelIndex++;
            Stopwatch levelStopwatch = Stopwatch.createStarted();
            GameState gameState = trackTime("reading", () -> readGameState());

            List<Command> bestMoves = trackTime("calculating", () -> findBestMoves(gameState));

            //System.out.println(bestMoves);

            trackTime("moving", () -> moveMaker.makeMoves(bestMoves, gameState));

            boolean proceedToNextLevel = shouldFinishLevel && gameState.levelType != LevelType.DRAGON;
            if (proceedToNextLevel) {
                tryy(() -> {
                    int sleepTime = gameState.levelType == LevelType.BEFORE_DRAGON ? 11000 : 1100;
                    Thread.sleep(sleepTime);
                    System.out.println("wake up!");
                });
            }
            PerformanceStats ps = new PerformanceStats(levelStopwatch.elapsed(), levelIndex, gameState.levelType);
            performanceStats.add(ps);
            System.out.println(ps);
            if (!proceedToNextLevel) {
                break;
            }
        }
        System.out.println();
        performanceStats.stream()
                .sorted(Comparator.comparing(s -> s.duration))
                .forEach(System.out::println);
        System.out.println();
        System.out.println("Total time: " + totalStopwatch);
    }

    private static List<Command> findBestMoves(GameState gameState) {
        List<Command> bestMoves = BestMoveFinder.findBestMoves(gameState, gameParameters);
        if (shouldFinishLevel) {
            bestMoves = append(bestMoves, ENTER);
        }
        return bestMoves;
    }

    private static <T> T trackTime(String msg, Supplier<T> f) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        T result = f.get();
        System.out.println(msg + ": " + stopwatch);
        return result;
    }

    private static List<Command> append(List<Command> a, Command command) {
        List<Command> r = new ArrayList<>(a);
        r.add(command);
        return r;
    }

    private static GameState readGameState() {
        GameState gameState = gameStateReader.readGameState();
        if (gameState.board.contains(CHEST) && gameState.board.contains(EGG)) {
            fail("chests and eggs in one level is not supported");
        }



        investigateChests(gameState);
        investigateEggTimings(gameState);

        System.out.println(gameState);

        return gameState;
    }

    private static void investigateChests(GameState gameState) {
        while (gameState.board.contains(CHEST)) {
            Cell chestCell = gameState.board.find(CHEST);
            List<Command> moves = BestMoveFinder.findInvestigateChestMoves(gameState, gameParameters, chestCell);
            moves = append(moves, BARK);
            moveMaker.makeMoves(moves, gameState);
            TileType tile = gameStateReader.readTile(chestCell);
            gameState.board.setInPlace(chestCell, tile);
            //System.out.println(gameState);
            moveMaker.undo(moves);
        }
    }

    private static void investigateEggTimings(GameState gameState) {
        Board board = gameState.board;
        if (!board.contains(EGG)) {
            return;
        }
        Map<Cell, Integer> eggTiming = new HashMap<>();
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
                if (board.get(i, j) == EGG) {
                    board.setInPlace(i, j, WALL);
                    eggTiming.put(new Cell(i, j), UNKNOWN_EGG_TIMING);
                }
            }
        }

        Map<Cell, Integer> expected = ImmutableMap.<Cell, Integer>builder()
//                .put(new Cell(5, 7), 18)
                .build();

        while (eggTiming.values().stream().anyMatch(v -> v == UNKNOWN_EGG_TIMING)) {
            List<Command> moves = BestMoveFinder.investigateEggsMoves(gameState, gameParameters);
            AtomicReference<Boolean> found = new AtomicReference<>(false);
            int movesMade = moveMaker.makeMoves(moves, gameState,
                    round -> {
                        //noinspection ConstantConditions
                        if (round % 3 != 0 || round <= eggTiming.values().stream().max(Comparator.naturalOrder()).get()) {
                            return true;
                        }
                        List<Cell> unknownCells = eggTiming.entrySet().stream()
                                .filter(e -> e.getValue() == UNKNOWN_EGG_TIMING)
                                .map(e -> e.getKey())
                                .collect(Collectors.toList());
                        if (!unknownCells.isEmpty()) {
                            tryy(() -> Thread.sleep(300));
                            for (Cell cell : unknownCells) {
                                TileType tile = gameStateReader.eggOrSnake(cell, board.height == 3);

                                Integer expectedRound = expected.get(cell);
                                if (expectedRound != null && (round < expectedRound && tile == SNAKE || round >= expectedRound && tile == EGG)) {
                                    fail();
                                }

                                if (tile == SNAKE) {
                                    eggTiming.put(cell, round);
                                    found.set(true);
                                }
                            }
                        }
                        boolean ready = eggTiming.values().stream().allMatch(v -> v != UNKNOWN_EGG_TIMING);
                        return !ready;
                    }
            );
            moveMaker.undo(moves.subList(0, movesMade));
            //System.out.println(eggTiming);
            eggTiming.forEach((cell, round) -> {
                if (round != UNKNOWN_EGG_TIMING) {
                    board.setInPlace(cell, EGG);
                }
            });
            gameState.eggTiming = new HashMap<>(eggTiming);
            if (!found.get()) {
                gameState.eggTiming.keySet().forEach(c -> board.setInPlace(c, EGG));
                break;
            }
        }
    }
}
