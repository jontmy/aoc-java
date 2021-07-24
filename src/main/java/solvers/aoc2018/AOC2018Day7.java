package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.RegexUtils.*;

public class AOC2018Day7 extends AOCDay<String> {

    public AOC2018Day7() throws IOException, URISyntaxException {
        super(7, 2018);
    }

    // Creates a directed acyclic graph of steps (edges input -> outputs)
    private Map<Character, Set<Character>> parse(List<String> input) {
        var regex = join("Step ", group(ANY_CHARACTER),
                " must be finished before step ", group(ANY_CHARACTER), " can begin.");
        var pattern = Pattern.compile(regex);
        var steps = new TreeMap<Character, Set<Character>>();
        input.stream()
                .map(pattern::matcher)
                .flatMap(Matcher::results)
                .map(match -> Pair.of(match.group(1).charAt(0), match.group(2).charAt(0)))
                .forEach(pair -> steps.computeIfAbsent(pair.left(), k -> new TreeSet<>()).add(pair.right()));
        return steps;
    }

    // Depth-first search implementation of the puzzle requirements:
    // Determine the order in which the steps should be completed.
    // If more than one step is ready, choose the step which is first alphabetically.
    private Set<Character> build(Set<Character> built, Queue<Character> options, Map<Character, Set<Character>> outward, Map<Character, Set<Character>> inward) {
        while (!options.isEmpty()) {
            // Build the first step that comes first in alphabetical order.
            var building = options.remove();
            built.add(building);

            // Add the next options available that have fulfilled all pre-requisites to be added to the next round.
            outward.getOrDefault(building, Collections.emptySet()).stream()
                    .filter(option -> built.containsAll(inward.get(option)))
                    .forEach(options::add);
        }
        return built;
    }

    @Override
    protected String solvePartOne(List<String> input) {
        var outward = parse(input);

        // Find the vertices with an in-degree of 0.
        var inward = new HashMap<Character, Set<Character>>();
        outward.forEach((v, values) -> {
            inward.putIfAbsent(v, new TreeSet<>());
            for (Character k : values) { // values become keys due to inversion
                inward.putIfAbsent(k, new TreeSet<>());
                inward.get(k).add(v);
            }
        });
        // inward.forEach((k, v) -> LOGGER.debug("{} <- {}", k, v));
        var entry = inward.entrySet().stream()
                .filter(e -> e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(PriorityQueue::new));

        // Depth-first search until all vertices have an out-degree of 0.
        return build(new LinkedHashSet<>(), entry, outward, inward).stream()
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        return "";
    }
}
