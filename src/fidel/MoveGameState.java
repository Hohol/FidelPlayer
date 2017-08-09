package fidel;

public class MoveGameState {
    final Board board;
    final PlayerState ps;

    public MoveGameState(Board board, PlayerState ps) {
        this.board = board;
        this.ps = ps;
    }
}
