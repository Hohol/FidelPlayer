package fidel.logic;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.GameParameters;
import fidel.common.GameState;

public class MoveGameState {
    public final Board board;
    public final PlayerState ps;
    public final Cell cur;
    public final int round;

    public MoveGameState(Board board, Cell cur, PlayerState ps, int round) {
        this.board = board;
        this.ps = ps;
        this.cur = cur;
        this.round = round;
    }

    public MoveGameState(GameState gameState, GameParameters gameParameters) {
        this(
                gameState.board,
                gameState.board.findEntrance(),
                new PlayerState(
                        gameState.gold, gameState.xp, 0, false, gameState.maxHp, 0,
                        gameState.maxHp, false, 0, 3,
                        Simulator.getInitialBossHp(gameState.levelType, gameParameters), false
                ),
                1
        );
    }

    public MoveGameState swapGates() {
        return new MoveGameState(board.swapGates(), board.findExit(), ps, round);
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
