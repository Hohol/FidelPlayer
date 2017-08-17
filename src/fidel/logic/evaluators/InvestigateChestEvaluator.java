package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Cell;
import fidel.common.Command;
import fidel.common.Utils;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

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
    public boolean finished(Cell cur, PlayerState ps, Cell exit) {
        return Utils.dist(cur, chestCell) == 1;
    }

    @Override
    public boolean updateOnEachMove() {
        return false;
    }

    @Override
    public boolean returnImmediately() {
        return false;
    }
}
