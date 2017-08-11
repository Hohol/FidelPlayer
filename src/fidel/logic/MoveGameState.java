package fidel.logic;

import fidel.common.Board;
import fidel.common.Cell;

class MoveGameState {
    final Board board;
    final PlayerState ps;
    final Cell cur;
    final int round;

    public MoveGameState(Board board, Cell cur, PlayerState ps, int round) {
        this.board = board;
        this.ps = ps;
        this.cur = cur;
        this.round = round;
    }

    @Override
    public String toString() {
        return "MoveGameState{" +
                "board=" + board +
                ", ps=" + ps +
                ", cur=" + cur +
                ", round=" + round +
                '}';
    }
}
