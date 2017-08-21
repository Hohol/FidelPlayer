package fidel.logic;

import fidel.common.Board;
import fidel.common.Cell;
import fidel.common.GameState;

import static fidel.common.TileType.*;
import static java.lang.Math.*;

public class ChessSimulator {
    private final Cell knightPosition;
    private final Cell bishopPosition;
    private final Cell kingPosition;
    private final int chessCnt;

    public ChessSimulator(GameState gameState) {
        Board board = gameState.board;
        knightPosition = board.find(KNIGHT);
        bishopPosition = board.find(BISHOP);
        kingPosition = board.find(KING);
        chessCnt = board.count(PAWN) + board.count(KNIGHT) + board.count(BISHOP);
    }


    public ChessInfo simulateChess(Board board, Cell cell, int chessKilledCnt) {
        int chessLost = 0;
        if (knightPosition != null && board.get(knightPosition) == KNIGHT) {
            int dRow = abs(cell.row - knightPosition.row);
            int dCol = abs(cell.col - knightPosition.col);
            if (dRow == 1 && dCol == 2 || dRow == 2 && dCol == 1) {
                chessLost++;
                board.setInPlace(knightPosition, EMPTY);
            }
        }

        if (bishopPosition != null && board.get(bishopPosition) == BISHOP) {
            if (checkBishop(board, cell, 1, 1) ||
                    checkBishop(board, cell, 1, -1) ||
                    checkBishop(board, cell, -1, 1) ||
                    checkBishop(board, cell, -1, -1)) {
                board.setInPlace(bishopPosition, EMPTY);
                chessLost++;
            }
        }
        if (checkPawn(board, cell.row + 1, cell.col - 1)) {
            chessLost++;
        }
        if (checkPawn(board, cell.row + 1, cell.col + 1)) {
            chessLost++;
        }
        int addXp = 0;
        if (kingPosition != null && board.get(kingPosition) == KING) {
            if (abs(kingPosition.row - cell.row) <= 1 && abs(kingPosition.col - cell.col) <= 1) {
                board.setInPlace(kingPosition, EMPTY);
            } else {
                if (chessKilledCnt == chessCnt - 1) {
                    addXp = 15; // todo 15?
                    board.setInPlace(kingPosition, EMPTY);
                }
            }
        }

        return new ChessInfo(addXp, chessKilledCnt, chessLost);
    }

    private boolean checkBishop(Board board, Cell cell, int dRow, int dCol) {
        int row = bishopPosition.row + dRow;
        int col = bishopPosition.col + dCol;
        while (board.inside(row, col)) {
            if (row == cell.row && col == cell.col) {
                return true;
            }
            if (board.get(row, col) != EMPTY) {
                return false;
            }
            row += dRow;
            col += dCol;
        }
        return false;
    }

    private boolean checkPawn(Board board, int row, int col) {
        if (!board.inside(row, col)) {
            return false;
        }
        if (board.get(row, col) == PAWN) {
            board.setInPlace(row, col, EMPTY);
            return true;
        }
        return false;
    }

    static class ChessInfo {
        final int addXp;
        final int chessKilledCnt;
        final int chessLost;

        ChessInfo(int addXp, int chessKilledCnt, int chessLost) {
            this.addXp = addXp;
            this.chessKilledCnt = chessKilledCnt;
            this.chessLost = chessLost;
        }
    }
}
