package fidel;

import static fidel.Direction.DIRS;
import static fidel.TileType.*;
import static java.lang.Math.*;

public class Simulator {
    private final LevelType levelType;
    private final Cell exit;

    public Simulator(LevelType levelType, Cell exit) {
        this.levelType = levelType;
        this.exit = exit;
    }

    MoveGameState simulateMove(Board board, PlayerState ps, int round, Direction dir, Cell to) {
        PlayerState newPs = calcNewPs(ps, board.get(to), dir, board, to, round);
        Board newBoard = board.setAndCopy(to, VISITED);
        if (newPs.xp > ps.xp) {
            awakeAborigines(newBoard, to);
        }
        return new MoveGameState(newBoard, newPs);
    }

    MoveGameState simulateBark(Board board, Cell cur, PlayerState ps) { // returns null if nothing changed
        Board newGameState = new Board(board);
        boolean somethingChanged = false;

        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!newGameState.inside(to)) {
                continue;
            }
            TileType toTile = newGameState.get(to);
            if (toTile.isTurtle()) {
                for (TileType turtle : TileType.TURTLES) {
                    //noinspection ConstantConditions
                    if (turtle.dir.isOpposite(dir) && toTile != turtle) {
                        newGameState.setInPlace(to, turtle);
                        somethingChanged = true;
                        break;
                    }
                }
            }
        }
        somethingChanged |= awakeAborigines(newGameState, cur);
        if (somethingChanged) {
            return new MoveGameState(newGameState, ps);
        } else {
            return null;
        }
    }

    PlayerState calcNewPs(PlayerState ps, TileType tile, Direction dir, Board board, Cell cell, int round) {
        int gold = ps.gold;
        if (tile == COIN) {
            gold++;
        }
        boolean smallFlowersNearby = tile == BIG_FLOWER && smallFlowersNearby(board, cell);
        int addXp = calcXp(tile, dir, smallFlowersNearby, ps);
        int dmg = calcDmg(tile, dir, smallFlowersNearby, ps);
        int xp = ps.xp + addXp;

        int streak = ps.streak;
        if (addXp > 0 || tile == SMALL_SPIDER) {
            streak++;
        } else {
            streak = 0;
        }

        boolean afterTriple = streak == 3 || ps.afterTriple && streak == 0;

        if (streak == 3) {
            xp += 3;
            streak = 0;
        }

        int poison = min(ps.maxHp, ps.poison + (tile == SNAKE ? 1 : 0));

        int hp = calcHp(ps, tile, dmg, poison);
        if (underAlienLaser(cell, round, ps)) {
            hp = min(hp, 0);
        }

        boolean switchUsed = ps.switchUsed || tile == SWITCH;
        int bossHp = calcBossHp(ps, tile);
        int buttonsPressed = ps.buttonsPressed + (tile == BUTTON ? 1 : 0);
        int robotBars = max(0, ps.robotBars - (tile == BUTTON ? 1 : 0));
        if (addXp > 0) {
            robotBars = 3;
        }

        return new PlayerState(gold, xp, streak, afterTriple, hp, poison, ps.maxHp, switchUsed, buttonsPressed, robotBars, bossHp);
    }

    private int calcBossHp(PlayerState ps, TileType tile) {
        if (ps.bossHp == 0) {
            return 0;
        }
        if (levelType == LevelType.ALIENS) {
            return ps.bossHp - (tile == ALIEN ? 1 : 0);
        }
        return 0;
    }

    private boolean underAlienLaser(Cell cell, int round, PlayerState ps) {
        return levelType == LevelType.ALIENS
                && ps.bossHp > 0
                && round % 10 == 0
                && (cell.row == exit.row || cell.col == exit.col);
    }

    private int calcDmg(TileType tile, Direction dir, boolean smallFlowersNearby, PlayerState ps) {
        if (tile == SPIDER || tile == CROWNED_SPIDER || tile == ALIEN || tile == ROBO_MEDIKIT) {
            return 1;
        }
        if (tile == RED_SPIDER) {
            if (ps.afterTriple) {
                return 0;
            } else {
                return 2;
            }
        }
        if (tile == VAMPIRE) {
            return ps.hp;
        }
        if (tile.isTurtle()) {
            if (dir == tile.dir) {
                return 0;
            } else {
                return 2;
            }
        }
        if (tile == SPIKES && !ps.switchUsed) {
            return 2;
        }
        if (tile == BIG_FLOWER) {
            if (smallFlowersNearby) {
                return 2;
            } else {
                return 0;
            }
        }
        if (tile == ABORIGINE) {
            return 0;
        }
        if (tile == ANGRY_ABORIGINE) {
            return 2;
        }
        if (tile == ROBOT) {
            if (robotsDisabled(ps)) {
                return 0;
            } else {
                return 2;
            }
        }
        return 0;
    }

    private int calcHp(PlayerState ps, TileType tile, int dmg, int poison) {
        int hp = ps.hp - dmg;
        hp = min(hp, ps.maxHp - poison);
        if (tile == MEDIKIT) {
            hp = ps.maxHp - poison;
        }
        return hp;
    }

    private boolean smallFlowersNearby(Board board, Cell cell) {
        for (Direction dir : DIRS) {
            Cell to = cell.add(dir);
            if (!board.inside(to)) {
                continue;
            }
            if (board.get(to) == SMALL_FLOWER) {
                return true;
            }
        }
        return false;
    }

    private int calcXp(TileType tile, Direction dir, boolean smallFlowersNearby, PlayerState ps) {
        if (tile == SPIDER || tile == ALIEN) {
            return 1;
        }
        if (tile == CROWNED_SPIDER) {
            return 3;
        }
        if (tile == SNAKE) {
            return 5;
        }
        if (tile == RED_SPIDER) {
            if (ps.afterTriple) {
                return 4;
            } else {
                return 1;
            }
        }
        if (tile == VAMPIRE) {
            if (ps.hp == 0) {
                return 5;
            } else {
                return 1;
            }
        }
        if (tile.isTurtle()) {
            if (dir == tile.dir) {
                return 4;
            } else {
                return 1;
            }
        }
        if (tile == BIG_FLOWER) {
            if (smallFlowersNearby) {
                return 1;
            } else {
                return 4;
            }
        }
        if (tile == ABORIGINE) {
            return 3;
        }
        if (tile == ANGRY_ABORIGINE) {
            return 1;
        }
        if (tile == ROBOT) {
            if (robotsDisabled(ps)) {
                return 10;
            } else {
                return 1;
            }
        }
        return 0;
    }

    private boolean robotsDisabled(PlayerState ps) {
        return ps.robotBars == 0;
    }

    boolean awakeAborigines(Board board, Cell cell) {
        boolean found = false;
        for (Direction dir : DIRS) {
            Cell to = cell.add(dir);
            if (!board.inside(to)) {
                continue;
            }
            if (board.get(to) == ABORIGINE) {
                found = true;
                board.setInPlace(to, ANGRY_ABORIGINE);
            }
        }
        return found;
    }
}
