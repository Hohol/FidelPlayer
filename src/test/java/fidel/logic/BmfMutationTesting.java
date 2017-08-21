package fidel.logic;

import fidel.common.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fidel.common.Command.*;
import static fidel.common.TileType.*;
import static org.testng.Assert.assertEquals;

@Test
public class BmfMutationTesting {

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
    void loweredWall() {
        check(
                new TileType[][]{
                        {ENTRANCE, BUTTON, LOWERED_WALL, BUTTON, BUTTON, EXIT},
                },
                Arrays.asList(ENTER, LEFT, LEFT, LEFT, LEFT, LEFT)
        );
    }

    @Test
    void bossBlocksExit() {
        gameParameters.alienBossHp = 1;
        levelType = LevelType.ALIENS;
        check(
                new TileType[][]{
                        {ENTRANCE, ALIEN, EXIT},
                        {VAMPIRE, WALL, VAMPIRE},
                        {VAMPIRE, VAMPIRE, VAMPIRE},
                },
                Arrays.asList(RIGHT, RIGHT)
        );
    }

    @Test
    void robodog() {
        gameParameters.robodogMaxHp = 1;
        levelType = LevelType.ROBODOG;
        check(
                new TileType[][]{
                        {ENTRANCE, SPIDER, SPIDER, EXIT},
                },
                Arrays.asList(RIGHT, RIGHT, RIGHT)
        );
    }

    private void check(TileType[][] map, List<Command> expected) {
        GameState gameState = new GameState(new Board(map), maxHp, gold, xp, levelType, eggTiming);
        List<Command> actual = BMF.findSimpleHighScoreMoves(gameState, gameParameters);
        assertEquals(actual, expected, actual.toString());
    }

}