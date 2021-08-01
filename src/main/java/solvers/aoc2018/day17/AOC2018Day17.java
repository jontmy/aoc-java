package solvers.aoc2018.day17;

import solvers.AOCDay;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.regex.Pattern.compile;
import static utils.RegexUtils.*;

public class AOC2018Day17 extends AOCDay<Integer> {
    private static final Pattern REGEX_LINE;

    static {
        var REGEX_AXIS = group(or("x", "y"));
        var REGEX_NUMBER = group(min(ANY_DIGIT, 1));
        var REGEX_RANGE = join(REGEX_NUMBER, "..", REGEX_NUMBER);
        REGEX_LINE = compile(join(REGEX_AXIS, "=", REGEX_NUMBER, ", ", REGEX_AXIS, "=", REGEX_RANGE));
    }

    public AOC2018Day17() throws IOException, URISyntaxException {
        super(17, 2018);
        var waterfall = parse();
        try {
            var path = Path.of("src/main/resources/output/2018/day17/initial.png");
            Files.deleteIfExists(path);
            Files.createFile(path);
            ImageIO.write(waterfall.toImage(), "png", path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Waterfall parse() {
        record Line(int x1, int y1, int x2, int y2) {
            static Line parse(char ax1, int val, char ax2, int lo, int hi) {
                assert ax1 != ax2;
                assert ax1 == 'x' || ax1 == 'y';
                assert ax2 == 'x' || ax2 == 'y';
                assert val >= 0 && lo >= 0 && hi >= 0 && lo < hi;
                int x1, y1, x2, y2;

                // Parse vertical lines.
                if (ax1 == 'x') {
                    x1 = val;
                    x2 = val;
                    y1 = lo;
                    y2 = hi;
                }
                // Parse horizontal lines.
                else {
                    y1 = val;
                    y2 = val;
                    x1 = lo;
                    x2 = hi;
                }
                return new Line(x1, y1, x2, y2);
            }

            Line translate(int dx, int dy) {
                return new Line(Line.this.x1 + dx,
                        Line.this.y1 + dy,
                        Line.this.x2 + dx,
                        Line.this.y2 + dy
                );
            }

            IntStream xs() {
                return IntStream.of(x1, x2);
            }

            IntStream ys() {
                return IntStream.of(y1, y2);
            }
        }

        // Parse each line into a human-readable data structure.
        var lines = super.input.stream()
                .map(REGEX_LINE::matcher)
                .flatMap(Matcher::results)
                .map(match -> Line.parse(match.group(1).charAt(0),
                        Integer.parseInt(match.group(2)),
                        match.group(3).charAt(0),
                        Integer.parseInt(match.group(4)),
                        Integer.parseInt(match.group(5))))
                .toList();

        // Find the minimum and maximum x and y values.
        var xStats = lines.stream()
                .flatMapToInt(Line::xs)
                .summaryStatistics();
        var yStats = lines.stream()
                .flatMapToInt(Line::ys)
                .summaryStatistics();
        var xMin = xStats.getMin();
        var xMax = xStats.getMax();
        var yMin = yStats.getMin();
        var yMax = yStats.getMax();

        // Translate all the lines such that (xMin, yMin) is the origin.
        var translated = lines.stream()
                .map(line -> line.translate(-xMin, -yMin))
                .toList();

        // Create the two-dimensional vertical slice of the ground.
        var width = xMax - xMin + 1; // xMax is an inclusive upper bound
        var height = yMax - yMin + 1; // yMax is an inclusive upper bound
        width += 2; // provide a 1px buffer at each of the horizontal bounds
        var slice = new char[width][height];

        for (Line line : translated) {
            assert line.x1() == line.x2() || line.y1() == line.y2() : "Lines must either be horizontal or vertical.";
            assert line.x1() <= line.x2();
            assert line.y1() <= line.y2();

            for (int x = line.x1(); x <= line.x2(); x++) {
                for (int y = line.y1(); y <= line.y2(); y++) {
                    slice[x + 1][y] = '#'; // compensating for the 1px buffer at each of the horizontal bounds
                }
            }
        }
        var sourceX = 500 - xMin + 1;
        slice[sourceX][0] = '|';
        return Waterfall.of(slice, width, height, sourceX);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return 0;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
