package solvers.aoc2018.day18;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AOC2018Day18 extends AOCDay<Integer> {
    private final Landscape initial;

    public AOC2018Day18() throws IOException, URISyntaxException {
        super(18, 2018);
        this.initial = Landscape.parse(super.input);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var landscape = Landscape.from(initial).derive(10);
        int forested = 0, lumberyards = 0;
        for (int x = 0; x < landscape.width(); x++) {
            for (int y = 0; y < landscape.height(); y++) {
                char c = landscape.area()[x][y];
                if (c == Landscape.FORESTED) forested++;
                else if (c == Landscape.LUMBERYARD) lumberyards++;
            }
        }
        return forested * lumberyards;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
