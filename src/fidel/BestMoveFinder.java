package fidel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fidel.Command.*;
import static fidel.Direction.DIRS;
import static fidel.TileType.*;
import static java.lang.Math.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class BestMoveFinder {

    List<Command> bestMoves = null;
    double bestEvaluation = Double.NEGATIVE_INFINITY;
    PlayerState bestState = null;
    final List<Command> curMoves = new ArrayList<>();
    final Cell exit;
    long start;
    boolean alienLevel;

    public BestMoveFinder(GameState gameState) {
        exit = gameState.findExit();
    }

    public static List<Command> findBestMoves(GameState gameState) {
        if (gameState.find(ROBODOOR) != null) {
            return Arrays.asList(DOWN, RIGHT, RIGHT, RIGHT, RIGHT,
                    UP, RIGHT, RIGHT);
        }
        MovesAndEvaluation first = new BestMoveFinder(gameState).findBestMoves0(gameState); // todo refactor
        gameState.swapInPlace();
        MovesAndEvaluation second = new BestMoveFinder(gameState).findBestMoves0(gameState);
        if (first.evaluation >= second.evaluation) {
            return first.moves;
        } else {
            List<Command> r = new ArrayList<>();
            r.add(ENTER);
            r.addAll(second.moves);
            return r;
        }
    }

    private MovesAndEvaluation findBestMoves0(GameState gameState) {
        start = System.currentTimeMillis();
        alienLevel = gameState.find(ALIEN) != null;
        try {
            dfs(gameState, gameState.findEntrance(),
                    new PlayerState(0, 0, 0, false, gameState.maxHp, 0, gameState.maxHp, false, 0, 0, 3),
                    1);
        } catch (TimeoutException e) {
        }
        System.out.println(bestState);
        return new MovesAndEvaluation(bestMoves, evaluate(bestState, bestMoves));
    }

    private boolean dfs(GameState gameState, Cell cur, PlayerState ps, int round) {
        if (ps.hp < 0) {
            return false;
        }
        if (!exitReachable(gameState, cur, exit)) {
            return false;
        }
        if (finished(cur, ps)) {
            double evaluation = evaluate(ps, curMoves);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestState = ps;
                bestMoves = new ArrayList<>(curMoves);
                System.out.println("cur best " + ps);
            }
            return true;
        }
        if (bestMoves != null && tooLate()) {
            throw new TimeoutException();
        }
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!gameState.inside(to)) {
                continue;
            }
            if (!passableNow(gameState.get(to), ps)) {
                continue;
            }
            TileType oldTile = gameState.get(to);
            PlayerState newPs = calcNewPs(ps, oldTile, dir, gameState, to, round);
            GameState newGameState = gameState.setAndCopy(to, VISITED);
            if (newPs.xp > ps.xp) {
                awakeAborigines(newGameState, to);
            }

            curMoves.add(dir.command);

            dfs(newGameState, to, newPs, round + 1);

            pop(curMoves);
        }

        GameState afterBarking = afterBarking(gameState, cur);
        if (afterBarking != null) {
            curMoves.add(BARK);
            dfs(afterBarking, cur, ps, round + 1);
            pop(curMoves);
        }

        return false;
    }

    private boolean awakeAborigines(GameState gameState, Cell cell) {
        boolean found = false;
        for (Direction dir : DIRS) {
            Cell to = cell.add(dir);
            if (!gameState.inside(to)) {
                continue;
            }
            if (gameState.get(to) == ABORIGINE) {
                found = true;
                gameState.setInPlace(to, ANGRY_ABORIGINE);
            }
        }
        return found;
    }

    private boolean finished(Cell cur, PlayerState ps) {
        if (alienLevel && ps.aliensKilled < 15) {
            return false;
        }
        return cur.equals(exit);
    }

    private boolean tooLate() {
        //return false; // todo revert
        return System.currentTimeMillis() - start > 15000;
    }

    private static boolean exitReachable(GameState gameState, Cell cur, Cell exit) {
        boolean[][] visited = new boolean[gameState.height][gameState.width]; // todo get rid of it (make thread local?)
        return dfs(gameState, cur, visited, exit);
    }

    private static boolean dfs(GameState gameState, Cell cur, boolean[][] visited, Cell exit) {
        if (cur.equals(exit)) {
            return true;
        }
        visited[cur.row][cur.col] = true;
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!gameState.inside(to)) {
                continue;
            }
            if (!potentiallyPassable(gameState.get(to))) {
                continue;
            }
            if (visited[to.row][to.col]) {
                continue;
            }
            if (dfs(gameState, to, visited, exit)) {
                return true;
            }
        }
        return false;
    }

    private GameState afterBarking(GameState gameState, Cell cur) { // returns null if nothing changed
        GameState newGameState = new GameState(gameState);
        boolean somethingChanged = false;

        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!newGameState.inside(to)) {
                continue;
            }
            TileType toTile = newGameState.get(to);
            if (toTile.isTurtle()) {
                for (TileType turtle : TileType.TURTLES) {
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
            return newGameState;
        } else {
            return null;
        }
    }

    private PlayerState calcNewPs(PlayerState ps, TileType tile, Direction dir, GameState gameState, Cell cell, int round) {
        int gold = ps.gold;
        if (tile == COIN) {
            gold++;
        }
        boolean smallFlowersNearby = tile == BIG_FLOWER && smallFlowersNearby(gameState, cell);
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
        if (alienLevel && round % 10 == 0 && (cell.row == exit.row || cell.col == exit.col)) {
            hp = min(hp, 0);
        }

        boolean switchUsed = ps.switchUsed || tile == SWITCH;
        int aliensKilled = ps.aliensKilled + (tile == ALIEN ? 1 : 0);
        int buttonsPressed = ps.buttonsPressed + (tile == BUTTON ? 1 : 0);
        int robotBars = max(0, ps.robotBars - (tile == BUTTON ? 1 : 0));
        if (addXp > 0) {
            robotBars = 3;
        }

        return new PlayerState(gold, xp, streak, afterTriple, hp, poison, ps.maxHp, switchUsed, aliensKilled, buttonsPressed, robotBars);
    }

    private int calcHp(PlayerState ps, TileType tile, int dmg, int poison) {
        int hp = ps.hp - dmg;
        hp = min(hp, ps.maxHp - poison);
        if (tile == MEDIKIT) {
            hp = ps.maxHp - poison;
        }
        return hp;
    }

    private int calcDmg(TileType tile, Direction dir, boolean smallFlowersNearby, PlayerState ps) {
        if (tile == SPIDER || tile == CROWNED_SPIDER || tile == ALIEN || tile == ROBO_MEDIKIT) {
            return 1;
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

    private boolean smallFlowersNearby(GameState gameState, Cell cell) {
        for (Direction dir : DIRS) {
            Cell to = cell.add(dir);
            if (!gameState.inside(to)) {
                continue;
            }
            if (gameState.get(to) == SMALL_FLOWER) {
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


    private static boolean potentiallyPassable(TileType tile) {
        return tile != ENTRANCE && tile != VISITED && tile != CHEST && tile != WALL && tile != GNOME;
    }

    private static boolean passableNow(TileType tile, PlayerState ps) {
        if (!potentiallyPassable(tile)) {
            return false;
        }
        if (tile == RAISED_WALL) {
            return ps.buttonsPressed % 2 == 1;
        }
        if (tile == LOWERED_WALL) {
            return ps.buttonsPressed % 2 == 0;
        }
        return true;
    }

    private static double evaluate(PlayerState ps, List<Command> moves) {
        if (ps == null) {
            return Integer.MIN_VALUE;
        }
        return ps.gold * 10 + ps.xp - moves.size() / 1000.0;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }

    static class MovesAndEvaluation {
        final List<Command> moves;
        final double evaluation;

        MovesAndEvaluation(List<Command> moves, double evaluation) {
            this.moves = moves;
            this.evaluation = evaluation;
        }
    }

    static class TimeoutException extends RuntimeException {
    }

    static class PlayerState {
        final int gold;
        final int xp;
        final int streak;
        final boolean afterTriple;
        final int hp;
        final int poison;
        final int maxHp;
        final boolean switchUsed;
        final int aliensKilled;
        final int buttonsPressed;
        final int robotBars;

        PlayerState(int gold, int xp, int streak, boolean afterTriple, int hp, int poison, int maxHp, boolean switchUsed, int aliensKilled, int buttonsPressed, int robotBars) {
            this.gold = gold;
            this.xp = xp;
            this.streak = streak;
            this.afterTriple = afterTriple;
            this.hp = hp;
            this.poison = poison;
            this.maxHp = maxHp;
            this.switchUsed = switchUsed;
            this.aliensKilled = aliensKilled;
            this.buttonsPressed = buttonsPressed;
            this.robotBars = robotBars;
        }

        @Override
        public String toString() {
            return "PlayerState{" +
                    "gold=" + gold +
                    ", xp=" + xp +
                    ", streak=" + streak +
                    ", afterTriple=" + afterTriple +
                    ", hp=" + hp +
                    ", poison=" + poison +
                    ", maxHp=" + maxHp +
                    ", switchUsed=" + switchUsed +
                    ", aliensKilled=" + aliensKilled +
                    ", buttonsPressed=" + buttonsPressed +
                    ", robotBars=" + robotBars +
                    '}';
        }
    }
}
