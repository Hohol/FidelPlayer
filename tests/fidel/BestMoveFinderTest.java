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
                3,
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, EMPTY, SPIDER},
                        {EMPTY, EMPTY, SPIDER, EXIT, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, DOWN, RIGHT)
        );
    }

    @Test
    void redSpider() {
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, RED_SPIDER, EMPTY},
                        {SMALL_SPIDER, SMALL_SPIDER, SMALL_SPIDER, EXIT},
                },
                Arrays.asList(DOWN, RIGHT, RIGHT, UP, RIGHT, DOWN)
        );
    }

    @Test
    void dontDie() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, SPIDER},
                        {EMPTY, EMPTY, EMPTY, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, DOWN, RIGHT)
        );
    }

    @Test
    void swapGates() {
        check(
                new TileType[][]{
                        {ENTRANCE, RED_SPIDER, SMALL_SPIDER, SMALL_SPIDER, SMALL_SPIDER, EXIT},
                },
                Arrays.asList(ENTER, LEFT, LEFT, LEFT, LEFT, LEFT)
        );
    }

    @Test
    void crownedSpider() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER},
                        {CROWNED_SPIDER, EXIT},
                },
                Arrays.asList(DOWN, RIGHT)
        );
    }

    @Test
    void spikes() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIKES, SPIKES, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, DOWN, RIGHT, RIGHT, UP)
        );
    }

    @Test
    void spikes2() {
        check(
                new TileType[][]{
                        {ENTRANCE, SWITCH, SPIKES, SPIKES, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void flowers() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, BIG_FLOWER, SMALL_FLOWER},
                        {EMPTY, EMPTY, SMALL_FLOWER, EXIT},
                },
                Arrays.asList(RIGHT, DOWN, RIGHT, RIGHT)
        );
    }

    @Test
    void flowers2() {
        check(
                new TileType[][]{
                        {ENTRANCE, BIG_FLOWER, SMALL_FLOWER, EXIT},
                        {EMPTY, SMALL_FLOWER, EMPTY, EMPTY},
                },
                Arrays.asList(ENTER, LEFT, DOWN, LEFT, UP, LEFT)
        );
    }

    @Test(enabled = false)
    void performance() {
        GameState gameState = new GameState(
                new Board(
                        new TileType[][]{
                                {EMPTY, MEDIKIT, EMPTY, ALIEN, EMPTY, ALIEN, ALIEN},
                                {EMPTY, EMPTY, ALIEN, EMPTY, MEDIKIT, EMPTY, ALIEN},
                                {EMPTY, ALIEN, ALIEN, EMPTY, ALIEN, ALIEN, EMPTY},
                                {ENTRANCE, EMPTY, EMPTY, EXIT, EMPTY, ALIEN, ALIEN},
                                {MEDIKIT, EMPTY, EMPTY, ALIEN, EMPTY, MEDIKIT, EMPTY},
                                {ALIEN, EMPTY, EMPTY, ALIEN, EMPTY, EMPTY, EMPTY},
                                {EMPTY, EMPTY, ALIEN, ALIEN, MEDIKIT, ALIEN, EMPTY}
                        }), 3
        );
        BestMoveFinder.findBestMoves(gameState);
    }

    @Test(enabled = false)
    void alienLaser() {
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ALIEN, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, DOWN, RIGHT, RIGHT, UP, RIGHT)
        );
    }

    @Test
    void aborigine() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, ABORIGINE, EXIT},
                },
                Arrays.asList(ENTER, LEFT, LEFT, LEFT)
        );
    }

    @Test
    void aborigineAndVampire() {
        check(
                new TileType[][]{
                        {ENTRANCE, ABORIGINE, VAMPIRE, EXIT}
                },
                Arrays.asList(BARK, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void buttons() {
        check(
                new TileType[][]{
                        {ENTRANCE, RAISED_WALL, EXIT},
                        {BUTTON, RAISED_WALL, EMPTY},
                },
                Arrays.asList(DOWN, RIGHT, RIGHT, UP)
        );
    }

    @Test
    void robots() {
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, ROBOT, EXIT},
                        {BUTTON, BUTTON, BUTTON, EMPTY},
                },
                Arrays.asList(DOWN, RIGHT, RIGHT, UP, RIGHT)
        );
    }

    @Test
    void robots2() {
        check(
                new TileType[][]{
                        {ENTRANCE, BUTTON, BUTTON, BUTTON, SPIDER, ROBOT, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, DOWN, RIGHT, RIGHT, UP, RIGHT)
        );
    }

    private void check(int initialHp, TileType[][] map, List<Command> expected) {
        GameState gameState = new GameState(new Board(map), initialHp);
        List<Command> actual = BestMoveFinder.findBestMoves(gameState);
        assertEquals(actual, expected, actual.toString());
    }

    private void check(TileType[][] map, List<Command> expected) {
        check(2, map, expected);
    }
}