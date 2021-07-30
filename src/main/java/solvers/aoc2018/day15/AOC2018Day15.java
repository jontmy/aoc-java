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
        var simulation = Simulation.of(input, 3);
        return simulation.simulateToCompletion();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        for (int atk = 3;; atk++) {
            var simulation = Simulation.of(input, atk);
            var nElves = simulation.cavern().elves().size();
            var outcome = simulation.simulateToCompletion();
            if (simulation.cavern().elves().size() == nElves) return outcome;
        }
    }
}
