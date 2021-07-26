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

public class AOC2018Day12 extends AOCDay<Integer> {
    private static final char ALIVE = '#', DEAD = '.';
    private final TreeMap<Integer, Character> state;
    private final Set<String> lives, dies;

    public AOC2018Day12() throws IOException, URISyntaxException {
        super(12, 2018);
        assert input.size() > 1;
        this.state = new TreeMap<>();
        parseInitialState(state, input.get(0));
        this.lives = new HashSet<>();
        this.dies = new HashSet<>();
        parseRules(lives, dies, input.subList(1, input.size()));
        // assert lives.size() + dies.size() == 32;
    }

    private static void parseInitialState(Map<Integer, Character> state, String input) {
        assert input.startsWith("initial state: ");
        var raw = input.replaceAll("initial state: ", "").trim();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            assert c == ALIVE || c == DEAD;
            state.put(i, c);
        }
    }

    private static void parseRules(Set<String > lives, Set<String> dies, List<String> input) {
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

    private static char simulate(int id, TreeMap<Integer, Character> state,  Set<String> lives, Set<String> dies) {
        var condition = IntStream.rangeClosed(id - 2, id + 2)
                .mapToObj(i -> state.getOrDefault(i, DEAD))
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();
        // assert lives.contains(condition) || dies.contains(condition);
        if (lives.contains(condition)) return ALIVE;
        else return DEAD;
    }

    private static Map<Integer, Character> simulate(TreeMap<Integer, Character> state, Set<String> lives, Set<String> dies) {
        int lo = state.firstKey() - 2, hi = state.lastKey() + 2;
        var changes = new TreeMap<Integer, Character>();
        for (int id = lo; id < hi; id++) {
            var outcome = simulate(id, state, lives, dies);
            if (state.getOrDefault(id, DEAD) != outcome) changes.put(id, outcome);
        }
        return changes;
    }

    public static String stringify(Map<Integer, Character> state) {
        return state.values().stream()
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        final int GENERATIONS = 20;
        LOGGER.debug(stringify(state));
        for (int i = 0; i < GENERATIONS; i++) {
            state.putAll(simulate(state, lives, dies));
            LOGGER.debug(stringify(state));
        }
        return state.entrySet().stream()
                .filter(kv -> kv.getValue() == ALIVE)
                .map(Map.Entry::getKey)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }
}
