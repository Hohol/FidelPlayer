package fidel.interaction;

import java.awt.*;
import java.util.List;

import fidel.common.Command;
import fidel.common.GameParameters;
import fidel.common.GameState;
import fidel.common.LevelType;
import fidel.logic.MoveGameState;
import fidel.logic.Simulator;


import static fidel.interaction.ExceptionHelper.*;

public class MoveMaker {

    private final Robot robot = tryy(() -> new Robot());

    public void makeMoves(List<Command> commands, GameState gameState) {
        GameParameters gameParameters = new GameParameters();
        Simulator simulator = new Simulator(gameState, gameParameters);
        MoveGameState state = new MoveGameState(gameState, gameParameters);

        boolean intermission = gameState.levelType == LevelType.INTERMISSION1 || gameState.levelType == LevelType.INTERMISSION2;

        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);
            robot.keyPress(command.keyCode);
            tryy(() -> Thread.sleep(40));
            robot.keyRelease(command.keyCode);

            if (i != commands.size() - 1 && !intermission) {
                MoveGameState nextState = simulator.simulate(command, state);
                System.out.println(command);
                System.out.println(nextState.ps.xp + " " + nextState.ps.hp);
                int sleepTime;
                if (nextState.ps.maxHp == state.ps.maxHp)
                    sleepTime = 40;
                else
                    sleepTime = 600;
                tryy(() -> Thread.sleep(sleepTime));
                state = nextState;
            }
        }
    }

    public void makeUndoMoves(List<Command> commands) {
        for (Command command : commands) {
            robot.keyPress(command.keyCode);
            tryy(() -> Thread.sleep(40));
            robot.keyRelease(command.keyCode);
            tryy(() -> Thread.sleep(40));
        }
    }
}