package solvers.aoc2018.day17;

import java.awt.*;
import java.awt.image.BufferedImage;

record Waterfall(char[][] slice, int width, int height) {
    protected static Waterfall of(char[][] slice, int width, int height) {
        return new Waterfall(slice, width, height);
    }

    protected static Waterfall from(Waterfall source) {
        var copy = new char[source.width()][source.height()];
        for (int x = 0; x < source.width(); x++) {
            System.arraycopy(source.slice()[x], 0, copy[x], 0, source.height());
        }
        return new Waterfall(copy, source.width(), source.height());
    }

    protected BufferedImage toImage() {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color;
                if (slice[x][y] == '#') color = Color.BLACK.getRGB();
                else color = Color.WHITE.getRGB();
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(width).append(" x ").append(height).append(" waterfall:\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(slice[x][y]);

            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
