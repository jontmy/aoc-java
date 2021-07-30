package solvers.aoc2018.day15;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AOC2018Day15 extends AOCDay<Integer> {
    public AOC2018Day15() throws IOException, URISyntaxException {
        super(15, 2018);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var simulation = Simulation.of(input);
        return simulation.simulateToCompletion();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
