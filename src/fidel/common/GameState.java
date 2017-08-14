package fidel.common;

import java.util.Collections;
import java.util.Map;

public class GameState {
    public static final int UNKNOWN_EGG_TIMING = -1;

    public final Board board;
    public final int maxHp;
    public final int gold;
    public final int xp;
    public final LevelType levelType;
    public Map<Cell, Integer> eggTiming;

    public GameState(Board board, int maxHp, int gold, int xp, LevelType levelType, Map<Cell, Integer> eggTiming) {
        this.board = board;
        this.maxHp = maxHp;
        this.gold = gold;
        this.xp = xp;
        this.levelType = levelType;
        this.eggTiming = eggTiming;
    }

    public GameState swapGates() {
        return new GameState(board.swapGates(), maxHp, gold, xp, levelType, eggTiming);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + board +
                ", maxHp=" + maxHp +
                ", gold=" + gold +
                ", xp=" + xp +
                ", levelType=" + levelType +
                ", eggTiming=" + eggTiming +
                '}';
    }

    public static GameState intermission(LevelType intermissionLevelType) {
        return new GameState(new Board(0, 0), 2, 0, 0, intermissionLevelType, Collections.emptyMap());
    }
}
