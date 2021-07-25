package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AOC2018Day8 extends AOCDay<Integer> {
    private final List<Integer> data;
    private final List<Node> license;

    public AOC2018Day8() throws IOException, URISyntaxException {
        super(8, 2018);
        assert input.size() == 1 : "Malformed input.";
        this.data = Arrays.stream(input.get(0).split(" "))
                .map(Integer::valueOf)
                .toList();
        this.license = new ArrayList<>();
        findEndPoint(0);
    }

    private int findEndPoint(int startPoint) {
        var nChildren = data.get(startPoint);
        assert nChildren >= 0 : "Zero or more child nodes must be specified.";
        var nMetadata = data.get(startPoint + 1);
        assert nMetadata >= 1 : "One or more metadata entries must be specified.";
        var endPoint = startPoint + 2;

        // Base case: 0 child nodes.
        if (nChildren == 0) {
            license.add(Node.of(startPoint, endPoint, endPoint, endPoint + nMetadata));
            return endPoint + nMetadata;
        }

        // Recursive case: 1 or more child nodes.
        for (int i = 0; i < nChildren; i++) {
            endPoint = findEndPoint(endPoint);
        }
        license.add(Node.of(startPoint, startPoint + 2, endPoint, endPoint + nMetadata));
        return endPoint + nMetadata;
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return license.stream()
                .map(node -> data.subList(node.metadataStart(), node.endPoint()))
                .flatMap(List::stream)
                .reduce(Integer::sum)
                .orElseThrow(() -> new AssertionError("Missing checksum."));
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }

    private record Node(int startPoint, int childStart, int metadataStart, int endPoint) {
        private static Node of(int startPoint, int childStart, int metadataStart, int endPoint) {
            return new Node(startPoint, endPoint, metadataStart, endPoint);
        }
    }
}
