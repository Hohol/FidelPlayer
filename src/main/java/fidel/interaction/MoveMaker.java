package fidel.interaction;

import fidel.common.*;
import fidel.logic.MoveGameState;
import fidel.logic.Simulator;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fidel.common.Command.*;
import static fidel.interaction.ExceptionHelper.tryy;

public class MoveMaker {

    public static final int PRESS_TIME = 400;
    private final Robot robot = tryy(() -> new Robot());

    public int makeMoves(List<Command> commands, GameState gameState) {
        return makeMoves(commands, gameState, null);
    }

    /**
     * @return number of moves made
     */
    public int makeMoves(List<Command> commands, GameState gameState, Function<Integer, Boolean> onMoveMade) {
        GameParameters gameParameters = new GameParameters();
        Simulator simulator = new Simulator(gameState, gameParameters);
        MoveGameState state = new MoveGameState(gameState, gameParameters);

        boolean intermission = gameState.levelType == LevelType.INTERMISSION1 || gameState.levelType == LevelType.INTERMISSION2;

        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);
            robot.keyPress(command.keyCode);
            tryy(() -> Thread.sleep(PRESS_TIME));
            robot.keyRelease(command.keyCode);

            if (!intermission && (onMoveMade != null || i != commands.size() - 1)) {
                MoveGameState nextState = simulator.simulate(command, state);
                System.out.println(command);
                System.out.println(nextState.round + " " + nextState.ps.xp + " " + nextState.ps.hp);
                System.out.println(nextState.board.find(TileType.GNOME));
                if (nextState.round != state.round && onMoveMade != null) {
                    boolean shouldContinue = onMoveMade.apply(state.round);
                    if (!shouldContinue) {
                        return i + 1;
                    }
                }
                int sleepTime;
                if (nextState.ps.maxHp == state.ps.maxHp)
                    sleepTime = 0;
                else
                    sleepTime = 600;
                tryy(() -> Thread.sleep(sleepTime));
                state = nextState;
            }
        }
        return commands.size();
    }

    public void undo(List<Command> moves) {
        List<Command> undoMoves = moves.stream()
                .filter(m -> m == RIGHT || m == DOWN || m == LEFT || m == UP || m == ENTER)
                .map(m -> m == ENTER ? ENTER : UNDO)
                .collect(Collectors.toList());
        Collections.reverse(undoMoves);

        for (Command command : undoMoves) {
            robot.keyPress(command.keyCode);
            tryy(() -> Thread.sleep(80));
            robot.keyRelease(command.keyCode);
        }
    }
}