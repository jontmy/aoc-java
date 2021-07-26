package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("DuplicatedCode")
public class AOC2018Day11 extends AOCDay<String> {
    private static final int SIZE = 300;
    private final int serial;

    public AOC2018Day11() throws IOException, URISyntaxException {
        super(11, 2018);

        assert power(3, 5, 8) == 4;
        assert power(122, 79, 57) == -5;
        assert power(217, 196, 39) == 0;
        assert power(101, 153, 71) == 4;

        assert area(33, 45, 3, 18) == 29;
        assert area(21, 61, 3, 42) == 30;

        assert area(90 , 269, 16, 18) == 113;
        assert area(232, 251, 12, 42) == 119;

        assert input.size() == 1;
        this.serial = Integer.parseInt(input.get(0));
    }

    private static int power(int x, int y, int sn) {
        int id = x + 10;
        int power = (id * y + sn) * id;
        if (power < 100) return -5;
        else return (((power - (power % 100)) / 100) % 10) - 5;
    }

    private static int area(int x, int y, int len, int sn) {
        assert len > 0 && len <= SIZE : "Out of length bounds";
        assert x > 0 && x <= SIZE - len: "Out of horizontal bounds.";
        assert y > 0 && y <= SIZE - len: "Out of vertical bounds.";
        return IntStream.range(x, x + len)
                .map(i -> IntStream.range(y, y + len)
                        .map(j -> power(i, j, sn))
                        .sum())
                .sum();
    }

    @Override
    protected String solvePartOne(List<String> input) {
        int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE, power = Integer.MIN_VALUE;
        for (int i = 1; i < SIZE - 2; i++) {
            for (int j = 1; j < SIZE - 2; j++) {
                int p = area(i, j, 3, serial);
                if (p > power) {
                    power = p;
                    x = i;
                    y = j;
                }
            }
        }
        return x + "," + y;
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE, sz = Integer.MIN_VALUE, power = Integer.MIN_VALUE;
        for (int s = 1; s < SIZE; s++) {
            LOGGER.info(s);
            for (int i = 1; i < SIZE - s + 1; i++) {
                for (int j = 1; j < SIZE - s + 1; j++) {
                    int p = area(i, j, s, serial);
                    if (p > power) {
                        power = p;
                        x = i;
                        y = j;
                        sz = s;
                    }
                }
            }
        }
        return x + "," + y + "," + sz;
    }
}
