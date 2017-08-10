package fidel.logic;

import java.util.List;

import fidel.common.Cell;
import fidel.common.Command;

public interface Evaluator {
    double evaluate(PlayerState ps, List<Command> moves);
    boolean finished(Cell cur, PlayerState ps, Cell exit);
}
