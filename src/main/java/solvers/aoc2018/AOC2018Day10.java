package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;
import utils.RegexUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import static utils.RegexUtils.*;

public class AOC2018Day10 extends AOCDay<String> {
    private static final String REGEX_NUMBER = group(set(or(" ", "-")), min(ANY_DIGIT , 1));
    private static final String REGEX_INPUT = join("position=<", REGEX_NUMBER, ", ", REGEX_NUMBER, "> velocity=<", REGEX_NUMBER, ", ", REGEX_NUMBER, ">");
    private static final Pattern PATTERN_INPUT = Pattern.compile(REGEX_INPUT);

    private final List<LightVector> vectors;

    public AOC2018Day10() throws IOException, URISyntaxException {
        super(10, 2018);
        this.vectors = parse(input);
    }

    private static List<LightVector> parse(List<String> input) {
        return input.stream()
                .map(AOC2018Day10::parse)
                .toList();
    }

    private static LightVector parse(String input) {
        var match = PATTERN_INPUT.matcher(input)
                .results()
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Malformed input."));
        return new LightVector.Builder()
                .setX(match.group(1))
                .setY(match.group(2))
                .setDx(match.group(3))
                .setDy(match.group(4))
                .build();
    }

    @Override
    protected String solvePartOne(List<String> input) {
        return "";
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        return "";
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
