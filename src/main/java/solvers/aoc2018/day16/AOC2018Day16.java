package solvers.aoc2018.day16;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public class AOC2018Day16 extends AOCDay<Integer> {
    private final List<Simulator> simulators;

    public AOC2018Day16() throws IOException, URISyntaxException {
        super(16, 2018);
        this.simulators = new ArrayList<>();
        parse();
    }

    private void parse() {
        var split = IntStream.range(0, input.size())
                .filter(i -> input.get(i).startsWith("A"))
                .max()
                .orElseThrow();

        // Parse the input for the first part.
        var first = IntStream.rangeClosed(0, split)
                .mapToObj(input::get)
                .filter(not(String::isBlank))
                .toList();
        for (int i = 0; i < first.size(); ) {
            var before = Registers.parse(first.get(i++));
            var instruction = Instruction.parse(first.get(i++));
            var after = Registers.parse(first.get(i++));
            simulators.add(new Simulator(before, instruction, after));
        }
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return (int) simulators.stream()
                .map(Simulator::operations)
                .filter(ops -> ops.size() >= 3)
                .count();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
