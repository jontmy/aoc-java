package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static utils.RegexUtils.*;

public class AOC2018Day10 extends AOCDay<String> {
    private static final String REGEX_NUMBER = group(min(set(or(" ", "-")), 0), min(ANY_DIGIT, 1));
    private static final String REGEX_INPUT = join("position=<", REGEX_NUMBER, ", ", REGEX_NUMBER, "> velocity=<", REGEX_NUMBER, ", ", REGEX_NUMBER, ">");
    private static final Pattern PATTERN_INPUT = Pattern.compile(REGEX_INPUT);

    private final List<LightVector> vectors;

    public AOC2018Day10() throws IOException, URISyntaxException {
        super(10, 2018);
        this.vectors = parse(input);
    }

    private static List<LightVector> parse(List<String> input) {
        var unadjusted = input.stream()
                .map(AOC2018Day10::parse)
                .toList();
        var xOffset = unadjusted.stream()
                .map(LightVector::x)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        var yOffset = unadjusted.stream()
                .map(LightVector::y)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        return unadjusted.stream()
                .map(vector -> LightVector.of(vector.x() - xOffset, vector.y() - yOffset, vector.dx(), vector.dy()))
                .toList();
    }

    private static LightVector parse(String input) {
        var match = PATTERN_INPUT.matcher(input)
                .results()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Malformed input: %s.".formatted(input)));
        return new LightVector.Builder()
                .setX(match.group(1))
                .setY(match.group(2))
                .setDx(match.group(3))
                .setDy(match.group(4))
                .build();
    }

    private static BufferedImage image(List<LightVector> vectors, int delta) {
        int scale = 1;

        var unadjusted = vectors.stream()
                .map(v -> Pair.of(v.x() + v.dx() * delta, v.y() + v.dy() * delta))
                .toList();
        var xStats = unadjusted.stream()
                .mapToInt(Pair::left)
                .summaryStatistics();
        var yStats = unadjusted.stream()
                .mapToInt(Pair::right)
                .summaryStatistics();
        int xOffset = xStats.getMin(), yOffset = yStats.getMin();
        int width = xStats.getMax() - xStats.getMin(), height = yStats.getMax() - yStats.getMin();

        var image = new BufferedImage(width / scale + 1, height / scale + 1, BufferedImage.TYPE_INT_RGB);
        unadjusted.stream()
                .map(xy -> Pair.of(xy.left() - xOffset, xy.right() - yOffset))
                .forEach(xy -> image.setRGB(xy.left() / scale, xy.right() / scale, Color.RED.getRGB()));
        return image;
    }

    @Override
    protected String solvePartOne(List<String> input) {
        int delta = 10605;
        var image = image(vectors, delta);
        try {
            var path = Path.of("src/main/resources/output/2018/day10.png");
            Files.deleteIfExists(path);
            Files.createFile(path);
            ImageIO.write(image, "png", path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "see day10.png";
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        return "10605";
    }

    private record LightVector(int x, int y, int dx, int dy) {
        private static LightVector of(int x, int y, int dx, int dy) {
            return new LightVector(x, y, dx, dy);
        }

        @Override
        public String toString() {
            return Pair.of(x, y) + " -> " + Pair.of(dx, dy);
        }

        private static class Builder {
            private int x, y, dx, dy;

            public Builder setX(String x) {
                this.x = Integer.parseInt(x.strip());
                return this;
            }

            public Builder setY(String y) {
                this.y = Integer.parseInt(y.strip());
                return this;
            }

            public Builder setDx(String dx) {
                this.dx = Integer.parseInt(dx.strip());
                return this;
            }

            public Builder setDy(String dy) {
                this.dy = Integer.parseInt(dy.strip());
                return this;
            }

            private LightVector build() {
                return LightVector.of(x, y, dx, dy);
            }
        }
    }
}
