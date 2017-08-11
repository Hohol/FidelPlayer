package fidel.logic;

import fidel.common.Board;
import fidel.common.Cell;

class MoveGameState {
    final Board board;
    final PlayerState ps;
    final Cell cur;

    public MoveGameState(Board board, Cell cur, PlayerState ps) {
        this.board = board;
        this.ps = ps;
        this.cur = cur;
    }

    @Override
    public String toString() {
        return "MoveGameState{" +
                "board=" + board +
                ", ps=" + ps +
                ", cur=" + cur +
                '}';
    }
}
