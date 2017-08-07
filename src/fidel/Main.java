package fidel;

import java.util.Arrays;

import static fidel.Command.*;

public class Main {

    public static void main(String[] args) {
        GameStateReader gameStateReader = new GameStateReader();
        MoveMaker moveMaker = new MoveMaker();

        //gameStateReader.readGameState();

        moveMaker.makeMoves(Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, ENTER));
    }
}
