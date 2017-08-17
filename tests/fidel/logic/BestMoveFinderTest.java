package fidel.logic;

import com.google.common.collect.ImmutableMap;
import fidel.common.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fidel.common.Command.*;
import static fidel.common.TileType.*;
import static org.testng.Assert.*;

@Test
public class BestMoveFinderTest {

    LevelType levelType;
    int maxHp;
    int gold;
    int xp;
    GameParameters gameParameters;
    Map<Cell, Integer> eggTiming;

    @BeforeMethod
    void init() {
        levelType = LevelType.NORMAL;
        maxHp = 2;
        gold = 0;
        xp = 0;
        gameParameters = new GameParameters();
        eggTiming = Collections.emptyMap();
    }

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
        maxHp = 3;
        check(
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
                        }), 3,
                0, 0, LevelType.ALIENS, eggTiming);
        while (true) {
            BestMoveFinder.findBestMoves(gameState, gameParameters);
        }
    }

    @Test(enabled = false)
    void performance2() {
        GameState gameState = new GameState(
                new Board(
                        new TileType[][]{
                                {EMPTY, MEDIKIT, EMPTY, EMPTY, EMPTY, EMPTY, VAMPIRE},
                                {COIN, SPIDER, EMPTY, EMPTY, TURTLE_LEFT, EMPTY, EMPTY},
                                {EMPTY, MEDIKIT, EMPTY, WALL, RED_SPIDER, EMPTY, EMPTY},
                                {EMPTY, MEDIKIT, TURTLE_RIGHT, WALL, EMPTY, EMPTY, EMPTY},
                                {ENTRANCE, EMPTY, EMPTY, EXIT, WALL, SPIDER, EMPTY},
                                {WALL, EMPTY, EMPTY, EMPTY, MEDIKIT, EMPTY, SNAKE}
                        }), 3,
                6, 0, LevelType.NORMAL, eggTiming);
        BestMoveFinder.findBestMoves(gameState, gameParameters);
    }

    @Test(enabled = false)
    void performance3() {
        GameState gameState = new GameState(
                new Board(
                        new TileType[][]{
                                {MEDIKIT, EMPTY, EMPTY, ALIEN, EMPTY, ALIEN, ALIEN},
                                {EMPTY, ALIEN, EMPTY, EMPTY, EMPTY, EMPTY, MEDIKIT},
                                {ALIEN, ALIEN, ALIEN, ALIEN, EMPTY, EMPTY, ALIEN},
                                {ENTRANCE, ALIEN, EMPTY, EXIT, ALIEN, EMPTY, EMPTY},
                                {EMPTY, MEDIKIT, ALIEN, EMPTY, ALIEN, EMPTY, MEDIKIT},
                                {ALIEN, EMPTY, ALIEN, EMPTY, EMPTY, ALIEN, ALIEN},
                                {ALIEN, ALIEN, EMPTY, EMPTY, MEDIKIT, ALIEN, ALIEN}
                        }), 3,
                9, 0, LevelType.ALIENS, eggTiming);
        BestMoveFinder.findBestMoves(gameState, gameParameters);
    }

    @Test
    void alienLaser() {
        gameParameters.alienBossHp = 2;
        check(
                new TileType[][]{
                        {EMPTY, ENTRANCE},
                        {EMPTY, ALIEN},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, ALIEN},
                        {EMPTY, EXIT},
                },
                Arrays.asList(DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, LEFT, DOWN, RIGHT, DOWN, DOWN)
        );
    }

    @Test
    void alienLaser2() {
        gameParameters.alienBossHp = 1;
        check(
                new TileType[][]{
                        {EMPTY, ENTRANCE},
                        {EMPTY, ALIEN},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, EMPTY},
                        {EMPTY, ALIEN},
                        {EMPTY, EXIT},
                },
                Arrays.asList(DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN)
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
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, DOWN, RIGHT, RIGHT, UP, RIGHT)
        );
    }

    @Test
    void robodog() {
        levelType = LevelType.ROBODOG;
        gameParameters.robodogMaxHp = 1;
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, EMPTY},
                        {EMPTY, WALL, EMPTY},
                        {EMPTY, EMPTY, EXIT},
                },
                Arrays.asList(DOWN, DOWN, RIGHT, RIGHT)
        );
    }

    @Test
    void robodog2() {
        levelType = LevelType.ROBODOG;
        gameParameters.robodogMaxHp = 1;
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, EMPTY},
                        {EMPTY, EMPTY, EMPTY},
                        {EMPTY, SPIDER, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, DOWN, DOWN)
        );
    }

    @Test
    void robodog3() {
        levelType = LevelType.ROBODOG;
        gameParameters.robodogMaxHp = 1;
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, EMPTY},
                        {EMPTY, WALL, EMPTY},
                        {EMPTY, TURTLE_LEFT, EXIT},
                },
                Arrays.asList(ENTER, UP, UP, LEFT, LEFT)
        );
    }

    @Test
    void treasure() {
        check(
                new TileType[][]{
                        {ENTRANCE, COIN},
                        {TREASURE_CHEST, EXIT},
                },
                Arrays.asList(DOWN, RIGHT)
        );
    }

    @Test
    void mimicChest() {
        check(
                new TileType[][]{
                        {EMPTY, EMPTY, EMPTY},
                        {ENTRANCE, MIMIC_CHEST, EXIT},
                },
                Arrays.asList(UP, RIGHT, BARK, DOWN, RIGHT)
        );
    }

    @Test
    void mimicChest2() {
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, MIMIC_CHEST, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, DOWN, RIGHT, RIGHT, UP)
        );
    }

    @Test
    void heal() {
        gold = 3;
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, SPIDER, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, HEAL, RIGHT, RIGHT)
        );
    }

    @Test
    void heal2() {
        gold = 3;
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, SPIDER, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, HEAL, RIGHT, RIGHT)
        );
    }

    @Test
    void heal3() {
        gold = 3;
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, EMPTY, SPIDER, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, DOWN, RIGHT, RIGHT, UP)
        );
    }

    @Test
    void bomb() {
        gold = 6;
        maxHp = 6;
        check(
                new TileType[][]{
                        {ENTRANCE, VAMPIRE, RED_SPIDER, RED_SPIDER, RED_SPIDER, VAMPIRE, EXIT},
                },
                Arrays.asList(BOMB, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void bomb2() {
        gold = 6;
        check(
                new TileType[][]{
                        {SMALL_SPIDER, EMPTY},
                        {ENTRANCE, SMALL_SPIDER},
                        {SMALL_SPIDER, EXIT},
                },
                Arrays.asList(BOMB, RIGHT, DOWN)
        );
    }

    @Test
    void bomb3() {
        gold = 6;
        check(
                new TileType[][]{
                        {SMALL_SPIDER, EMPTY, ABORIGINE},
                        {ENTRANCE, SMALL_SPIDER, EMPTY},
                        {SMALL_SPIDER, EXIT, ABORIGINE},
                },
                Arrays.asList(RIGHT, UP, RIGHT, DOWN, DOWN, LEFT)
        );
    }

    @Test
    void aboriginesFallAsleep() {
        check(
                new TileType[][]{
                        {EMPTY, EMPTY, EMPTY, EMPTY},
                        {EMPTY, EMPTY, EMPTY, EMPTY},
                        {ENTRANCE, ABORIGINE, ABORIGINE, EXIT},
                },
                Arrays.asList(RIGHT, UP, UP, RIGHT, DOWN, DOWN, RIGHT)
        );
    }

    @Test
    void syringe() {
        gold = 9;
        maxHp = 4;
        check(
                new TileType[][]{
                        {ENTRANCE, ROBOT, ROBOT, ROBOT, ROBOT, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, SYRINGE, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void syringe2() {
        gold = 9;
        check(
                new TileType[][]{
                        {ENTRANCE, SNAKE, SNAKE, SNAKE, SPIDER, SPIDER, SNAKE, SNAKE, SNAKE, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, SYRINGE, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void levelUp() {
        xp = 59;
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, SPIDER, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void levelUpBomb() {
        xp = 57;
        gold = 6;
        check(
                new TileType[][]{
                        {ENTRANCE, VAMPIRE, ROBOT, SPIDER, VAMPIRE, EXIT},
                },
                Arrays.asList(BOMB, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void bombButton() {
        gold = 6;
        check(
                new TileType[][]{
                        {ENTRANCE, BUTTON},
                        {RAISED_WALL, WALL},
                        {EXIT, EMPTY},
                },
                Arrays.asList(BOMB, DOWN, DOWN)
        );
    }

    @Test
    void alienBossXp() {
        levelType = LevelType.ALIENS;
        gameParameters.alienBossHp = 2;
        xp = 43;
        check(
                new TileType[][]{
                        {ENTRANCE, ALIEN, ALIEN, ALIEN, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void bombMedikit() {
        gold = 6;
        check(
                new TileType[][]{
                        {ENTRANCE},
                        {SPIDER},
                        {SPIDER},
                        {MEDIKIT},
                        {VAMPIRE},
                        {COIN},
                        {COIN},
                        {COIN},
                        {COIN},
                        {COIN},
                        {COIN},
                        {COIN},
                        {COIN},
                        {COIN},
                        {EXIT},
                },
                Arrays.asList(DOWN, DOWN, BOMB, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN)
        );
    }

    @Test
    void eggs() {
        check(
                new TileType[][]{
                        {ENTRANCE, ROBOT, EGG, ROBOT, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void snakeSpawn() {
        eggTiming = ImmutableMap.of(new Cell(2, 0), 3);
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY},
                        {EMPTY, EMPTY},
                        {EGG, EXIT},
                },
                Arrays.asList(RIGHT, DOWN, LEFT, DOWN, RIGHT)
        );
    }

    @Test
    void snakeSpawn2() {
        eggTiming = ImmutableMap.of(new Cell(2, 0), 4);
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY},
                        {EMPTY, EMPTY},
                        {EGG, EXIT},
                },
                Arrays.asList(RIGHT, DOWN, DOWN)
        );
    }

    @Test
    void levelUpAndPoison() {
        xp = 59;
        check(
                new TileType[][]{
                        {ENTRANCE, SNAKE, ROBOT, SPIDER, EXIT},
                        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                },
                Arrays.asList(ENTER, LEFT, LEFT, LEFT, LEFT)
        );
    }

    @Test
    void paw() {
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, EMPTY},
                        {PAW_LEFT, WALL, EMPTY},
                        {EXIT, EMPTY, EMPTY},
                },
                Arrays.asList(RIGHT, RIGHT, DOWN, DOWN, LEFT, LEFT)
        );
    }

    @Test
    void pawBark() {
        check(
                new TileType[][]{
                        {ENTRANCE, PAW_LEFT, PAW_RIGHT, EXIT},
                },
                Arrays.asList(BARK, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void pawXp() {
        check(
                new TileType[][]{
                        {ENTRANCE, PAW_LEFT},
                        {SPIDER, EXIT}
                },
                Arrays.asList(BARK, RIGHT, DOWN)
        );
    }

    @Test
    void snout() {
        check(
                new TileType[][]{
                        {ENTRANCE, SNOUT, FIRE, FIRE, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT, RIGHT)
        );
    }

    @Test
    void volcano() {
        gold = 6;
        check(
                new TileType[][]{
                        {ENTRANCE, EXIT},
                        {VOLCANO, EMPTY},
                },
                Arrays.asList(BOMB, RIGHT)
        );
    }

    @Test
    void volcano2() {
        gold = 6;
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, EMPTY, EMPTY, EXIT},
                        {VOLCANO, EMPTY, EMPTY, WALL, WALL},
                },
                Arrays.asList(ENTER, LEFT, LEFT, DOWN, LEFT, BOMB, UP, LEFT)
        );
    }

    @Test (enabled = false)
    void bombOnExit() { // todo
        gold = 6;
        check(
                new TileType[][]{
                        {ENTRANCE, EMPTY, EMPTY, EMPTY, EXIT},
                        {VOLCANO, WALL, WALL, WALL, WALL},
                },
                Arrays.asList(BOMB, RIGHT, RIGHT, RIGHT)
        );
    }

    private void check(TileType[][] map, List<Command> expected) {
        Board board = new Board(map);
        if (board.contains(ALIEN)) {
            levelType = LevelType.ALIENS;
        }
        GameState gameState = new GameState(board, maxHp, gold, xp, levelType, eggTiming);

        List<Command> actual = BestMoveFinder.findBestMoves(gameState, gameParameters);
        assertEquals(actual, expected, actual.toString());
    }
}