package fidel.interaction;

import java.awt.image.BufferedImage;

public class SimpleImage {
    private final int rgb[][];
    private final int height;
    private final int width;

    public SimpleImage(BufferedImage img) {
        height = img.getHeight();
        width = img.getWidth();
        rgb = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rgb[i][j] = img.getRGB(i, j);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRGB(int i, int j) {
        return rgb[i][j];
    }
}
