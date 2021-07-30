package solvers.aoc2018.day15;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

record Cavern(char[][] map, int width, int height, List<Goblin> goblins, List<Elf> elves) {
    protected static final char WALL = '#', TRAVERSABLE = '.', GOBLIN = 'G', ELF = 'E';

    protected static Cavern parse(List<String> data) {
        var height = data.size();
        var widths = data.stream()
                .map(String::length)
                .distinct()
                .toList();
        assert widths.size() == 1;
        var width = widths.get(0);

        var map = new char[width][height];
        var goblins = new ArrayList<Coordinates>();
        var elves = new ArrayList<Coordinates>();

        // Parse walls (#) and traversable terrain (. G E)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var c = data.get(y).charAt(x);
                switch (c) {
                    case WALL, TRAVERSABLE -> map[x][y] = c;
                    case GOBLIN -> {
                        goblins.add(Coordinates.at(x, y));
                        map[x][y] = TRAVERSABLE;
                    }
                    case ELF -> {
                        elves.add(Coordinates.at(x, y));
                        map[x][y] = TRAVERSABLE;
                    }
                }
            }
        }

        // Create cavern Unit objects.
        var cavern = Cavern.of(map, width, height);
        goblins.stream()
                .map(g -> new Goblin(cavern, g))
                .forEach(cavern.goblins()::add);
        elves.stream()
                .map(e -> new Elf(cavern, e))
                .forEach(cavern.elves()::add);
        return cavern;
    }

    protected static Cavern of(char[][] map, int width, int height) {
        return new Cavern(map, width, height, new ArrayList<>(), new ArrayList<>());
    }

    protected char at(int x, int y) {
        return at(Coordinates.at(x, y));
    }

    protected char at(Coordinates coordinates) {
        if (goblins.stream().map(Unit::coordinates).anyMatch(coordinates::equals)) {
            return GOBLIN;
        } else if (elves.stream().map(Unit::coordinates).anyMatch(coordinates::equals)) {
            return ELF;
        } else {
            return map[coordinates.x()][coordinates.y()];
        }
    }

    protected List<Coordinates> adjacent(int x, int y) {
        if (x < 0 || x > width) throw new IllegalArgumentException(String.valueOf(x));
        if (y < 0 || y > height) throw new IllegalArgumentException(String.valueOf(y));
        var adj = new ArrayList<Coordinates>();
        if (x + 1 >= 0 && x + 1 < width) adj.add(Coordinates.at(x + 1, y));
        if (x - 1 >= 0 && x - 1 < width) adj.add(Coordinates.at(x - 1, y));
        if (y + 1 >= 0 && y + 1 < height) adj.add(Coordinates.at(x, y + 1));
        if (y - 1 >= 0 && y - 1 < height) adj.add(Coordinates.at(x, y - 1));
        return adj;
    }

    protected List<Coordinates> adjacent(Coordinates coordinates) {
        return adjacent(coordinates.x(), coordinates.y()).stream()
                .map(c -> Coordinates.at(c.x(), c.y()))
                .toList();
    }

    protected List<Coordinates> adjacent(Collection<Coordinates> coordinates) {
        return coordinates.stream()
                .map(this::adjacent)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(width).append(" x ").append(height).append(" cavern:\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(at(x, y));
            }
            sb.append("\n");
        }
        sb.append("Goblins (")
                .append("%02d".formatted(goblins.size()))
                .append("): ")
                .append(goblins.stream()
                        .sorted(Unit.DISPLAY_ORDER_COMPARATOR)
                        .map(Unit::toString)
                        .toList()
                        .toString())
                .append("\n");
        sb.append("  Elves (")
                .append("%02d".formatted(elves.size()))
                .append("): ")
                .append(elves.stream()
                        .sorted(Unit.DISPLAY_ORDER_COMPARATOR)
                        .map(Unit::toString)
                        .toList()
                        .toString())
                .append("\n");
        return sb.toString();
    }
}
