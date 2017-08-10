package fidel;

import java.util.*;
import java.util.stream.Collectors;

import fidel.common.*;
import fidel.interaction.GameStateReader;
import fidel.interaction.MoveMaker;
import fidel.logic.BestMoveFinder;

import static fidel.common.Command.*;
import static fidel.common.TileType.CHEST;
import static fidel.interaction.ExceptionHelper.*;

public class Main {

    static boolean shouldFinishLevel = true;
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

            moveMaker.makeMoves(bestMoves);
            if (shouldFinishLevel) {
                tryy(() -> Thread.sleep(1000));
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

        System.out.println(gameState);

        investigateChests(gameState);

        return gameState;
    }

    private static void investigateChests(GameState gameState) {
        while (gameState.board.contains(CHEST)) {
            Cell chestCell = gameState.board.find(CHEST);
            List<Command> moves = BestMoveFinder.findInvestigateChestMoves(gameState, gameParameters, chestCell);
            moves = append(moves, BARK);
            moveMaker.makeMoves(moves);
            GameState tmpGameState = gameStateReader.readGameState();
            TileType tile = tmpGameState.board.get(chestCell);
            gameState.board.setInPlace(chestCell, tile);
            System.out.println(gameState);
            List<Command> undoMoves = moves.stream()
                    .filter(m -> m == RIGHT || m == DOWN || m == LEFT || m == UP || m == ENTER)
                    .map(m -> m == ENTER ? ENTER : UNDO)
                    .collect(Collectors.toList());
            Collections.reverse(undoMoves);
            moveMaker.makeMoves(undoMoves);
        }
    }
}
