package solvers.aoc2018.day18;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

public class AOC2018Day18 extends AOCDay<Integer> {
    private final Landscape initial;

    public AOC2018Day18() throws IOException, URISyntaxException {
        super(18, 2018);
        this.initial = Landscape.parse(super.input);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return Landscape.from(initial)
                .derive(10)
                .value();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        final var MULTIPLIER = 1000;
        final var GENERATIONS = 1000000000L;

        var stabilized = Landscape.from(initial).derive(MULTIPLIER);
        var baseline = stabilized.value();

        // Find the periodicity of the landscape.
        var periodicity = Stream.iterate(stabilized.derive(MULTIPLIER), ls -> ls.derive(MULTIPLIER))
                .map(Landscape::value)
                .takeWhile(val -> val != baseline)
                .count();

        // Account for the multiplier in the periodicity.
        periodicity += 1;
        periodicity *= MULTIPLIER;

        return Landscape.from(initial)
                .derive((int) (GENERATIONS % periodicity))
                .value();
    }
}
