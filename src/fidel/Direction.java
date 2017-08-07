package fidel;

public enum Direction {
    RIGHT(0, 1, Command.RIGHT),
    LEFT(0, -1, Command.LEFT),
    UP(-1, 0, Command.UP),
    DOWN(1, 0, Command.DOWN);

    public final int dRow, dCol;
    public final Command command;

    Direction(int dRow, int dCol, Command command) {
        this.dRow = dRow;
        this.dCol = dCol;
        this.command = command;
    }

    public static Direction[] DIRS = Direction.values();
}
