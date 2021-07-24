package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AOC2018Day5 extends AOCDay<Integer> {
    public AOC2018Day5() throws IOException, URISyntaxException {
        super(5, 2018);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        assert !input.isEmpty() : "Missing input.";
        var current = Polymer.of(input.get(0));
        var reacted = current.react();
        while (reacted.length() < current.length()) {
            current = reacted;
            reacted = reacted.react();
        }
        return reacted.length();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }

    private record Polymer(String chain) {
        private static Polymer of(String chain) {
            return new Polymer(chain);
        }

        private static boolean reacts(char first, char second) {
            if (Character.toLowerCase(first) != Character.toLowerCase(second)) {
                return false; // different unit (character)
            }
            return first != second; // true if both are of different cases, false if both are of the same case
        }

        private Polymer react() {
            var sb = new StringBuilder();
            for (int i = 0; i < chain.length() - 1; ) {
                char first = chain.charAt(i);
                char second = chain.charAt(i + 1);
                if (reacts(first, second)) {
                    i += 2;
                } else {
                    sb.append(first);
                    i++;
                }
            }
            sb.append(chain.charAt(chain.length() - 1));
            return Polymer.of(sb.toString());
        }

        private int length() {
            return chain.length();
        }

        @Override
        public String toString() {
            return chain;
        }
    }
}
