package fidel.logic.evaluators;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.Command;
import fidel.logic.MoveGameState;

import java.util.List;

public class InvestigateEggsEvaluator implements Evaluator {
    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        return state.round - moves.size() / 1000.0;
    }

    @Override
    public boolean finished(MoveGameState gameState, Cell exit) {
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

    @Override
    public Cell getExit(Board board) {
        return null;
    }
}
