package fidel.interaction;

import fidel.common.GameState;
import fidel.common.LevelType;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

@Test
public class GameStateReaderTest {
    GameStateReader gameStateReader = new GameStateReader();

    @Test
    void test() throws IOException {
        BufferedImage img = ImageIO.read(new File("tests/imgs/1.png"));
        GameState gameState = gameStateReader.parseImage(img);
        assertEquals(gameState.maxHp, 3);
        assertEquals(gameState.levelType, LevelType.ALIENS);
        assertEquals(gameState.gold, 9);
    }
}