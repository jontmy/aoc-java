package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.RegexUtils.*;

@SuppressWarnings("DuplicatedCode")
public class AOC2018Day7 extends AOCDay<String> {
    private final Map<Character, Set<Character>> inward, outward;
    private final List<Character> entry;

    public AOC2018Day7() throws IOException, URISyntaxException {
        super(7, 2018);
        this.outward = parse(input);
        this.inward = new HashMap<>();
        outward.forEach((v, values) -> {
            inward.putIfAbsent(v, new TreeSet<>());
            for (Character k : values) { // values become keys due to inversion
                inward.putIfAbsent(k, new TreeSet<>());
                inward.get(k).add(v);
            }
        });
        this.entry = inward.entrySet().stream() // vertices with an in-degree of 0.
                .filter(e -> e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();
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
    private Set<Character> buildSequential(Set<Character> built, Queue<Character> options) {
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
        return buildSequential(new LinkedHashSet<>(), new PriorityQueue<>(entry)).stream()
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private int buildParallel(Character endpoint) {
        var built = new LinkedHashMap<Character, Integer>();
        var options = new PriorityQueue<>(entry);
        var pool = new WorkerPool();

        while (!options.isEmpty()) {
            assert pool.availableWorkers() > 0 : "No available workers.";
            assert pool.currentSecond() < 10000 : "Timeout.";
            var successfullyQueued = new ArrayList<Character>();
            var unsuccessfullyQueued = new ArrayList<Character>();

            // Delegate a step that comes first in alphabetical order to an available worker.
            while (pool.availableWorkers() > 0 && !options.isEmpty()) {
                var option = options.remove();
                if (optionFulfilsAllPrerequisites(option, built, pool.currentSecond())) {
                    successfullyQueued.add(option);
                    built.put(option, pool.delegate(option));
                } else {
                    unsuccessfullyQueued.add(option);
                }
            }
            options.addAll(unsuccessfullyQueued);

            // Simulate seconds passing until another worker is available.
            pool.advanceToNextCompletion();

            // Add the next options available that have fulfilled all pre-requisites to be added to the next round.
            successfullyQueued.stream()
                    .map(justBuilt -> outward.getOrDefault(justBuilt, Collections.emptySet()))
                    .flatMap(Set::stream)
                    .filter(Predicate.not(options::contains))
                    .forEach(options::add);
        }
        pool.advanceToFullCompletion();
        assert built.keySet().stream()
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString()
                .endsWith(String.valueOf(endpoint)) : "Failed to complete correctly.";
        return pool.currentSecond();
    }

    private boolean optionFulfilsAllPrerequisites(Character option, Map<Character, Integer> built, int currentSecond) {
        var prerequisites = inward.get(option);
        if (!built.keySet().containsAll(prerequisites)) return false;
        for (Character prerequisite : prerequisites) {
            var completion = built.get(prerequisite);
            if (completion > currentSecond) return false;
        }
        return true;
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        var endpoints = inward.keySet().stream()
                .filter(c -> outward.getOrDefault(c, Collections.emptySet()).size() == 0)
                .toList();
        assert endpoints.size() == 1 : "More than 1 endpoint.";
        var endpoint = endpoints.get(0);
        return String.valueOf(buildParallel(endpoint));
    }

    private static final class WorkerPool {
        private static final int CAPACITY = 5;
        private final Set<Integer> workers;
        private int second;

        private WorkerPool() {
            this.workers = new HashSet<>(CAPACITY);
            this.second = 0;
        }

        private static int secondsRequired(char c) {
            assert ((int) c) >= 65 && ((int) c) <= 90;
            return 60 + ((int) c) - 64;
        }

        private void advanceToNextCompletion() {
            second = workers.stream().min(Comparator.naturalOrder()).orElse(currentSecond());
            workers.removeIf(worker -> worker <= second);
            assert availableWorkers() > 0 : "All workers are still unavailable.";
        }

        private void advanceToFullCompletion() {
            second = workers.stream().max(Comparator.naturalOrder()).orElse(currentSecond());
            workers.clear();
        }

        private int delegate(char task) {
            assert availableWorkers() > 0 : "No available workers.";
            var completion = second + secondsRequired(task);
            workers.add(completion);
            return completion;
        }

        private int availableWorkers() {
            return CAPACITY - workers.size();
        }

        public int currentSecond() {
            return second;
        }
    }
}
