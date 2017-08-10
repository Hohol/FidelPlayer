package fidel.interaction;

import fidel.common.Board;
import fidel.common.GameState;
import fidel.common.LevelType;
import fidel.common.TileType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fidel.common.TileType.*;
import static fidel.interaction.ExceptionHelper.*;
import static java.awt.Color.WHITE;

public class GameStateReader {

    private static final int TILE_WIDTH = 98;
    private static final int TILE_HEIGHT = 90;

    private final Robot robot = tryy(() -> new Robot());

    public GameState readGameState() {
        BufferedImage img = getImageFromCapture();
        return parseImage(img);
    }

    GameState parseImage(BufferedImage img) {
        LevelType intermissionLevelType = checkIntermission(img);
        if (intermissionLevelType != null) {
            return GameState.intermission(intermissionLevelType);
        }

        BufferedImage[][] tileImages = getTileImages(img);

        /*saveTile(tileImages[6][0], SPIDER);
        if (true) {
            return null;
        }/**/

        Map<TileType, List<BufferedImage>> tileTypeImgs = loadTiles();
        int h = tileImages.length;
        int w = tileImages[0].length;
        int maxHp = getMaxHp(img);
        int gold = getGold(img);
        Board board = new Board(h, w);
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                board.setInPlace(row, col, findMostSimilar(tileTypeImgs, tileImages[row][col]));
            }
        }

        LevelType levelType = LevelType.NORMAL;
        if (board.contains(ALIEN)) {
            levelType = LevelType.ALIENS;
        } else if (board.contains(ROBODOG)) {
            levelType = LevelType.ROBODOG;
            board.setInPlace(board.find(ROBODOG), EXIT);
        }
        return new GameState(board, maxHp, gold, levelType);
    }

    private int getMaxHp(BufferedImage img) {
        int cnt = 0;
        for (int x = 27; x <= 400; x++) {
            int rowPixel = 754;
            int red = getRed(img.getRGB(x, rowPixel));
            int prevRed = getRed(img.getRGB(x - 1, rowPixel));
            if (red >= 50 && prevRed < 50) {
                cnt++;
            }
        }
        return cnt;
    }

    private int getGold(BufferedImage img) {
        int cnt = 0;
        for (int x = 457; x <= 735; x++) {
            int rowPixel = 808;
            int red = getRed(img.getRGB(x, rowPixel));
            int prevRed = getRed(img.getRGB(x - 1, rowPixel));
            if (prevRed == 255 && red != 255) {
                cnt++;
            }
        }
        return cnt;
    }

    private void saveTile(BufferedImage img, TileType tileType) {
        int cnt = 0;
        while (true) {
            String name = "tiles/" + tileType + (cnt == 0 ? "" : ("-" + cnt));
            if (!(new File(name + ".png").exists())) {
                writeImg(img, name, false);
                break;
            }
            cnt++;
        }
    }

    private BufferedImage[][] getTileImages(BufferedImage img) {
        if (isFirstLevel(img)) {
            return getTileImages(img, 26, 418, 3, 7);
        } else {
            return getTileImages(img, 26, 58, 7, 7);
        }
    }

    private BufferedImage[][] getTileImages(BufferedImage img, int startX, int startY, int h, int w) {
        BufferedImage[][] r = new BufferedImage[h][w];
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                r[row][col] = img.getSubimage(startX + col * TILE_WIDTH, startY + row * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
            }
        }
        return r;
    }

    private boolean isFirstLevel(BufferedImage img) {
        BufferedImage actualImg = img.getSubimage(140, 290, 70, 50);
        BufferedImage firstLevelImg = tryy(() -> ImageIO.read(new File("detect-1-lvl.png")));
        double diff = getDifference(actualImg, firstLevelImg, Double.POSITIVE_INFINITY);
        return diff < 1000;
    }

    LevelType checkIntermission(BufferedImage img) {
        BufferedImage actualImg = img.getSubimage(140, 290, 70, 50);
        BufferedImage intermission1Img = tryy(() -> ImageIO.read(new File("detect-intermission1.png")));
        if (getDifference(actualImg, intermission1Img, Double.POSITIVE_INFINITY) < 100000) {
            return LevelType.INTERMISSION1;
        }
        BufferedImage intermission2Img = tryy(() -> ImageIO.read(new File("detect-intermission2.png")));
        if (getDifference(actualImg, intermission2Img, Double.POSITIVE_INFINITY) == 0) {
            return LevelType.INTERMISSION2;
        }
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
        writeImg(img, "marked_tiles", true);
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

        BufferedImage img = robot.createScreenCapture(new Rectangle(x, y, w, h));
        save(img);
        return img;
    }

    private void save(BufferedImage img) {
        File cur = new File("img.png");
        if (cur.exists()) {
            File prev = new File("prev-img.png");
            prev.delete();
            cur.renameTo(prev);
        }
        writeImg(img, "img", true);
    }

    private BufferedImage getImageFromFile() {
        return tryy(() -> ImageIO.read(new File("img.png")));
    }

    private static void writeImg(BufferedImage img, String name, boolean overwrite) {
        tryy(() -> {
            File file = new File(name + ".png");
            if (!overwrite && file.exists()) {
                throw new RuntimeException(name + " already exists");
            }
            return ImageIO.write(img, "png", new FileOutputStream(file));
        });
    }

    private static TileType findMostSimilar(Map<TileType, List<BufferedImage>> tiles, BufferedImage rectangle) {
        double min = Double.POSITIVE_INFINITY;
        TileType r = null;
        for (Map.Entry<TileType, List<BufferedImage>> entry : tiles.entrySet()) {
            TileType name = entry.getKey();
            for (BufferedImage img : entry.getValue()) {
                double diff = getDifference(img, rectangle, min);
                if (diff < min) {
                    min = diff;
                    r = name;
                }
            }
        }
        return r;
    }

    private static double getDifference(BufferedImage imageA, BufferedImage imageB, double maxAllowed) {
        double sum = 0;
        for (int i = 0; i < imageA.getWidth(); i++) {
            for (int j = 0; j < imageA.getHeight(); j++) {
                int colorA = imageA.getRGB(i, j);

                int redA = getRed(colorA);
                int greenA = getGreen(colorA);
                int blueA = getBlue(colorA);

                int colorB = imageB.getRGB(i, j);

                int redB = getRed(colorB);
                int greenB = getGreen(colorB);
                int blueB = getBlue(colorB);

                long diff = Math.abs(redA - redB) + Math.abs(greenA - greenB) + Math.abs(blueA - blueB);
                sum += diff;
                if (sum >= maxAllowed) {
                    return sum;
                }
            }
        }
        return sum;
    }

    private static int getBlue(int colourB) {
        return colourB & 0x000000ff;
    }

    private static int getGreen(int colourA) {
        return (colourA & 0x0000ff00) >> 8;
    }

    private static int getRed(int color) {
        return (color & 0x00ff0000) >> 16;
    }

    private static Map<TileType, List<BufferedImage>> loadTiles() {
        Map<TileType, List<BufferedImage>> r = new HashMap<>();
        for (File file : new File("tiles").listFiles()) {
            try {
                BufferedImage img = ImageIO.read(file);
                TileType name = getTileType(file.getName());
                List<BufferedImage> list = r.get(name);
                if (list == null) {
                    list = new ArrayList<>();
                    r.put(name, list);
                }
                list.add(img);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return r;
    }

    private static TileType getTileType(String fullName) {
        char c = fullName.contains("-") ? '-' : '.';
        String name = fullName.substring(0, fullName.indexOf(c));
        for (TileType type : values()) {
            if (name.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }
        throw new RuntimeException("tile tipe not found for name: " + name);
    }

    public static void main(String[] args) {
        GameStateReader gameStateReader = new GameStateReader();
        BufferedImage img = gameStateReader.getImageFromFile();
//        BufferedImage img = gameStateReader.getImageFromCapture();
        BufferedImage[][] tileImages = gameStateReader.getTileImages(img);

        gameStateReader.saveTile(tileImages[4][6], ROBO_MEDIKIT);

        /*BufferedImage img = new GameStateReader().getImageFromCapture();
        int cnt = 1;
        while (true) {
            String name = "tests/imgs/" + cnt;
            if (!(new File(name + ".png").exists())) {
                writeImg(img, name, false);
                break;
            }
            cnt++;
        }*/
    }
}
