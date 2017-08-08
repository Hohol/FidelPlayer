package fidel;

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
    void maxHp() throws IOException {
        BufferedImage img = ImageIO.read(new File("tests/imgs/1.png"));
        assertEquals(gameStateReader.parseImage(img).maxHp, 3);
    }
}