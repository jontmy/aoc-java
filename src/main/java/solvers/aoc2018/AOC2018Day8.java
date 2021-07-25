package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AOC2018Day8 extends AOCDay<Integer> {
    private final List<Integer> license;

    public AOC2018Day8() throws IOException, URISyntaxException {
        super(8, 2018);
        assert input.size() == 1 : "Malformed input.";
        this.license = Arrays.stream(input.get(0).split(" "))
                .map(Integer::valueOf)
                .toList();
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var checksum = new AtomicInteger(0);
        findEndPoint(0, checksum);
        return checksum.get();
    }

    private int findEndPoint(int startPoint, AtomicInteger checksum) {
        var nChildren = license.get(startPoint);
        assert nChildren >= 0 : "Zero or more child nodes must be specified.";
        var nMetadata = license.get(startPoint + 1);
        assert nMetadata >= 1 : "One or more metadata entries must be specified.";
        var endPoint = startPoint + 2;

        // Base case: 0 child nodes.
        if (nChildren == 0) {
            var metadata = license.subList(endPoint, endPoint + nMetadata);
            var component = metadata.stream()
                    .reduce(Integer::sum)
                    .orElseThrow(() -> new AssertionError("Missing checksum."));
            checksum.addAndGet(component);
            return endPoint + nMetadata;
        }

        // Recursive case: 1 or more child nodes.
        for (int i = 0; i < nChildren; i++) {
            endPoint = findEndPoint(endPoint, checksum);
        }
        var metadata = license.subList(endPoint, endPoint + nMetadata);
        var component = metadata.stream()
                .reduce(Integer::sum)
                .orElseThrow(() -> new AssertionError("Missing checksum."));
        checksum.addAndGet(component);
        return endPoint + nMetadata;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
