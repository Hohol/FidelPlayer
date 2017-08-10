package fidel.common;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fidel.common.TileType.ENTRANCE;
import static fidel.common.TileType.EXIT;
import static fidel.interaction.ExceptionHelper.fail;

public class Board {
    private static final int BITS_PER_CELL = 6;

    public final int height;
    public final int width;
    private final long[] map;

    public Board(int height, int width) {
        if (width > 10) {
            fail("width > 10 is not supported");
        }
        this.height = height;
        this.width = width;
        map = new long[height];
    }

    public Board(Board board) {
        height = board.height;
        width = board.width;
        map = board.map.clone();
    }

    public Board(TileType[][] tileTypes) {
        this(tileTypes.length, tileTypes[0].length);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setInPlace(i, j, tileTypes[i][j]);
            }
        }
    }

    public void setInPlace(int row, int col, TileType tile) {
        long cellMask = (1 << BITS_PER_CELL) - 1;
        int shift = col * BITS_PER_CELL;
        long shiftedCellMask = cellMask << shift;
        map[row] &= ~shiftedCellMask;
        map[row] |= ((long) tile.ordinal()) << shift;
    }

    public TileType get(int row, int col) {
        long cellMask = (1 << BITS_PER_CELL) - 1;
        int shift = col * BITS_PER_CELL;
        long ordinal = (map[row] >> shift) & cellMask;
        return TileType.ALL[(int) ordinal];
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
                if (get(i, j) == tileType) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }

    public boolean contains(TileType tileType) {
        return find(tileType) != null;
    }

    public TileType get(Cell cell) {
        return get(cell.row, cell.col);
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

    public Board swapGates() {
        Cell entrance = findEntrance();
        Cell exit = findExit();
        Board r = new Board(this);
        r.setInPlace(entrance, EXIT);
        r.setInPlace(exit, ENTRANCE);
        return r;
    }

    @Override
    public String toString() {
        //noinspection ConstantConditions
        int[] colWidth = IntStream.range(0, width).map(col ->
                IntStream.range(0, height).map(row -> get(row, col).name().length()).max().getAsInt()
        ).toArray();
        String s = IntStream.range(0, height)
                .mapToObj(row ->
                        IntStream.range(0, width)
                                .mapToObj(col -> padRight(get(row, col).name(), colWidth[col]))
                                .collect(Collectors.joining(", "))
                )
                .map(rs -> "{" + rs + "}")
                .collect(Collectors.joining(",\n"));
        return "\n\n" + s + "\n\n";
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public Cell getOppositeCell(Cell cell) {
        return new Cell(height - 1 - cell.row, width - 1 - cell.col);
    }

    public TileType getOpposite(Cell cell) {
        return get(getOppositeCell(cell));
    }
}
