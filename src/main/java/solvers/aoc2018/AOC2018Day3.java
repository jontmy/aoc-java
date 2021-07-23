package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static utils.RegexUtils.*;

public class AOC2018Day3 extends AOCDay<Integer> {
    public AOC2018Day3() throws IOException, URISyntaxException {
        super(3, 2018);
    }

    private static List<Rectangle> parse(List<String> input) {
        return input.stream()
                .map(AOC2018Day3::parse)
                .toList();
    }

    private static Rectangle parse(String input) {
        var regex = join("#",
                min(ANY_DIGIT, 1), " @ ",
                group(min(ANY_DIGIT, 1)), ",",
                group(min(ANY_DIGIT, 1)), ": ",
                group(min(ANY_DIGIT, 1)), "x",
                group(min(ANY_DIGIT, 1))
        );
        var result = Pattern.compile(regex)
                .matcher(input)
                .results()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Regex parsing failed - missing result."));
        assert result.groupCount() == 4 : "Regex parsing failed - missing capture groups.";
        var x = Integer.parseInt(result.group(1));
        var y = Integer.parseInt(result.group(2));
        var w = Integer.parseInt(result.group(3));
        var h = Integer.parseInt(result.group(4));
        return Rectangle.of(x, y, w, h);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var rectangles = parse(input);
        return 0;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }

    private record Rectangle(int x, int y, int w, int h) {

        private static Rectangle of(int x, int y, int w, int h) {
            return new Rectangle(x, y, w, h);
        }

        @SuppressWarnings("DuplicatedCode")
        private Rectangle intersect(Rectangle that) {
            Objects.requireNonNull(that);
            var x = Math.max(this.x, that.x);
            var y = Math.max(this.y, that.y);
            var w = Math.min(this.x + this.w, that.x + that.w) - x;
            var h = Math.min(this.y + this.h, that.y + that.h) - y;
            return Rectangle.of(x, y, w, h);
        }

        @SuppressWarnings("DuplicatedCode")
        private Rectangle union(Rectangle that) {
            Objects.requireNonNull(that);
            var x = Math.min(this.x, that.x);
            var y = Math.min(this.y, that.y);
            var w = Math.max(this.x + this.w, that.x + that.w) - x;
            var h = Math.max(this.y + this.h, that.y + that.h) - y;
            return Rectangle.of(x, y, w, h);
        }

        private int area() {
            return w * h;
        }
    }
}
