package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AOC2018Day14 extends AOCDay<String> {
    public AOC2018Day14() throws IOException, URISyntaxException {
        super(14, 2018);
    }

    public static void main(String[] args) {
        var scoreboard = parse(List.of("37"));
        LOGGER.info(scoreboard);
        var recipes = Recipe.from(List.of(scoreboard.first, scoreboard.last));
        scoreboard.append(recipes);
        LOGGER.info(scoreboard);
    }

    private static Scoreboard parse(List<String> input) {
        assert input.size() == 1;
        return Scoreboard.parse(input.get(0));
    }

    @Override
    protected String solvePartOne(List<String> input) {
        var scoreboard = parse(input);
        return "";
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        var scoreboard = parse(input);
        return "";
    }

    private static final class Scoreboard {
        private final Recipe first;
        private Recipe last;
        private int size;

        private Scoreboard(Recipe first, Recipe last, int size) {
            this.first = first;
            this.last = last;
            this.size = size;
        }

        private static Scoreboard parse(String scores) {
            var recipes = Recipe.parse(scores);
            var first = recipes.get(0);
            var last = recipes.get(recipes.size() - 1);
            Recipe.link(last, first);
            return new Scoreboard(first, last, recipes.size());
        }

        // Creates a link between the last recipe of this scoreboard and a list of recipes,
        // such that the last recipe's next recipe is the first recipe in the list, and so on.
        // The last recipe in the list will then be linked as the previous recipe of the first recipe in this scoreboard.
        // Assumes all recipes in the list are linked with one another.
        private void append(List<Recipe> recipes) {
            Recipe.link(last, recipes.get(0));
            Recipe.link(recipes.get(recipes.size() - 1), first);
            this.size += recipes.size();
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            var cursor = first;
            do {
                sb.append(cursor.score()).append(" ");
                cursor = cursor.next();
            } while (!cursor.equals(first));
            return sb.toString();
        }
    }

    private static final class Recipe {
        private final int score;
        private Recipe prev, next;

        private Recipe(int score, Recipe prev, Recipe next) {
            this.score = score;
            this.prev = prev;
            this.next = next;
        }

        // Returns a new recipe with a specified score with no links to a previous or next recipe.
        private static Recipe of(int score) {
            return new Recipe(score, null, null);
        }

        // Returns a list of new recipes linked between one another from a list of existing recipes.
        // The 'prev' field of the first recipe, and the 'next' field of the last recipe are unlinked.
        private static List<Recipe> from(List<Recipe> existing) {
            // To create new recipes, the two Elves combine their current recipes.
            var sum = existing.stream()
                    .map(Recipe::score)
                    .reduce(Integer::sum)
                    .orElseThrow();

            // This creates new recipes from the digits of the sum of the current recipes' scores.
            return parse(String.valueOf(sum));
        }

        // Returns a list of new recipes linked with one another from a score string.
        // The 'prev' field of the first recipe, and the 'next' field of the last recipe are unlinked.
        private static List<Recipe> parse(String scores) {
            var recipes = scores.chars()
                    .mapToObj(c -> (char) c)
                    .map(c -> Integer.valueOf(c.toString()))
                    .map(Recipe::of)
                    .toList();

            // Link recipes with one another.
            for (int i = 0; i < recipes.size() - 1; i++) {
                var current = recipes.get(i);
                var next = recipes.get(i + 1);
                link(current, next);
            }
            var last = recipes.get(recipes.size() - 1);
            last.prev = recipes.get(recipes.size() - 2);

            return recipes;
        }

        // Links 2 recipes together, where the first is the previous recipe of the second, and the
        // second is the next recipe of the first.
        private static void link(Recipe l, Recipe r) {
            l.next = r;
            r.prev = l;
        }

        // Returns the recipe n steps forward from the current recipe.
        private Recipe forward(int steps) {
            if (steps <= 0) throw new IllegalArgumentException(String.valueOf(steps));
            var cursor = this;
            for (int i = 0; i < steps; i++) {
                cursor = cursor.next();
            }
            return cursor;
        }

        private int score() {
            return score;
        }

        private Recipe prev() {
            return prev;
        }

        private Recipe next() {
            return next;
        }

        @Override
        public String toString() {
            return "Recipe{" +
                    "score=" + score +
                    ", prev=" + prev +
                    ", next=" + next +
                    '}';
        }
    }
}
