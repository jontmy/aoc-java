package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AOC2018Day2 extends AOCDay<String> {
    public AOC2018Day2() throws IOException, URISyntaxException {
        super(2, 2018);
    }

    private static boolean check(String a, String b) {
        assert a.length() == b.length() : "Strings are not of the same length.";
        var difference = IntStream.range(0, a.length())
                .filter(i -> a.charAt(i) != b.charAt(i))
                .count();
        return difference == 1;
    }

    @Override
    protected String solvePartOne(List<String> input) {
        var letterCounts = input.stream()
                .map(String::chars)
                .map(chars -> chars.mapToObj(c -> (char) c)
                        .collect(Collectors.groupingBy(c -> c, Collectors.counting())))
                .map(Map::values)
                .toList();
        var appearsTwice = letterCounts.stream()
                .filter(counts -> counts.contains(2L))
                .count();
        var appearsThrice = letterCounts.stream()
                .filter(counts -> counts.contains(3L))
                .count();
        return String.valueOf(appearsTwice * appearsThrice);
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            var a = input.get(i);
            for (int j = i + 1; j < input.size(); j++) {
                var b = input.get(j);
                if (!check(a, b)) continue;
                return IntStream.range(0, a.length())
                        .filter(k -> a.charAt(k) == b.charAt(k))
                        .mapToObj(a::charAt)
                        .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                        .toString();
            }
        }
        throw new AssertionError("Solution not found.");
    }
}
