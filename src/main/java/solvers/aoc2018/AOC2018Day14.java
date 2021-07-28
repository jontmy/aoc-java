package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AOC2018Day14 extends AOCDay<String> {
    public AOC2018Day14() throws IOException, URISyntaxException {
        super(14, 2018);
    }

    public static void main(String[] args) {
        var scoreboard = parse(List.of("37"));
        LOGGER.info(scoreboard);
        for (int i = 0; i < 15; i++) {
            scoreboard.createNewRecipes();
            LOGGER.info(scoreboard);
        }
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

    private static final class Elf {
        private Recipe currentRecipe;

        private Elf(Recipe currentRecipe) {
            this.currentRecipe = currentRecipe;
        }

        private void pickNewCurrentRecipe() {
            // To do this, the Elf steps forward through the scoreboard a number of recipes
            // equal to 1 plus the score of their current recipe.
            // If they run out of recipes, they loop back around to the beginning.
            var steps = 1 + currentRecipe.score();
            this.currentRecipe = currentRecipe.forward(steps); // looping is implicitly implemented in Scoreboard
        }

        private Recipe currentRecipe() {
            return currentRecipe;
        }
    }

    private static final class Scoreboard {
        private final List<Elf> elves;
        private final Recipe first;
        private Recipe last;
        private int size;

        private Scoreboard(List<Elf> elves, Recipe first, Recipe last, int size) {
            this.elves = elves;
            this.first = first;
            this.last = last;
            this.size = size;
        }

        private static Scoreboard parse(String scores) {
            var recipes = Recipe.parse(scores);
            var elves = recipes.stream()
                    .map(Elf::new)
                    .collect(Collectors.toList());
            var first = recipes.get(0);
            var last = recipes.get(recipes.size() - 1);
            Recipe.link(last, first);
            return new Scoreboard(elves, first, last, recipes.size());
        }

        // Creates a link between the last recipe of this scoreboard and a list of recipes,
        // such that the last recipe's next recipe is the first recipe in the list, and so on.
        // The last recipe in the list will then be linked as the previous recipe of the first recipe in this scoreboard.
        // Assumes all recipes in the list are linked with one another.
        private void appendRecipes(List<Recipe> recipes) {
            Recipe.link(last, recipes.get(0));
            Recipe.link(recipes.get(recipes.size() - 1), first);
            last = recipes.get(recipes.size() - 1);
            this.size += recipes.size();
        }

        // Creates new recipes from the current recipes of the elves.
        // The new recipes are appended to the end of the scoreboard.
        private void createNewRecipes() {
            // To create new recipes, the Elves combine their current recipes.
            var currentRecipes = elves.stream()
                    .map(Elf::currentRecipe)
                    .toList();
            var newRecipes = Recipe.derive(currentRecipes);

            // The new recipes are added to the end of the scoreboard in the order they are created.
            appendRecipes(newRecipes);

            // After all new recipes are added to the scoreboard, each Elf picks a new current recipe.
            elves.forEach(Elf::pickNewCurrentRecipe);
        }

        private int size() {
            return size;
        }

        @Override
        public String toString() {
            var elvesRecipes = elves.stream()
                    .map(Elf::currentRecipe)
                    .collect(Collectors.toSet());
            var sb = new StringBuilder();
            var cursor = first;
            do {
                if (elvesRecipes.contains(cursor)) sb.append("(");
                sb.append(cursor.score());
                if (elvesRecipes.contains(cursor)) sb.append(")");
                sb.append(" ");
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

        // Returns a list of new recipes parsed from a string of specified scores, linked between one another.
        // The 'prev' field of the first recipe, and the 'next' field of the last recipe are unlinked.
        private static List<Recipe> parse(String scores) {
            var recipes = scores.chars()
                    .mapToObj(c -> (char) c)
                    .map(c -> Integer.valueOf(c.toString()))
                    .map(Recipe::of)
                    .toList();

            // Link recipes with one another.
            for (int i = 0; i < recipes.size(); i++) {
                var current = recipes.get(i);
                var next = i < recipes.size() - 1 ? recipes.get(i + 1) : null;
                link(current, next);
            }
            return recipes;
        }

        // Returns a list of new recipes linked between one another from a list of existing recipes.
        // The 'prev' field of the first recipe, and the 'next' field of the last recipe are unlinked.
        private static List<Recipe> derive(List<Recipe> existing) {
            // To create new recipes, the Elves combine their current recipes.
            var sum = existing.stream()
                    .map(Recipe::score)
                    .reduce(Integer::sum)
                    .orElseThrow();

            // This creates new recipes from the digits of the sum of the current recipes' scores.
            return parse(String.valueOf(sum));
        }

        // Links 2 recipes together, where the first is the previous recipe of the second, and the
        // second is the next recipe of the first.
        private static void link(Recipe l, Recipe r) {
            if (l != null) l.next = r;
            if (r != null) r.prev = l;
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
