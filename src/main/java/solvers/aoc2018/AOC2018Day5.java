package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;

public class AOC2018Day5 extends AOCDay<Integer> {
    public AOC2018Day5() throws IOException, URISyntaxException {
        super(5, 2018);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        assert !input.isEmpty() : "Missing input.";
        var initial = Polymer.of(input.get(0));
        return initial.react().length();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        assert !input.isEmpty() : "Missing input.";
        var initial = Polymer.of(input.get(0));
        var units = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return units.chars()
                .mapToObj(c -> (char) c)
                .map(initial::without)
                .map(Polymer::react)
                .map(Polymer::length)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new AssertionError("Missing polymer reaction result."));
    }

    private record Polymer(String chain) {
        private static Polymer of(String chain) {
            return new Polymer(chain);
        }

        private static boolean unitsReact(char first, char second) {
            if (Character.toLowerCase(first) != Character.toLowerCase(second)) {
                return false; // different unit (character)
            }
            return first != second; // true if both are of different cases, false if both are of the same case
        }

        private static Polymer reactOnce(Polymer reactant) {
            var sb = new StringBuilder();
            for (int i = 0; i < reactant.length() - 1; ) {
                char first = reactant.chain().charAt(i);
                char second = reactant.chain().charAt(i + 1);
                if (unitsReact(first, second)) {
                    i += 2;
                } else {
                    sb.append(first);
                    i++;
                }
            }
            sb.append(reactant.chain().charAt(reactant.length() - 1));
            return Polymer.of(sb.toString());
        }

        private Polymer without(char alphabet) {
            var reduction = chain.chars()
                    .mapToObj(c -> (char) c)
                    .filter(unit -> Character.toLowerCase(unit) != Character.toLowerCase(alphabet))
                    .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                    .toString();
            return Polymer.of(reduction);
        }

        private Polymer react() {
            var current = this;
            var reacted = reactOnce(current);
            while (reacted.length() < current.length()) {
                current = reacted;
                reacted = reactOnce(reacted);
            }
            return reacted;
        }

        private int length() {
            return chain.length();
        }
    }
}
