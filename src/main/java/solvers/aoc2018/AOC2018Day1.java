package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;

public class AOC2018Day1 extends AOCDay<Integer> {
    public AOC2018Day1() throws IOException, URISyntaxException {
        super(1, 2018);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return input.stream()
                .mapToInt(Integer::parseInt)
                .sum();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        var changes = input.stream()
                .map(Integer::valueOf)
                .toList();
        var frequencies = new HashSet<Integer>();
        var current = 0;
        frequencies.add(current);
        for (int i = 0; ; i++) {
            current += changes.get(i % changes.size());
            if (frequencies.contains(current)) break;
            else frequencies.add(current);
        }
        return current;
    }
}
