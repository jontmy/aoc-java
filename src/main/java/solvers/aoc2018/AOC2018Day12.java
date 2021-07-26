package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static utils.RegexUtils.*;

public class AOC2018Day12 extends AOCDay<Long> {
    private static final char ALIVE = '#', DEAD = '.';
    private final Map<Integer, Character> state;
    private final Set<String> lives, dies;
    private int lo, hi;

    public AOC2018Day12() throws IOException, URISyntaxException {
        super(12, 2018);
        assert input.size() > 1;
        this.state = new HashMap<>();
        parseInitialState(state, input.get(0));
        this.lives = new HashSet<>();
        this.dies = new HashSet<>();
        parseRules(lives, dies, input.subList(1, input.size()));
        assert lives.size() + dies.size() == 32;
    }

    private static void parseInitialState(Map<Integer, Character> state, String input) {
        assert input.startsWith("initial state: ");
        var raw = input.replaceAll("initial state: ", "").strip();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            assert c == ALIVE || c == DEAD;
            state.put(i, c);
        }
    }

    private static void parseRules(Set<String> lives, Set<String> dies, List<String> input) {
        var regex = join(group(repeat(set(or("#", ".")), 5, 5)),
                " => ", group(or("#", ".")));
        var pattern = Pattern.compile(regex);
        var rules = input.stream().map(pattern::matcher)
                .flatMap(Matcher::results)
                .map(match -> Pair.of(match.group(1), match.group(2)))
                .toList();
        for (Pair<String, String> rule : rules) {
            var precondition = rule.left();
            assert precondition.length() == 5;
            assert rule.right().length() == 1;
            var outcome = rule.right().charAt(0);
            assert outcome == ALIVE || outcome == DEAD;
            if (outcome == ALIVE) lives.add(precondition);
            else dies.add(precondition);
        }
    }

    private char simulate(int id, Map<Integer, Character> simulation) {
        var condition = IntStream.rangeClosed(id - 2, id + 2)
                .mapToObj(i -> simulation.getOrDefault(i, DEAD))
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();
        assert lives.contains(condition) || dies.contains(condition);
        if (lives.contains(condition)) return ALIVE;
        else return DEAD;
    }

    private Map<Integer, Character> simulate(Map<Integer, Character> simulation) {
        var changes = Collections.synchronizedMap(new HashMap<Integer, Character>());
        char l1 = simulate(lo - 1, simulation), l2 = simulate(lo - 2, simulation);
        char h1 = simulate(hi + 1, simulation), h2 = simulate(hi + 2, simulation);
        if (l1 == ALIVE) changes.put(--lo, l1);
        if (l2 == ALIVE) changes.put(--lo, l2);
        if (h1 == ALIVE) changes.put(++hi, h1);
        if (h2 == ALIVE) changes.put(++hi, h2);
        IntStream.range(lo, hi)
                .parallel()
                .mapToObj(id -> Pair.of(id, simulate(id, simulation)))
                .forEach(pair -> {
                    var id = pair.left();
                    var outcome = pair.right();
                    if (simulation.getOrDefault(id, DEAD) != outcome) changes.put(id, outcome);
                });
        return changes;
    }

    private Integer simulate(long generations) {
        this.lo = 0;
        this.hi = state.size() - 1;
        var simulation = new HashMap<>(state);
        for (long i = 0; i < generations; i++) {
            simulation.putAll(simulate(simulation));
        }
        return simulation.entrySet().stream()
                .filter(kv -> kv.getValue() == ALIVE)
                .map(Map.Entry::getKey)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    protected Long solvePartOne(List<String> input) {
        return simulate(20).longValue();
    }

    @Override
    protected Long solvePartTwo(List<String> input) {
        var generations = IntStream.range(0, 5)
                .map(i -> i *= 100)
                .map(this::simulate)
                .boxed()
                .toList();
        var deltas = IntStream.range(1, 5)
                .map(i -> generations.get(i) - generations.get(i - 1))
                .boxed()
                .toList();
        assert deltas.subList(2, 4).stream().allMatch(delta -> deltas.get(1).equals(delta));
        return simulate(500) + (50000000000L - 500) / 100 * deltas.get(1);
    }
}
