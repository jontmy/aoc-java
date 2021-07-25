package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AOC2018Day8 extends AOCDay<Integer> {
    private final List<Integer> data;
    private final TreeMap<Integer, Node> license;

    public AOC2018Day8() throws IOException, URISyntaxException {
        super(8, 2018);
        assert input.size() == 1 : "Malformed input.";
        this.data = Arrays.stream(input.get(0).split(" "))
                .map(Integer::valueOf)
                .toList();
        this.license = new TreeMap<>();
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
            license.put(startPoint, Node.of(startPoint, endPoint, endPoint, endPoint + nMetadata));
            return endPoint + nMetadata;
        }

        // Recursive case: 1 or more child nodes.
        for (int i = 0; i < nChildren; i++) {
            endPoint = findEndPoint(endPoint);
        }
        license.put(startPoint, Node.of(startPoint, startPoint + 2, endPoint, endPoint + nMetadata));
        return endPoint + nMetadata;
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return license.values().stream()
                .map(node -> data.subList(node.metadataStart(), node.endPoint()))
                .flatMap(List::stream)
                .reduce(Integer::sum)
                .orElseThrow(() -> new AssertionError("Missing checksum."));
    }

    // Returns the list of nodes that are children (excluding further descendants) of a parent node.
    private List<Node> children(Node parent) {
        var children = new ArrayList<Node>();
        for (int lo = parent.childStart(); lo < parent.metadataStart(); ) {
            var child = license.ceilingEntry(lo);
            if (child == null) break;
            lo = child.getValue().endPoint();
            children.add(child.getValue());
        }
        assert children.isEmpty() || children.get(children.size() - 1).endPoint() == parent.metadataStart() : "Incorrect children derivation.";
        return children;
    }

    private int calculate(Node parent, Map<Node, List<Node>> relationships, Map<Node, Integer> memoized) {
        assert relationships.containsKey(parent) : "Parent not in relationships map.";

        // Base case 1: parent node value has been calculated previously and is memoized.
        if (memoized.containsKey(parent)) {
            return memoized.get(parent);
        }
        // Base case 2: parent node has no child nodes -> the value is the sum of its metadata entries.
        int value;
        var metadata = data.subList(parent.metadataStart(), parent.endPoint());
        if (relationships.get(parent).isEmpty()) {
            value = metadata.stream()
                    .reduce(Integer::sum)
                    .orElseThrow();
        }
        // Recursive case: parent node has child nodes -> referenced by the metadata entries.
        else {
            var children = relationships.get(parent);
            var indices = metadata.stream()
                    .peek(i -> { assert i >= 0; })
                    // Metadata entries become indices, '0' does not refer to any child node.
                    .filter(i -> i != 0)
                    // Entry of 1 refers to the first child node (convert 1-indexing to 0-indexing).
                    .map(i -> i - 1)
                    // If a referenced child node does not exist, that reference is skipped.
                    .filter(i -> i < children.size());
            value = indices.map(children::get)
                    // Map the value of each child node via a recursive call.
                    .map(child -> calculate(child, relationships, memoized))
                    // The value of the parent node is the sum of the values of the child nodes.
                    .reduce(Integer::sum)
                    .orElse(0);
        }
        assert value >= 0 : "Negative value calculated.";
        memoized.put(parent, value);
        return value;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        var relationships = license.values().stream()
                .collect(Collectors.toMap(parent -> parent, this::children, (a, b) -> a, TreeMap::new));
        var values = new HashMap<Node, Integer>();
        return calculate(relationships.firstKey(), relationships, values);
    }

    private record Node(int startPoint, int childStart, int metadataStart, int endPoint) implements Comparable<Node> {
        private static Node of(int startPoint, int childStart, int metadataStart, int endPoint) {
            return new Node(startPoint, childStart, metadataStart, endPoint);
        }

        @Override
        public int compareTo(Node that) {
            return Comparator.comparing(Node::startPoint).compare(this, that);
        }

        @Override
        public String toString() {
            return "(%d -> %d)".formatted(startPoint, endPoint);
        }
    }
}
