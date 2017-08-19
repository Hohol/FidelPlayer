package fidel.logic.evaluators;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.Command;
import fidel.common.Utils;
import fidel.logic.MoveGameState;

import java.util.List;

public class InvestigateChestEvaluator implements Evaluator {
    private final Cell chestCell;

    public InvestigateChestEvaluator(Cell chestCell) {
        this.chestCell = chestCell;
    }

    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        return -moves.size();
    }

    @Override
    public boolean finished(MoveGameState gameState, Cell exit) {
        return Utils.dist(gameState.cur, chestCell) == 1;
    }

    @Override
    public boolean updateOnEachMove() {
        return false;
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
