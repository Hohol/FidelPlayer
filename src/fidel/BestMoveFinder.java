package fidel;

import java.util.ArrayList;
import java.util.List;

import static fidel.Direction.*;
import static fidel.TileType.*;

public class BestMoveFinder {

    private final GameState gameState;
    List<Command> bestMoves = null;
    double bestEvaluation = Double.NEGATIVE_INFINITY;
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
        dfs(gameState.findEntrance(), new PlayerState(0, 0));
        return bestMoves;
    }

    private boolean dfs(Cell cur, PlayerState ps) {
        if (cur.equals(exit)) {
            double evaluation = evaluate(ps);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
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
        int xp = ps.xp + calcXp(tile);
        return new PlayerState(gold, xp);
    }

    private int calcXp(TileType tile) {
        if (tile == SPIDER) {
            return 1;
        }
        if (tile == SNAKE) {
            return 5;
        }
        return 0;
    }

    private boolean passable(TileType tile) {
        return tile != ENTRANCE && tile != VISITED;
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

        PlayerState(int gold, int xp) {
            this.gold = gold;
            this.xp = xp;
        }

        @Override
        public String toString() {
            return "PlayerState{" +
                    "gold=" + gold +
                    ", xp=" + xp +
                    '}';
        }
    }
}
