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

    private int adjacent(int x, int y, char type) {
        return (int) adjacent(x, y).stream()
                .filter(c -> c == type)
                .count();
    }

    protected Landscape derive() {
        var derived = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var acre = area[x][y];
                switch (acre) {
                    case BARREN -> {
                        if (adjacent(x, y, FORESTED) >= 3) {
                            derived[x][y] = FORESTED;
                        } else {
                            derived[x][y] = BARREN;
                        }
                    }
                    case FORESTED -> {
                        if (adjacent(x, y, LUMBERYARD) >= 3) {
                            derived[x][y] = LUMBERYARD;
                        } else {
                            derived[x][y] = FORESTED;
                        }
                    }
                    case LUMBERYARD -> {
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
