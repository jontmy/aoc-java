package solvers.aoc2018.day17;

import utils.Coordinates;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

record Waterfall(char[][] slice, int width, int height, ArrayList<Coordinates> sources) {
    protected static Waterfall of(char[][] slice, int width, int height, int sourceX) {
        var sources = new ArrayList<Coordinates>();
        sources.add(Coordinates.at(sourceX, 0));
        return new Waterfall(slice, width, height, sources);
    }

    protected static Waterfall from(Waterfall source) {
        var copy = new char[source.width()][source.height()];
        for (int x = 0; x < source.width(); x++) {
            System.arraycopy(source.slice()[x], 0, copy[x], 0, source.height());
        }
        return new Waterfall(copy, source.width(), source.height(), new ArrayList<>(source.sources()));
    }

    protected BufferedImage toImage() {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = switch (slice[x][y]) {
                    case '#' -> Color.BLACK.getRGB();
                    case '|' -> Color.BLUE.getRGB();
                    default -> Color.WHITE.getRGB();
                };
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
