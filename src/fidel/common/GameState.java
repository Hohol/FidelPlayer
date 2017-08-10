package fidel.common;

public class GameState {
    public final Board board;
    public final int maxHp;
    public final LevelType levelType;

    public GameState(Board board, int maxHp, LevelType levelType) {
        this.board = board;
        this.maxHp = maxHp;
        this.levelType = levelType;
    }

    public GameState swapGates() {
        return new GameState(board.swapGates(), maxHp, levelType);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + board +
                ", maxHp=" + maxHp +
                ", levelType=" + levelType +
                '}';
    }

    public static GameState intermission(LevelType intermissionLevelType) {
        return new GameState(null, 0, intermissionLevelType);
    }
}
