package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.Command;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

public class SimpleHighScoreEvaluator implements Evaluator {
    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        PlayerState ps = state.ps;
        return ps.gold * 1.2 + ps.xp - moves.size() / 1000.0;
    }

    @Override
    public boolean finished(MoveGameState gameState, Cell exit) {
        return gameState.cur.equals(exit);
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
        return board.findExit();
    }
}
