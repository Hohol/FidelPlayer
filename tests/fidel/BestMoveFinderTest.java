package fidel;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static fidel.Command.*;
import static fidel.TileType.*;
import static org.testng.Assert.*;

@Test
public class BestMoveFinderTest {
    @Test
    void testEmpty() {
        check(
                new TileType[][]{{ENTRANCE, EMPTY, EMPTY, EXIT}},
                Arrays.asList(RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void collectCoins() {
        check(
                new TileType[][]{
                        {COIN, EMPTY},
                        {ENTRANCE, EXIT},
                },
                Arrays.asList(UP, RIGHT, DOWN)
        );
    }

    @Test
    void collectXp() {
        check(
                new TileType[][]{
                        {SPIDER, EMPTY},
                        {ENTRANCE, EXIT},
                },
                Arrays.asList(UP, RIGHT, DOWN)
        );
    }

    @Test
    void tripleKill() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, EMPTY, SPIDER},
                        {EMPTY, EMPTY, SPIDER, EXIT, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, DOWN, RIGHT)
        );
    }

    private void check(TileType[][] map, List<Command> expected) {
        GameState gameState = new GameState(map);
        List<Command> actual = BestMoveFinder.findBestMoves(gameState);
        assertEquals(actual, expected, actual.toString());
    }
}