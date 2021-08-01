package solvers.aoc2018.day17;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import utils.Coordinates;
import utils.StreamUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.stream.IntStream;

record Waterfall(char[][] slice, int width, int height, ArrayDeque<Coordinates> sources) {
    protected static final Logger LOGGER = (Logger) LogManager.getLogger(Waterfall.class);
    protected static final char SAND = '.', CLAY = '#', FLOW = '|', WATER = '~';

    protected static Waterfall of(char[][] slice, int width, int height, int sourceX) {
        var sources = new ArrayDeque<Coordinates>();
        sources.add(Coordinates.at(sourceX, 0));
        return new Waterfall(slice, width, height, sources);
    }

    protected static Waterfall from(Waterfall source) {
        var copy = new char[source.width()][source.height()];
        for (int x = 0; x < source.width(); x++) {
            System.arraycopy(source.slice()[x], 0, copy[x], 0, source.height());
        }
        return new Waterfall(copy, source.width(), source.height(), new ArrayDeque<>(source.sources()));
    }

    protected char get(int x, int y) {
        return slice[x][y];
    }

    protected void set(int x, int y, char val) {
        slice[x][y] = val;
    }

    // Returns the number of tiles that can be reached by the water.
    protected int waterlogged() {
        var waterlogged = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var c = get(x, y);
                if (c == WATER || c == FLOW) waterlogged++;
            }
        }
        return waterlogged;
    }

    // Returns the number of tiles that won't drain out.
    protected int retained() {
        var retained = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var c = get(x, y);
                if (c == WATER) retained++;
            }
        }
        return retained;
    }

    // Causes all water source blocks to flow and fill the reservoirs in their downward path until they overflow.
    protected void flow() {
        int j = 0;
        while (!sources.isEmpty()) {
            LOGGER.debug(sources.size());
            var n = sources.size();
            for (int i = 0; i < n; i++) {
                var source = sources.remove();
                flow(source);
            }
        }
    }

    // Causes the specified water source block to flow and fill the reservoirs in their downward path until they overflow.
    protected void flow(Coordinates source) {
        assert source.y() < height;

        // Find the base of the reservoir, if one exists.
        var base = IntStream.range(source.y(), height)
                .filter(y -> get(source.x(), y) == CLAY)
                .findFirst()
                .orElse(height);

        // Water flows downward through the sand until it reaches either the base of the reservoir or the bottom.
        IntStream.range(source.y(), base).forEach(y -> set(source.x(), y, FLOW));
        if (base == height) return;

        // If the water source lands on an edge of a reservoir, create new water source blocks adjacent to it.
        if (get(source.x() - 1, base) != CLAY && get(source.x() + 1, base) != CLAY) {
            LOGGER.warn(source.x());
            sources.add(Coordinates.at(source.x() - 1, base - 1));
            sources.add(Coordinates.at(source.x() + 1, base - 1));
            return;
        }

        // Otherwise, the reservoir exists, so it fills with water until it overflows.
        // Find the coordinates of the edges of the reservoirs.
        var reservoirLeftX = StreamUtils.reversedRangeClosed(0, source.x())
                .filter(x -> get(x, base) != CLAY)
                .findFirst()
                .orElseThrow() + 1;
        var reservoirLeftY = StreamUtils.reversedRangeClosed(0, base)
                .filter(y -> get(reservoirLeftX, y) != CLAY)
                .findFirst()
                .orElseThrow() + 1;
        var left = Coordinates.at(reservoirLeftX, reservoirLeftY);
        var reservoirRightX = IntStream.range(source.x(), width)
                .filter(x -> get(x, base) != CLAY)
                .findFirst()
                .orElseThrow() - 1;
        var reservoirRightY = StreamUtils.reversedRangeClosed(0, base)
                .filter(y -> get(reservoirRightX, y) != CLAY)
                .findFirst()
                .orElseThrow() + 1;
        var right = Coordinates.at(reservoirRightX, reservoirRightY);

        var fillY = Math.max(left.y(), right.y()); // the minimum depth that the reservoir must fill up to
        var overflowY = fillY - 1; // the minimum depth at which reservoir overflow can occur
        // Check whether the reservoir overflows at each depth.
        for (int y = base - 1; y >= 0; y--) {
            // Settle the water at that depth.
            set(source.x(), y, WATER);
            for (int x = source.x() - 1; x > left.x(); x--) {
                if (get(x, y) == CLAY) break;
                else set(x, y, WATER);
            }
            for (int x = source.x() + 1; x < right.x(); x++) {
                if (get(x, y) == CLAY) break;
                else set(x, y, WATER);
            }

            // Check for reservoir overflow at that depth.
            if (y > overflowY) continue; // overflowing cannot occur before this depth - see declaration above
            var overflow = false;
            if (y < left.y() && get(left.x() + 1, y) == WATER) {
                // Overflow at the left reservoir edge.
                overflow = true;
                sources.add(Coordinates.at(left.x() - 1, y));
            }
            if (y < right.y() && get(right.x() - 1, y) == WATER) {
                // Overflow at the right reservoir edge.
                overflow = true;
                sources.add(Coordinates.at(right.x() + 1, y));
            }
            if (overflow) {
                set(source.x(), y, FLOW);
                for (int x = source.x() - 1; x >= left.x(); x--) {
                    if (get(x, y) == CLAY) break;
                    else set(x, y, FLOW);
                    for (int j = y + 1; j < base; j++) {
                        if (get(x, j) == CLAY) break;
                        else set(x, j, WATER);
                    }
                }
                for (int x = source.x() + 1; x <= right.x(); x++) {
                    if (get(x, y) == CLAY) break;
                    else set(x, y, FLOW);
                    for (int j = y + 1; j < base; j++) {
                        if (get(x, j) == CLAY) break;
                        else set(x, j, WATER);
                    }
                }
                return;
            }
        }
    }

    protected BufferedImage toImage() {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = switch (slice[x][y]) {
                    case SAND -> Color.HSBtoRGB(0.13F, 0.20F, 1.00F);
                    case CLAY -> Color.BLACK.getRGB();
                    case '|' -> Color.HSBtoRGB(0.56F, 0.40F, 0.90F);
                    case '~' -> Color.HSBtoRGB(0.60F, 0.50F, 0.90F);
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
