package fidel.common;

import static fidel.common.Direction.*;

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
    TURTLE_UP(UP),
    MEDIKIT,
    WALL,
    CROWNED_SPIDER,
    SPIKES,
    SWITCH,
    BIG_FLOWER,
    SMALL_FLOWER,
    ALIEN,
    ABORIGINE,
    ANGRY_ABORIGINE,
    BUTTON,
    ROBOT,
    RAISED_WALL,
    LOWERED_WALL,
    ROBO_MEDIKIT,
    ROBODOG,
    GNOME;

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

    public static TileType[] TURTLES = {TURTLE_RIGHT, TURTLE_DOWN, TURTLE_LEFT, TURTLE_UP};
}
