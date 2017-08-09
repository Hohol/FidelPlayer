package fidel.logic;

import fidel.common.Board;

class MoveGameState {
    final Board board;
    final PlayerState ps;

    public MoveGameState(Board board, PlayerState ps) {
        this.board = board;
        this.ps = ps;
    }
}
