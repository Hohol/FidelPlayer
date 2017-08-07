package fidel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import static fidel.Tryy.*;
import static java.awt.Color.*;

public class GameStateReader {

    private final Robot robot = tryy(() -> new Robot());

    public GameState readGameState() {
        //BufferedImage img = getImageFromFile();
        BufferedImage img = getImageFromCapture();
        int tileWidth = 98;
        int tileHeight = 90;
        /*int x = 26;
        int y = 58;
        int h = 7;
        int w = 7;*/

        // for 3x7 map
        int x = 26;
        int y = 418;
        int w = 7;
        int h = 3;

        showMarkedTiles(img, tileWidth, tileHeight, x, y, h, w);
        //writeImg(img, "img");

        return null;
    }

    private void showMarkedTiles(BufferedImage img, int tileWidth, int tileHeight, int x, int y, int h, int w) {
        for (int col = 0; col < w + 1; col++) {
            int xx = x + tileWidth * col;
            for (int pRow = 0; pRow <= tileHeight * h; pRow++) {
                int yy = y + pRow;
                img.setRGB(xx, yy, WHITE.getRGB());
            }
        }
        for (int row = 0; row < h + 1; row++) {
            int yy = y + tileHeight * row;
            for (int pCol = 0; pCol < tileWidth * w; pCol++) {
                int xx = x + pCol;
                img.setRGB(xx, yy, WHITE.getRGB());
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int a = 52;
                int b = 37;
                img.setRGB(x + i * tileWidth + a, y + j * tileHeight + b, WHITE.getRGB());
            }
        }
        writeImg(img, "marked_tiles");
    }

    private BufferedImage getImageFromCapture() {
        int[] a = GetWindowRect.getRect("Fidel Dungeon Rescue");
        int x = a[0];
        int y = a[1];
        int w = a[2] - x + 1;
        int h = a[3] - y + 1;
        robot.mouseMove(x + 100, y + 10);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        tryy(() -> Thread.sleep(100));
        return robot.createScreenCapture(new Rectangle(x, y, w, h));
    }

    private BufferedImage getImageFromFile() {
        return tryy(() -> ImageIO.read(new File("img.png")));
    }

    private void writeImg(BufferedImage img, String name) {
        tryy(() -> ImageIO.write(img, "png", new FileOutputStream(name + ".png")));
    }
}
