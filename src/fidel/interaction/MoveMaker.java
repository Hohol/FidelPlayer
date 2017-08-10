package fidel.interaction;

import java.awt.*;
import java.util.List;

import fidel.common.Command;
import static fidel.interaction.ExceptionHelper.*;

public class MoveMaker {

    private final Robot robot = tryy(() -> new Robot());

    public void makeMoves(List<Command> commands) {
        for (Command command : commands) {
            makeMove(command);
        }
    }

    private void makeMove(Command command) {
        robot.keyPress(command.keyCode);
        int sleepTime = 200;
        tryy(() -> Thread.sleep(sleepTime));
        robot.keyRelease(command.keyCode);
        tryy(() -> Thread.sleep(400));
    }
}