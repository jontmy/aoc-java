package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static utils.RegexUtils.*;

public class AOC2018Day3 extends AOCDay<Integer> {
    private static final int UNCLAIMED = 0, CLAIMED = 1, DISPUTED = 2;

    public AOC2018Day3() throws IOException, URISyntaxException {
        super(3, 2018);
    }

    private static List<Claim> parse(List<String> input) {
        return input.stream()
                .map(AOC2018Day3::parse)
                .toList();
    }

    private static Claim parse(String input) {
        var regex = join("#",
                group(min(ANY_DIGIT, 1)), " @ ",
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
        assert result.groupCount() == 5 : "Regex parsing failed - missing capture groups.";
        var id = Integer.parseInt(result.group(1));
        var x = Integer.parseInt(result.group(2));
        var y = Integer.parseInt(result.group(3));
        var w = Integer.parseInt(result.group(4));
        var h = Integer.parseInt(result.group(5));
        return Claim.of(id, x, y, w, h);
    }

    private static byte[][] claim(byte[][] fabric, Rectangle claim) {
        for (int i = claim.x; i < claim.x + claim.w; i++) {
            for (int j = claim.y; j < claim.y + claim.h; j++) {
                switch (fabric[i][j]) {
                    case UNCLAIMED -> fabric[i][j] = CLAIMED;
                    case CLAIMED -> fabric[i][j] = DISPUTED;
                }
            }
        }
        return fabric;
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var claims = parse(input);
        var fabric = claims.stream()
                .map(Claim::area)
                .reduce(Rectangle::union)
                .map(r -> new byte[r.x + r.w][r.y + r.h])
                .orElseThrow(() -> new AssertionError("Fabric creation failed."));
        fabric = claims.stream()
                .map(Claim::area)
                .reduce(fabric, AOC2018Day3::claim, (a, b) -> a);
        var disputed = 0;
        for (byte[] row : fabric) {
            for (byte b : row) {
                if (b == DISPUTED) disputed++;
            }
        }
        return disputed;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        var claims = parse(input);
        for (int i = 0; i < claims.size(); i++) {
            var claim = claims.get(i);
            var rectangle = claim.area;
            var undisputed = Stream.concat(claims.subList(0, i).stream(), claims.subList(i + 1, claims.size()).stream())
                    .map(Claim::area)
                    .noneMatch(rectangle::intersects);
            if (undisputed) return claim.id;
        }
        throw new AssertionError("Undisputed claim not found.");
    }

    private record Claim(int id, Rectangle area) {
        private static Claim of(int id, int x, int y, int w, int h) {
            return new Claim(id, Rectangle.of(x, y, w, h));
        }
    }

    private record Rectangle(int x, int y, int w, int h) {
        private static Rectangle of(int x, int y, int w, int h) {
            return new Rectangle(x, y, w, h);
        }

        @SuppressWarnings("DuplicatedCode")
        private Optional<Rectangle> intersect(Rectangle that) {
            Objects.requireNonNull(that);
            var x = Math.max(this.x, that.x);
            var y = Math.max(this.y, that.y);
            var w = Math.min(this.x + this.w, that.x + that.w) - x;
            var h = Math.min(this.y + this.h, that.y + that.h) - y;
            if (w > 0 && h > 0) return Optional.of(Rectangle.of(x, y, w, h));
            else return Optional.empty();
        }

        private boolean intersects(Rectangle that) {
            return this.intersect(that).isPresent();
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
    }
}
