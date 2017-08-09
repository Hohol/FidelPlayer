package fidel;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static fidel.TileType.ENTRANCE;
import static fidel.TileType.EXIT;

public class Board {
    public final int height;
    public final int width;
    public final TileType[][] map;

    public Board(int height, int width) {
        this.height = height;
        this.width = width;
        map = new TileType[height][width];
    }

    public Board(Board board) {
        height = board.height;
        width = board.width;
        map = new TileType[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setInPlace(i, j, board.get(i, j));
            }
        }
    }

    public Board(TileType[][] tileTypes) {
        map = tileTypes;
        height = map.length;
        width = map[0].length;
    }

    public void setInPlace(int row, int col, TileType tile) {
        map[row][col] = tile;
    }

    public Board setAndCopy(int row, int col, TileType tile) {
        Board newBoard = new Board(this);
        newBoard.setInPlace(row, col, tile);
        return newBoard;
    }

    public Board setAndCopy(Cell cell, TileType tile) {
        return setAndCopy(cell.row, cell.col, tile);
    }

    public Cell findEntrance() {
        return find(ENTRANCE);
    }

    public Cell findExit() {
        return find(EXIT);
    }

    public Cell find(TileType tileType) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] == tileType) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }

    public TileType get(Cell cell) {
        return get(cell.row, cell.col);
    }

    public TileType get(int row, int col) {
        return map[row][col];
    }

    public boolean inside(Cell to) {
        return inside(to.row, to.col);
    }

    private boolean inside(int row, int col) {
        return row >= 0 && col >= 0 && row < height && col < width;
    }

    public void setInPlace(Cell to, TileType tileType) {
        setInPlace(to.row, to.col, tileType);
    }

    public void swapInPlace() {
        Cell entrance = findEntrance();
        Cell exit = findExit();
        setInPlace(entrance, EXIT);
        setInPlace(exit, ENTRANCE);
    }

    @Override
    public String toString() {
        //noinspection ConstantConditions
        int[] colWidth = IntStream.range(0, width).map(col ->
                IntStream.range(0, height).map(row -> map[row][col].name().length()).max().getAsInt()
        ).toArray();
        return Stream.of(map)
                .map(row -> "{"
                        + IntStream.range(0, width).mapToObj(col -> padRight(row[col].name(), colWidth[col])).collect(Collectors.joining(", "))
                        + "}"
                )
                .collect(Collectors.joining(",\n"));
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}
