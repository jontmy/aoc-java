package solvers.aoc2018;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AOC2018Day1 extends AOCDay<String> {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(AOC2018Day1.class);

    public AOC2018Day1() throws IOException, URISyntaxException {
        super(1, 2018);
    }

    @Override
    protected String solvePartOne(List<String> input) {
        input.forEach(LOGGER::debug);
        return "UNSOLVED";
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        return "UNSOLVED";
    }
}
