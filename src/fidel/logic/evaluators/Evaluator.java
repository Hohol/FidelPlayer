package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Cell;
import fidel.common.Command;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

public interface Evaluator {
    double evaluate(MoveGameState state, List<Command> moves);
    boolean finished(Cell cur, PlayerState ps, Cell exit);
    boolean updateOnEachMove();
    boolean returnImmediately();
}
