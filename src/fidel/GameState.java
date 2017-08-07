package fidel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameState {
    public final int height;
    public final int width;
    private final TileType[][] map;

    public GameState(int height, int width) {
        this.height = height;
        this.width = width;
        map = new TileType[height][width];
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
}
