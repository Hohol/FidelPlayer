package fidel.common;

public enum Direction {
    RIGHT(0, 1, Command.RIGHT),
    DOWN(1, 0, Command.DOWN),
    LEFT(0, -1, Command.LEFT),
    UP(-1, 0, Command.UP);


    public final int dRow, dCol;
    public final Command command;

    Direction(int dRow, int dCol, Command command) {
        this.dRow = dRow;
        this.dCol = dCol;
        this.command = command;
    }

    public static Direction[] DIRS = Direction.values();

    public boolean isOpposite(Direction dir) {
        return dRow == -dir.dRow && dCol == -dir.dCol;
    }

    public Direction opposite() {
        return DIRS[(ordinal() + 2) % 4];
    }

    public Direction normal() {
        return DIRS[(ordinal() + 1) % 4];
    }
}
