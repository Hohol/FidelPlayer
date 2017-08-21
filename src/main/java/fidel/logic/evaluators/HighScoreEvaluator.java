package fidel.logic.evaluators;

import fidel.common.*;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

import java.util.List;

import static fidel.common.TileType.*;

public class HighScoreEvaluator implements Evaluator {
    final int levelIndex;
    final LevelType levelType;
    final Cell bombableWallPosition;
    final boolean shouldUsePortal;

    public HighScoreEvaluator(int levelIndex, LevelType levelType, Cell bombableWallPosition, boolean shouldUsePortal) {
        this.levelIndex = levelIndex;
        this.levelType = levelType;
        this.bombableWallPosition = bombableWallPosition;
        this.shouldUsePortal = shouldUsePortal;
    }

    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        PlayerState ps = state.ps;
        double r = 0;
        if (levelIndex < 4) {
            if (ps.gold < 4) {
                r -= 100500;
                r += ps.gold * 100;
            }
        }
        r += ps.gold * 1.2;
        r += ps.xp - moves.size() / 1000.0;
        return r;
    }

    @Override
    public boolean finished(MoveGameState gameState, Cell exit) {
        if (shouldUsePortal) {
            TileType tile = gameState.board.get(bombableWallPosition);
            if (tile != EMPTY && tile != VISITED) {
                return false;
            }
        }
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
        if (shouldUsePortal) {
            return board.find(PORTAL);
        } else {
            return board.findExit();
        }
    }
}
