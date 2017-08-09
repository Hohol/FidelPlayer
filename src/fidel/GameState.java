package fidel;

public class GameState {
    public final Board board;
    public final int maxHp;

    public GameState(Board board, int maxHp) {
        this.board = board;
        this.maxHp = maxHp;
    }

    public void swapInPlace() {
        board.swapInPlace();
    }

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + board +
                ", maxHp=" + maxHp +
                '}';
    }
}
