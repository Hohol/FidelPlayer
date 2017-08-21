package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.Command;
import fidel.logic.MoveGameState;

public interface Evaluator {
    double evaluate(MoveGameState state, List<Command> moves);

    boolean finished(MoveGameState gameState, Cell exit);

    boolean updateOnEachMove();

    boolean returnImmediately();

    Cell getExit(Board board);
}
