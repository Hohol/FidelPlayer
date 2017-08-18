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
    EGG,
    TREASURE_CHEST,
    MIMIC_CHEST,
    BARKED_MIMIC_CHEST,

    DRAGON_SPIKE_1,
    DRAGON_SPIKE_2,
    FIRE,
    SMALL_FIRE,
    PAW_LEFT,
    PAW_RIGHT,
    VORTEX,
    SNOUT,
    EYE,
    WING,

    EXIT_50_XP,
    EXIT_15_XP,
    VOLCANO,
    BOMBABLE_WALL,
    GNOME_AND_SPIKES,
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

    public static TileType[] ALL = values();

    public String shortName() {
        if (this == DRAGON_SPIKE_1) {
            return "DS1";
        }
        if (this == DRAGON_SPIKE_2) {
            return "DS2";
        }
        if (this == TREASURE_CHEST) {
            return "TC";
        }
        return name();
    }
}
