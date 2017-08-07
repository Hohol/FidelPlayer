package fidel;

import java.util.ArrayList;
import java.util.List;

import static fidel.Direction.*;
import static fidel.TileType.*;

public class BestMoveFinder {

    private final GameState gameState;
    List<Command> bestMoves = null;
    double bestEvaluation = Double.NEGATIVE_INFINITY;
    PlayerState bestState = null;
    final List<Command> curMoves = new ArrayList<>();
    final Cell exit;

    public BestMoveFinder(GameState gameState) {
        this.gameState = gameState;
        exit = gameState.findExit();
    }

    public static List<Command> findBestMoves(GameState gameState) {
        return new BestMoveFinder(gameState).findBestMoves0(gameState);
    }

    private List<Command> findBestMoves0(GameState gameState) {
        dfs(gameState.findEntrance(), new PlayerState(0, 0, 0, false, gameState.initialHp));
        System.out.println(bestState);
        return bestMoves;
    }

    private boolean dfs(Cell cur, PlayerState ps) {
        if (ps.hp < 0) {
            return false;
        }
        if (cur.equals(exit)) {
            double evaluation = evaluate(ps);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestState = ps;
                bestMoves = new ArrayList<>(curMoves);
            }
            return true;
        }
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!gameState.inside(to)) {
                continue;
            }
            if (!passable(gameState.get(to))) {
                continue;
            }
            TileType oldTile = gameState.get(to);
            PlayerState newPs = calcNewPs(ps, oldTile);

            gameState.set(to, VISITED);
            curMoves.add(dir.command);

            dfs(to, newPs);

            pop(curMoves);
            gameState.set(to, oldTile);
        }
        return false;
    }

    private PlayerState calcNewPs(PlayerState ps, TileType tile) {
        int gold = ps.gold;
        if (tile == COIN) {
            gold++;
        }
        int addXp = calcXp(tile, ps.afterTriple);
        int dmg = calcDmg(tile, ps.hp);
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

        int hp = ps.hp - dmg;

        return new PlayerState(gold, xp, streak, afterTriple, hp);
    }

    private int calcDmg(TileType tile, int hp) {
        if (tile == SPIDER) {
            return 1;
        }
        if (tile == VAMPIRE) {
            return hp;
        }
        return 0;
    }

    private int calcXp(TileType tile, boolean afterTriple) {
        if (tile == SPIDER) {
            return 1;
        }
        if (tile == SNAKE) {
            return 5;
        }
        if (tile == RED_SPIDER) {
            if (afterTriple) {
                return 4;
            } else {
                return 1;
            }
        }
        if (tile == VAMPIRE) {
            return 1;
        }
        return 0;
    }

    private boolean passable(TileType tile) {
        return tile != ENTRANCE && tile != VISITED && tile != CHEST;
    }

    private double evaluate(PlayerState ps) {
        return ps.gold * 10 + ps.xp;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }

    static class PlayerState {
        final int gold;
        final int xp;
        final int streak;
        final boolean afterTriple;
        final int hp;

        PlayerState(int gold, int xp, int streak, boolean afterTriple, int hp) {
            this.gold = gold;
            this.xp = xp;
            this.streak = streak;
            this.afterTriple = afterTriple;
            this.hp = hp;
        }

        @Override
        public String toString() {
            return "PlayerState{" +
                    "gold=" + gold +
                    ", xp=" + xp +
                    ", streak=" + streak +
                    ", afterTriple=" + afterTriple +
                    ", hp=" + hp +
                    '}';
        }
    }
}
