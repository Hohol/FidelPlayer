package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Cell;
import fidel.common.Command;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

public class HighScoreEvaluator implements Evaluator {
    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        PlayerState ps = state.ps;
        return ps.gold * 1.2 + ps.xp - moves.size() / 1000.0;
    }

    @Override
    public boolean finished(Cell cur, PlayerState ps, Cell exit) {
        if (ps.bossHp > 0) {
            return false;
        }
        return cur.equals(exit);
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
