package fidel;

import java.util.ArrayList;
import java.util.List;

import static fidel.Command.*;
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
        MovesAndEvaluation first = new BestMoveFinder(gameState).findBestMoves0();
        gameState.swap();
        MovesAndEvaluation second = new BestMoveFinder(gameState).findBestMoves0();
        if (first.evaluation >= second.evaluation) {
            return first.moves;
        } else {
            List<Command> r = new ArrayList<>();
            r.add(ENTER);
            r.addAll(second.moves);
            return r;
        }
    }

    private MovesAndEvaluation findBestMoves0() {
        dfs(gameState.findEntrance(), new PlayerState(0, 0, 0, false, gameState.initialHp, 0, gameState.initialHp));
        System.out.println(bestState);
        return new MovesAndEvaluation(bestMoves, evaluate(bestState));
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

        int poison = Math.min(ps.maxHp, ps.poison + (tile == SNAKE ? 1 : 0));

        int hp = ps.hp - dmg;
        hp = Math.min(hp, ps.maxHp - poison);

        return new PlayerState(gold, xp, streak, afterTriple, hp, poison, ps.maxHp);
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

    private int evaluate(PlayerState ps) {
        return ps.gold * 10 + ps.xp;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }

    static class MovesAndEvaluation {
        final List<Command> moves;
        final int evaluation;

        MovesAndEvaluation(List<Command> moves, int evaluation) {
            this.moves = moves;
            this.evaluation = evaluation;
        }
    }

    static class PlayerState {
        final int gold;
        final int xp;
        final int streak;
        final boolean afterTriple;
        final int hp;
        final int poison;
        final int maxHp;

        PlayerState(int gold, int xp, int streak, boolean afterTriple, int hp, int poison, int maxHp) {
            this.gold = gold;
            this.xp = xp;
            this.streak = streak;
            this.afterTriple = afterTriple;
            this.hp = hp;
            this.poison = poison;
            this.maxHp = maxHp;
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
                    '}';
        }
    }
}
