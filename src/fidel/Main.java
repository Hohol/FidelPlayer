package fidel;

import java.util.Arrays;
import java.util.List;

import static fidel.Command.*;

public class Main {

    public static void main(String[] args) {
        GameStateReader gameStateReader = new GameStateReader();
        MoveMaker moveMaker = new MoveMaker();
        BestMoveFinder bestMoveFinder = new BestMoveFinder();

        GameState gameState = gameStateReader.readGameState();
        List<Command> bestMoves = bestMoveFinder.findBestMoves(gameState);

        moveMaker.makeMoves(bestMoves);
    }
}
