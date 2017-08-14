package fidel;

import com.google.common.collect.ImmutableMap;
import fidel.common.*;
import fidel.interaction.GameStateReader;
import fidel.interaction.MoveMaker;
import fidel.logic.BestMoveFinder;

import java.util.*;
import java.util.stream.Collectors;

import static fidel.common.Command.*;
import static fidel.common.GameState.UNKNOWN_EGG_TIMING;
import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.fail;
import static fidel.interaction.ExceptionHelper.tryy;

public class Main {

    static boolean shouldFinishLevel = false;
    static final GameStateReader gameStateReader = new GameStateReader();
    static final GameParameters gameParameters = new GameParameters();
    static final MoveMaker moveMaker = new MoveMaker();

    public static void main(String[] args) {
        while (true) {
            GameState gameState = readGameState();

            List<Command> bestMoves = BestMoveFinder.findBestMoves(gameState, gameParameters);

            if (shouldFinishLevel) {
                bestMoves = append(bestMoves, ENTER);
            }

            System.out.println(bestMoves);

            moveMaker.makeMoves(bestMoves, gameState);
            if (shouldFinishLevel) {
                tryy(() -> Thread.sleep(1100));
            } else {
                break;
            }
        }
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

        System.out.println(gameState);

        investigateChests(gameState);
        gameState.eggTiming = getEggTiming(gameState);

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
            System.out.println(gameState);
            moveMaker.undo(moves);
        }
    }

    private static Map<Cell, Integer> getEggTiming(GameState gameState) {
        Board board = gameState.board;
        if (!board.contains(EGG)) {
            return Collections.emptyMap();
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
//                .put(new Cell(1, 3), 12)
                .build();

        List<Command> moves = BestMoveFinder.investigateEggsMoves(gameState, gameParameters);
        int movesMade = moveMaker.makeMoves(moves, gameState,
                round -> {
                    List<Cell> unknownCells = eggTiming.entrySet().stream()
                            .filter(e -> e.getValue() == UNKNOWN_EGG_TIMING)
                            .map(e -> e.getKey())
                            .collect(Collectors.toList());
                    if (!unknownCells.isEmpty()) {
                        tryy(() -> Thread.sleep(100));
                        for (Cell cell : unknownCells) {
                            TileType tile = gameStateReader.eggOrSnake(cell, board.height == 3);

                            Integer expectedRound = expected.get(cell);
                            if (expectedRound != null && (round < expectedRound && tile == SNAKE || round >= expectedRound && tile == EGG)) {
                                fail();
                            }

                            if (tile == SNAKE) {
                                eggTiming.put(cell, round);
                            }
                        }
                    }
                    boolean ready = eggTiming.values().stream().allMatch(v -> v != UNKNOWN_EGG_TIMING);
                    return !ready;
                }
        );
        moveMaker.undo(moves.subList(0, movesMade));
        eggTiming.keySet().forEach(cell -> board.setInPlace(cell, EGG));
        System.out.println(eggTiming);
        return eggTiming;
    }
}
