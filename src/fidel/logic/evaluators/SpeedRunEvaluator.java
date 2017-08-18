package fidel.logic.evaluators;

import java.util.List;

import fidel.common.Cell;
import fidel.common.Command;
import fidel.common.LevelType;
import fidel.logic.MoveGameState;
import fidel.logic.PlayerState;

public class SpeedRunEvaluator implements Evaluator {
    private final int levelIndex;
    private final LevelType levelType;

    public SpeedRunEvaluator(int levelIndex, LevelType levelType) {
        this.levelType = levelType;
        this.levelIndex = levelIndex;
    }

    @Override
    public double evaluate(MoveGameState state, List<Command> moves) {
        return evaluate(moves.size(), state.ps.xp);
    }

    double evaluate(int moveCnt, int xp) {
        double r = 0;

        if (levelIndex == 2) {
            if (xp < 30) {
                r -= 100500;
                r += xp * 100;
            }
        }
        if (levelIndex == 3) {
            if (xp < 40) {
                r -= 100500;
                r += xp * 100;
            }
        }
        if (levelType == LevelType.BEFORE_ALIEN) { // lvl 4
            if (xp < 58) {
                r -= 200500;
                r += xp;
            } else if (xp < 60) {
                r += 1000;
                r -= xp;
            } else {
                r -= 100500;
            }
        }

        r -= moveCnt;
        return r;
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
        return levelType == LevelType.DRAGON;
    }
}
