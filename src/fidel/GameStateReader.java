package fidel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import static fidel.Tryy.*;

public class GameStateReader {

    private final Robot robot = tryy(() -> new Robot());

    public GameState readGameState() {
        int[] a = GetWindowRect.getRect("Fidel Dungeon Rescue");
        int x = a[0];
        int y = a[1];
        int w = a[2] - x + 1;
        int h = a[3] - y + 1;
        robot.mouseMove(x + 100, y + 10);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        tryy(() -> Thread.sleep(100));
        BufferedImage img = robot.createScreenCapture(new Rectangle(x, y, w, h));
        writeImg(img);
        return null;
    }

    private void writeImg(BufferedImage img) {
        tryy(() -> ImageIO.write(img, "png", new FileOutputStream("img.png")));
    }
}
