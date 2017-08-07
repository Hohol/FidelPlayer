package fidel;

import static fidel.Direction.*;

public enum TileType {
    EMPTY,
    ENTRANCE,
    EXIT,
    COIN,
    SNAKE,
    SPIDER,
    VISITED,
    SMALL_SPIDER,
    RED_SPIDER,
    CHEST,
    VAMPIRE,
    TURTLE_RIGHT(RIGHT),
    TURTLE_DOWN(DOWN),
    TURTLE_LEFT(LEFT),
    TURTLE_UP(UP);

    public final Direction dir;

    TileType(Direction dir) {
        this.dir = dir;
    }

    TileType() {
        this.dir = null;
    }

    public boolean isTurtle() {
        return dir != null;
    }
}
