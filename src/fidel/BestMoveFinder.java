package fidel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fidel.Command.*;
import static fidel.Direction.*;
import static fidel.TileType.*;

public class BestMoveFinder {
    public List<Command> findBestMoves(GameState gameState) {
        boolean[][] visited = new boolean[gameState.height][gameState.width];
        Cell[][] from = new Cell[gameState.height][gameState.width];
        List<Command> r = new ArrayList<>();
        dfs(gameState, gameState.findEntrance(), visited, r);
        r.add(ENTER);
        return r;
    }

    private boolean dfs(GameState gameState, Cell cur, boolean[][] visited, List<Command> r) {
        if (gameState.get(cur) == EXIT) {
            return true;
        }
        for (Direction dir : DIRS) {
            Cell to = cur.add(dir);
            if (!gameState.inside(to)) {
                continue;
            }
            if (!visited[to.row][to.col]) {
                visited[to.row][to.col] = true;
                r.add(dir.command);

                if (dfs(gameState, to, visited, r)) {
                    return true;
                }

                pop(r);
                visited[to.row][to.col] = false;
            }
        }
        return false;
    }

    private void pop(List<Command> r) {
        r.remove(r.size() - 1);
    }
}
