package fidel;

public class Cell {
    public final int row;
    public final int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Cell add(Direction dir) {
        return new Cell(row + dir.dRow, col + dir.dCol);
    }
}
