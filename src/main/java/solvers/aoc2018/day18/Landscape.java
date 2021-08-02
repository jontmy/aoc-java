package solvers.aoc2018.day18;

import java.util.ArrayList;
import java.util.List;

record Landscape(char[][] area, int width, int height) {
    protected static final char BARREN = '.', FORESTED = '|', LUMBERYARD = '#';

    protected static Landscape parse(List<String> input) {
        assert !input.isEmpty();
        var width = input.get(0).length();
        var height = input.size();
        var area = new char[width][height];
        for (int y = 0; y < height; y++) {
            var cs = input.get(y);
            for (int x = 0; x < width; x++) {
                area[x][y] = cs.charAt(x);
            }
        }
        return new Landscape(area, width, height);
    }

    protected static Landscape from(Landscape landscape) {
        var width = landscape.width();
        var height = landscape.height;
        var area = new char[width][height];
        for (int x = 0; x < landscape.width(); x++) {
            System.arraycopy(landscape.area()[x], 0, area[x], 0, landscape.height());
        }
        return new Landscape(area, width, height);
    }

    // Returns the eight acres surrounding an acre. (Acres on the edges of the lumber collection area
    // might have fewer than eight adjacent acres; the missing acres aren't counted.)
    private List<Character> adjacent(int x, int y) {
        var adjacent = new ArrayList<Character>();
        if (x > 0) adjacent.add(area[x - 1][y]);
        if (x < height - 1) adjacent.add(area[x + 1][y]);
        if (y > 0) {
            adjacent.add(area[x][y - 1]);
            if (x > 0) adjacent.add(area[x - 1][y - 1]);
            if (x < height - 1) adjacent.add(area[x + 1][y - 1]);
        }
        if (y < height - 1) {
            adjacent.add(area[x][y + 1]);
            if (x > 0) adjacent.add(area[x - 1][y + 1]);
            if (x < height - 1) adjacent.add(area[x + 1][y + 1]);
        }
        return adjacent;
    }

    // Returns the number of acres surrounding an acre at the specified coordinates that match the specified type.
    private int adjacent(int x, int y, char type) {
        return (int) adjacent(x, y).stream()
                .filter(c -> c == type)
                .count();
    }

    /*
    The change to each acre is based entirely on the contents of that acre as well as the number of open,
    wooded, or lumberyard acres adjacent to it at the start of each minute.
    Changes happen across all acres simultaneously, each of them using the state of all acres at the
    beginning of the minute and changing to their new form by the end of that same minute.
    Changes that happen during the minute don't affect each other.
    */
    protected Landscape derive() {
        var derived = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var acre = area[x][y];
                switch (acre) {
                    case BARREN -> {
                        // An open acre will become filled with trees if three or more adjacent acres contained trees.
                        // Otherwise, nothing happens.
                        if (adjacent(x, y, FORESTED) >= 3) {
                            derived[x][y] = FORESTED;
                        } else {
                            derived[x][y] = BARREN;
                        }
                    }
                    case FORESTED -> {
                        // An acre filled with trees will become a lumberyard if three or more adjacent acres were lumberyards.
                        // Otherwise, nothing happens.
                        if (adjacent(x, y, LUMBERYARD) >= 3) {
                            derived[x][y] = LUMBERYARD;
                        } else {
                            derived[x][y] = FORESTED;
                        }
                    }
                    case LUMBERYARD -> {
                        // An acre containing a lumberyard will remain a lumberyard if it was adjacent to at least
                        // one other lumberyard and at least one acre containing trees. Otherwise, it becomes open.
                        if (adjacent(x, y, LUMBERYARD) >= 1 && adjacent(x, y, FORESTED) >= 1) {
                            derived[x][y] = LUMBERYARD;
                        } else {
                            derived[x][y] = BARREN;
                        }
                    }
                    default -> throw new IllegalStateException(String.valueOf(acre));
                }
            }
        }
        return new Landscape(derived, width, height);
    }

    protected Landscape derive(int minutes) {
        var derived = this;
        for (int i = 0; i < minutes; i++) {
            derived = derived.derive();
        }
        return derived;
    }

    // Multiplying the number of wooded acres by the number of lumberyards gives the total resource value.
    protected int value() {
        int forested = 0, lumberyards = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = area[x][y];
                if (c == Landscape.FORESTED) forested++;
                else if (c == Landscape.LUMBERYARD) lumberyards++;
            }
        }
        return forested * lumberyards;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public String toString() {
        var sb = new StringBuilder();
        sb.append(width).append(" x ").append(height).append(" landscape:\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(area[x][y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
