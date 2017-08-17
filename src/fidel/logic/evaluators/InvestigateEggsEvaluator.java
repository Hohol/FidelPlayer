package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Cell;
import fidel.common.Command;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

public class InvestigateEggsEvaluator implements Evaluator {
    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        return state.round - moves.size() / 1000.0;
    }

    @Override
    public boolean finished(Cell cur, PlayerState ps, Cell exit) {
        return false;
    }

    @Override
    public boolean updateOnEachMove() {
        return true;
    }

    @Override
    public boolean returnImmediately() {
        return false;
    }
}
