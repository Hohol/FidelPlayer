package fidel;

import java.awt.*;
import java.util.List;

import static fidel.Tryy.*;

public class MoveMaker {

    private final Robot robot = tryy(() -> new Robot());

    void makeMoves(List<Command> commands) {
        for (Command command : commands) {
            makeMove(command);
        }
    }

    private void makeMove(Command command) {
        robot.keyPress(command.keyCode);
        int sleepTime = 400;
        tryy(() -> Thread.sleep(sleepTime));
        robot.keyRelease(command.keyCode);
        tryy(() -> Thread.sleep(sleepTime));
    }
}