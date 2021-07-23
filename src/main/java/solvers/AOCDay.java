package solvers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import utils.Benchmark;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AOCDay<R> {
    protected static final Logger LOGGER = (Logger) LogManager.getLogger(AOCDay.class);
    private final List<String> input;
    private final int day, year;

    protected AOCDay(int day, int year) throws IOException, URISyntaxException {
        this.day = day;
        this.year = year;

        var url = Objects.requireNonNull(getClass().getResource("/input/%d/day%d.txt".formatted(year, day)));
        var uri = url.toURI();
        try {
            FileSystems.getFileSystem(uri);
        } catch (IllegalArgumentException | FileSystemNotFoundException e) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            try {
                FileSystems.newFileSystem(uri, env);
            } catch (IllegalArgumentException f) {
                FileSystems.getDefault();
            }
        }
        final var path = Path.of(uri);
        this.input = Files.readAllLines(path);
    }

    public void solve() throws IOException {
        var partOneBenchmark = Benchmark.of(input, this::solvePartOne);
        var partOneResults = partOneBenchmark.run(1);
        LOGGER.info("Advent of Code {}, day {}, part 1 -> {} (took {} ms)", year, day, partOneResults.left(), partOneResults.right());

        var partTwoBenchmark = Benchmark.of(input, this::solvePartTwo);
        var partTwoResults = partTwoBenchmark.run(1);
        LOGGER.info("Advent of Code {}, day {}, part 2 -> {} (took {} ms)", year, day, partTwoResults.left(), partTwoResults.right());

        var path = Path.of("src/main/resources/output/%d/day%d.txt".formatted(year, day));
        Files.deleteIfExists(path);
        Files.createFile(path);
        Files.write(path, List.of(partTwoResults.left().toString(), partTwoResults.left().toString()));
    }

    protected abstract R solvePartOne(List<String> input);

    protected abstract R solvePartTwo(List<String> input);
}
