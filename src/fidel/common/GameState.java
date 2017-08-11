package fidel.common;

public class GameState {
    public final Board board;
    public final int maxHp;
    public final int gold;
    public final LevelType levelType;

    public GameState(Board board, int maxHp, int gold, LevelType levelType) {
        this.board = board;
        this.maxHp = maxHp;
        this.gold = gold;
        this.levelType = levelType;
    }

    public GameState swapGates() {
        return new GameState(board.swapGates(), maxHp, gold, levelType);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + board +
                ", maxHp=" + maxHp +
                ", gold=" + gold +
                ", levelType=" + levelType +
                '}';
    }

    public static GameState intermission(LevelType intermissionLevelType) {
        return new GameState(new Board(0, 0), 0, 0, intermissionLevelType);
    }
}
