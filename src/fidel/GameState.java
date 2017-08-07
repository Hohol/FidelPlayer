package fidel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fidel.TileType.*;

public class GameState {
    public final int height;
    public final int width;
    public final TileType[][] map;

    public GameState(int height, int width) {
        this.height = height;
        this.width = width;
        map = new TileType[height][width];
    }

    public GameState(TileType[][] tileTypes) {
        map = tileTypes;
        height = map.length;
        width = map[0].length;
    }

    @Override
    public String toString() {
        return Stream.of(map)
                .map(row -> Stream.of(row).map(Enum::toString).collect(Collectors.joining(",")))
                .collect(Collectors.joining("\n"));
    }

    public void set(int row, int col, TileType tile) {
        map[row][col] = tile;
    }

    public Cell findEntrance() {
        return find(ENTRANCE);
    }

    public Cell findExit() {
        return find(EXIT);
    }

    private Cell find(TileType tileType) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] == tileType) {
                    return new Cell(i, j);
                }
            }
        }
        throw new RuntimeException();
    }

    public TileType get(Cell cell) {
        return get(cell.row, cell.col);
    }

    private TileType get(int row, int col) {
        return map[row][col];
    }

    public boolean inside(Cell to) {
        return inside(to.row, to.col);
    }

    private boolean inside(int row, int col) {
        return row >= 0 && col >= 0 && row < height && col < width;
    }

    public void set(Cell to, TileType tileType) {
        set(to.row, to.col, tileType);
    }
}
