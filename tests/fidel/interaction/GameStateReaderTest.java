package fidel.interaction;

import fidel.common.GameState;
import fidel.common.LevelType;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static fidel.interaction.ExceptionHelper.tryy;
import static org.testng.Assert.assertEquals;

@Test
public class GameStateReaderTest {
    GameStateReader gameStateReader = new GameStateReader();

    @Test
    void test() throws IOException {
        BufferedImage img = readImg("1");
        GameState gameState = gameStateReader.parseImage(img);
        System.out.println(gameState);
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

    private void checkXp(String name, int expected) {
        int actual = gameStateReader.getXp(readImg(name));
        assertEquals(actual, expected);
    }

    private BufferedImage readImg(String imgName) {
        return tryy(() -> ImageIO.read(new File("tests/imgs/" + imgName + ".png")));
    }
}