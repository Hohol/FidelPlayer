package fidel.logic;

import fidel.common.*;

import static fidel.common.Direction.DIRS;
import static fidel.common.Direction.DOWN;
import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.fail;
import static java.lang.Math.*;

public class Simulator {

    private final static int[] REQUIRED_XP = {60, 90, 100, 110, 120, 140};

    private final LevelType levelType;
    private final Cell exit;
    private final GameParameters gameParameters;
    private final boolean aborigineLevel;
    private final int requiredXp;

    public Simulator(GameState gameState, GameParameters gameParameters) {
        this.levelType = gameState.levelType;
        this.exit = gameState.board.find(EXIT);
        this.aborigineLevel = gameState.board.contains(ABORIGINE);
        this.gameParameters = gameParameters;
        requiredXp = REQUIRED_XP[gameState.maxHp - 2];
    }

    MoveGameState simulateMove(MoveGameState gameState, Direction dir) {
        Cell to = gameState.cur.add(dir);
        PlayerState ps = gameState.ps;
        Board board = gameState.board;
        PlayerState newPs = calcNewPs(ps, board.get(to), dir, board, to, gameState.round);
        Board newBoard = board.setAndCopy(to, VISITED);
        if (levelType == LevelType.ROBODOG && ps.bossHp > 0) {
            newBoard.setInPlace(newBoard.getOppositeCell(to), EMPTY);
        }
        if (newPs.xp > ps.xp) {
            awakeAborigines(newBoard, to);
        }
        if (aborigineLevel) {
            sleepAborigines(newBoard, to, dir);
        }
        return new MoveGameState(newBoard, to, newPs, gameState.round + 1);
    }

    private void sleepAborigines(Board board, Cell to, Direction dir) {
        Direction opposite = dir.opposite();
        Direction normal = dir.normal();
        Cell c = to.add(opposite).add(opposite);
        sleep(board, c);
        sleep(board, c.add(normal));
        sleep(board, c.add(normal.opposite()));
    }

    private void sleep(Board board, Cell c) {
        if (board.inside(c) && board.get(c) == ANGRY_ABORIGINE) {
            board.setInPlace(c, ABORIGINE);
        }
    }

    MoveGameState simulateBark(MoveGameState gameState) { // returns null if nothing changed
        Cell cur = gameState.cur;

        Board newBoard = new Board(gameState.board);
        boolean somethingChanged = false;

        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!newBoard.inside(to)) {
                continue;
            }
            TileType toTile = newBoard.get(to);
            if (toTile.isTurtle()) {
                for (TileType turtle : TileType.TURTLES) {
                    //noinspection ConstantConditions
                    if (turtle.dir.isOpposite(dir) && toTile != turtle) {
                        newBoard.setInPlace(to, turtle);
                        somethingChanged = true;
                        break;
                    }
                }
            }
        }
        somethingChanged |= awakeAborigines(newBoard, cur);
        somethingChanged |= awakeMimics(newBoard, cur);
        if (somethingChanged) {
            return new MoveGameState(newBoard, cur, gameState.ps, gameState.round);
        } else {
            return null;
        }
    }

    public MoveGameState simulateHeal(MoveGameState gameState) {
        PlayerState ps = gameState.ps;

        if (ps.gold < 3) {
            return null;
        }
        int hp = min(ps.hp + 1, ps.maxHp - ps.poison);
        if (hp == ps.hp) {
            return null;
        }
        return new MoveGameState(
                gameState.board,
                gameState.cur,
                new PlayerState(
                        ps.gold - 3,
                        ps.xp, ps.streak, ps.afterTriple,
                        hp,
                        ps.poison, ps.maxHp, ps.switchUsed, ps.buttonsPressed, ps.robotBars, ps.bossHp,
                        ps.usedBomb),
                gameState.round
        );
    }

    public MoveGameState simulateSyringe(MoveGameState gameState) {
        PlayerState ps = gameState.ps;
        if (ps.gold < 9) {
            return null;
        }
        if (ps.hp == ps.maxHp) {
            return null;
        }
        return new MoveGameState(
                gameState.board,
                gameState.cur,
                new PlayerState(
                        ps.gold - 9,
                        ps.xp, ps.streak, ps.afterTriple,
                        ps.maxHp,
                        0,
                        ps.maxHp, ps.switchUsed, ps.buttonsPressed, ps.robotBars, ps.bossHp,
                        ps.usedBomb),
                gameState.round
        );
    }

    public MoveGameState simulateBomb(MoveGameState gameState) {
        PlayerState ps = gameState.ps;
        Board board = gameState.board;
        Cell cur = gameState.cur;
        if (ps.gold < 6) {
            return null;
        }
        int addXp = 0;
        int buttonsPressed = ps.buttonsPressed;
        Board newBoard = new Board(board);
        boolean found = false;
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!board.inside(to)) {
                continue;
            }

            TileType target = board.get(to);
            if (bombableEnemy(target)) {
                found = true;
                addXp += 3;
                newBoard.setInPlace(to, EMPTY);
            } else if (target == BUTTON) {
                found = true;
                buttonsPressed++;
                newBoard.setInPlace(to, EMPTY);
            } else if (bombableItem(target)) {
                newBoard.setInPlace(to, EMPTY);
                if (target == MEDIKIT) {
                    found = true;
                }
            }

        }
        if (!found) {
            return null;
        }

        int xp = ps.xp + addXp;

        int maxHp = ps.maxHp;
        int hp = ps.hp;

        if (ps.xp < requiredXp && xp >= requiredXp) {
            maxHp++;
            hp = maxHp - ps.poison;
        }

        return new MoveGameState(
                newBoard,
                cur,
                new PlayerState(
                        ps.gold - 6,
                        xp,
                        ps.streak, ps.afterTriple, hp,
                        ps.poison, maxHp, ps.switchUsed, buttonsPressed, ps.robotBars, ps.bossHp,
                        true
                ),
                gameState.round
        );
    }

    private boolean bombableItem(TileType tile) {
        return tile == MEDIKIT;
    }

    private boolean bombableEnemy(TileType tile) {
        return tile == SNAKE ||
                tile == SPIDER ||
                tile == SMALL_SPIDER ||
                tile == RED_SPIDER ||
                tile == VAMPIRE ||
                tile == TURTLE_RIGHT ||
                tile == TURTLE_DOWN ||
                tile == TURTLE_LEFT ||
                tile == TURTLE_UP ||
                tile == CROWNED_SPIDER ||
                tile == BIG_FLOWER ||
                tile == ALIEN ||
                tile == ABORIGINE ||
                tile == ANGRY_ABORIGINE ||
                tile == SMALL_FLOWER ||
                tile == ROBOT;
    }

    PlayerState calcNewPs(PlayerState ps, TileType tile, Direction dir, Board board, Cell cell, int round) {
        int gold = ps.gold;
        if (tile == COIN) {
            gold++;
        }
        if (tile == TREASURE_CHEST) {
            gold += 5;
        }
        gold = min(9, gold);
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
        int bossHp = calcBossHp(ps, tile, cell, board, dir);
        if (bossHp == 0 && ps.bossHp != 0) {
            xp += 15;
        }

        int buttonsPressed = ps.buttonsPressed + (tile == BUTTON ? 1 : 0);
        int robotBars = max(0, ps.robotBars - (tile == BUTTON ? 1 : 0));
        if (addXp > 0) {
            robotBars = 3;
        }

        int maxHp = ps.maxHp;
        if (hp >= 0 && ps.xp < requiredXp && xp >= requiredXp) {
            maxHp++;
            hp = maxHp - ps.poison;
        }

        return new PlayerState(gold, xp, streak, afterTriple, hp, poison, maxHp, switchUsed, buttonsPressed, robotBars, bossHp, ps.usedBomb);
    }

    private int calcBossHp(PlayerState ps, TileType tile, Cell cell, Board board, Direction dir) {
        if (ps.bossHp == 0) {
            return 0;
        }
        if (levelType == LevelType.ALIENS) {
            return ps.bossHp - (tile == ALIEN ? 1 : 0);
        }
        if (levelType == LevelType.ROBODOG) {
            TileType robodogTile = board.getOpposite(cell);
            if (robodogTile == ROBO_MEDIKIT) {
                return gameParameters.robodogMaxHp;
            }
            return max(0, ps.bossHp - calcRobodogDmg(robodogTile, ps, dir));
        }
        return ps.bossHp;
    }

    private int calcRobodogDmg(TileType tile, PlayerState ps, Direction dir) {
        if (tile == SPIDER) {
            return 1;
        }
        if (tile == RED_SPIDER) {
            if (ps.afterTriple) {
                return 0;
            } else {
                return 2;
            }
        }
        if (tile.isTurtle()) {
            if (dir.opposite() == tile.dir) {
                return 0;
            } else {
                return 2;
            }
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
            if (ps.usedBomb) {
                return 2;
            } else {
                return 0;
            }
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
        if (tile == MIMIC_CHEST || tile == BARKED_MIMIC_CHEST && dir != DOWN) {
            return 2;
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
            if (ps.usedBomb) {
                return 1;
            } else {
                return 3;
            }
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
        if (tile == BARKED_MIMIC_CHEST && dir == DOWN) {
            return 4;
        }
        return 0;
    }

    private boolean robotsDisabled(PlayerState ps) {
        return ps.robotBars == 0;
    }

    boolean awakeAborigines(Board board, Cell cell) {
        return awake(board, cell, ABORIGINE, ANGRY_ABORIGINE);
    }

    private boolean awakeMimics(Board board, Cell cell) {
        return awake(board, cell, MIMIC_CHEST, BARKED_MIMIC_CHEST);
    }

    private boolean awake(Board board, Cell cell, TileType fromState, TileType toState) {
        boolean found = false;
        for (Direction dir : DIRS) {
            Cell to = cell.add(dir);
            if (!board.inside(to)) {
                continue;
            }

            if (board.get(to) == fromState) {
                found = true;
                board.setInPlace(to, toState);
            }
        }
        return found;
    }

    public static int getInitialBossHp(LevelType levelType, GameParameters gameParameters) {
        if (levelType == LevelType.ALIENS) {
            return gameParameters.alienBossHp;
        }
        if (levelType == LevelType.ROBODOG) {
            return gameParameters.robodogMaxHp;
        }
        return 0;
    }

    public MoveGameState simulate(Command command, MoveGameState gameState) {
        switch (command) {
            case UP:
                return simulateMove(gameState, Direction.UP);
            case LEFT:
                return simulateMove(gameState, Direction.LEFT);
            case DOWN:
                return simulateMove(gameState, Direction.DOWN);
            case RIGHT:
                return simulateMove(gameState, Direction.RIGHT);
            case BARK:
                return simulateBark(gameState);
            case HEAL:
                return simulateHeal(gameState);
            case BOMB:
                return simulateBomb(gameState);
            case SYRINGE:
                return simulateSyringe(gameState);
            case ENTER:
                return gameState.swapGates();
        }
        throw new RuntimeException("not supported: " + command);
    }
}
