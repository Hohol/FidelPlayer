package fidel.logic;

import fidel.common.Board;
import fidel.common.Cell;

class MoveGameState {
    final Board board;
    final PlayerState ps;
    final Cell cur;

    public MoveGameState(Board board, PlayerState ps, Cell cur) {
        this.board = board;
        this.ps = ps;
        this.cur = cur;
    }
}
