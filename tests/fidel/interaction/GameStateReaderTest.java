package fidel.interaction;

import fidel.common.Board;
import fidel.common.GameState;
import fidel.common.LevelType;
import fidel.common.TileType;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.tryy;
import static org.testng.Assert.assertEquals;

@Test
public class GameStateReaderTest {
    GameStateReader gameStateReader = new GameStateReader();

    @Test
    void test() throws IOException {
        BufferedImage img = readImg("1");
        GameState gameState = gameStateReader.parseImage(img);
        assertEquals(gameState.maxHp, 3);
        assertEquals(gameState.levelType, LevelType.ALIENS);
        assertEquals(gameState.gold, 9);
        assertEquals(gameState.xp, 7);
    }

    @Test
    void xp() {
        checkXp("1", 7);
        checkXp("5", 5);
        checkXp("19", 19);
        checkXp("23", 23);
        checkXp("40", 40);
        checkXp("68", 68);
    }

    @Test
    void dragon() {
        BufferedImage img = readImg("dragon");
        GameState gameState = gameStateReader.parseImage(img);
        Board board = gameState.board;
        assertEquals(board.get(1, 1), DRAGON_SPIKE_1);
        assertEquals(board.get(0, 0), DRAGON_SPIKE_2);
        assertEquals(board.get(0, 1), FIRE);
        assertEquals(board.get(0, 2), PAW_LEFT);
        assertEquals(board.get(4, 5), PAW_RIGHT);
        assertEquals(board.get(3, 1), EMPTY); // todo detect vortex
        assertEquals(board.get(0, 6), EMPTY); // todo detect vortex
        assertEquals(board.get(5, 4), SNOUT);
        assertEquals(board.get(2, 2), EYE);
        assertEquals(board.get(5, 0), WING);
        assertEquals(board.get(6, 8), WING);
    }

    private void checkXp(String name, int expected) {
        int actual = gameStateReader.getXp(readImg(name));
        assertEquals(actual, expected);
    }

    private BufferedImage readImg(String imgName) {
        return tryy(() -> ImageIO.read(new File("tests/imgs/" + imgName + ".png")));
    }
}