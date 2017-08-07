package fidel;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        GameStateReader gameStateReader = new GameStateReader();
        MoveMaker moveMaker = new MoveMaker();
        BestMoveFinder bestMoveFinder = new BestMoveFinder();

        GameState gameState = gameStateReader.readGameState();

        System.out.println(gameState);

        List<Command> bestMoves = bestMoveFinder.findBestMoves(gameState);

        moveMaker.makeMoves(bestMoves);
    }
}
