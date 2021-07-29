package solvers.aoc2018.day15;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;

public class AOC2018Day15 extends AOCDay<Integer> {
    public AOC2018Day15() throws IOException, URISyntaxException {
        super(15, 2018);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var simulation = Simulation.of(input);
        var cavern = simulation.cavern();
        LOGGER.debug(cavern);
        var goblin = cavern.goblins().stream().min(Comparator.naturalOrder()).orElseThrow();
        var elf = cavern.elves().stream().max(Comparator.naturalOrder()).orElseThrow();
        LOGGER.info(goblin.pathfind(Cavern.ELF));
        LOGGER.info(elf.pathfind(Cavern.GOBLIN));
        for (int i = 0; i < 20; i++) {
            simulation.simulateRound();
            LOGGER.debug(cavern);
        }
        LOGGER.debug(cavern);
        return 0;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
