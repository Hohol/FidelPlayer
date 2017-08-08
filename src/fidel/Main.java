package fidel;

import java.util.ArrayList;
import java.util.List;

import static fidel.Command.*;
import static fidel.Tryy.*;

public class Main {

    static boolean auto = true;

    public static void main(String[] args) {
        MoveMaker moveMaker = new MoveMaker();

        GameStateReader gameStateReader = new GameStateReader();

        while (true) {
            GameState gameState = gameStateReader.readGameState();

            System.out.println(gameState);

            List<Command> bestMoves = BestMoveFinder.findBestMoves(gameState);

            if (auto) {
                bestMoves = new ArrayList<>(bestMoves);
                bestMoves.add(ENTER);
            }

            System.out.println(bestMoves);

            moveMaker.makeMoves(bestMoves);
            if (auto) {
                tryy(() -> Thread.sleep(1000));
            } else {
                break;
            }
        }
    }
}
