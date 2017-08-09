package fidel;

public class GameState {
    public final Board board;
    public final int maxHp;
    public final LevelType levelType;

    public GameState(Board board, int maxHp, LevelType levelType) {
        this.board = board;
        this.maxHp = maxHp;
        this.levelType = levelType;
    }

    public void swapGatesInPlace() {
        board.swapGatesInPlace();
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + board +
                ", maxHp=" + maxHp +
                ", levelType=" + levelType +
                '}';
    }
}
