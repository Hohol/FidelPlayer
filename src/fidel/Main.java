package fidel;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        MoveMaker moveMaker = new MoveMaker();

        GameStateReader gameStateReader = new GameStateReader();

        GameState gameState = gameStateReader.readGameState();

        System.out.println(gameState);

        List<Command> bestMoves = BestMoveFinder.findBestMoves(gameState);

        System.out.println(bestMoves);

        moveMaker.makeMoves(bestMoves);
    }
}
