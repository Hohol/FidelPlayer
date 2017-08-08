package fidel;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        GameStateReader gameStateReader = new GameStateReader();
        MoveMaker moveMaker = new MoveMaker();

        GameState gameState = gameStateReader.readGameState();

        System.out.println(gameState);

        List<Command> bestMoves = BestMoveFinder.findBestMoves(gameState);

        System.out.println(bestMoves);

        moveMaker.makeMoves(bestMoves);
    }
}
