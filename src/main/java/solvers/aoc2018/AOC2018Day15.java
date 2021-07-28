package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AOC2018Day15 extends AOCDay<Integer> {
    public AOC2018Day15() throws IOException, URISyntaxException {
        super(15, 2018);
        var cavern = Cavern.parse(super.input);
        // assert cavern.goblins().size() == 20;
        // assert cavern.elves().size() == 10;
        LOGGER.debug(cavern);
        LOGGER.info(Unit.pathfind(cavern, 1, 1, cavern.elves()));
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return 0;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }

    private record Cavern(char[][] map, int width, int height, List<Unit> goblins, List<Unit> elves) {
        private static final char WALL = '#', TRAVERSABLE = '.', GOBLIN = 'G', ELF = 'E';

        private static Cavern parse(List<String> input) {
            var height = input.size();
            var widths = input.stream()
                    .map(String::length)
                    .distinct()
                    .toList();
            assert widths.size() == 1;
            var width = widths.get(0);

            var map = new char[width][height];
            var goblins = new ArrayList<Unit>();
            var elves = new ArrayList<Unit>();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    var c = input.get(y).charAt(x);
                    map[x][y] = c;
                    if (c == WALL || c == TRAVERSABLE) continue;
                    var unitBuilder = new Unit.Builder().setX(x).setY(y);
                    if (c == GOBLIN) goblins.add(unitBuilder.buildGoblin());
                    else if (c == ELF) elves.add(unitBuilder.buildElf());
                }
            }
            return new Cavern(map, width, height, goblins, elves);
        }

        private List<Coordinates> adjacent(int x, int y) {
            if (x < 0 || x > width) throw new IllegalArgumentException(String.valueOf(x));
            if (y < 0 || y > height) throw new IllegalArgumentException(String.valueOf(y));
            var adj = new ArrayList<Coordinates>();
            if (x + 1 >= 0 && x + 1 < width) adj.add(Coordinates.at(x + 1, y));
            if (x + 1 >= 0 && x + 1 < width) adj.add(Coordinates.at(x - 1, y));
            if (y + 1 >= 0 && y + 1 < height) adj.add(Coordinates.at(x, y + 1));
            if (y - 1 >= 0 && y - 1 < height) adj.add(Coordinates.at(x, y - 1));
            return adj;
        }

        private List<Coordinates> adjacent(Coordinates coordinates) {
            return adjacent(coordinates.x(), coordinates.y()).stream()
                    .map(c -> Coordinates.at(c.x(), c.y()))
                    .toList();
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append(width).append(" x ").append(height).append(" cavern:\n");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    sb.append(map[x][y]);
                }
                sb.append("\n");
            }
            sb.append("Goblins (")
                    .append("%02d".formatted(goblins.size()))
                    .append("): ")
                    .append(goblins.stream()
                            .sorted(Unit.DISPLAY_COORDINATES_COMPARATOR)
                            .map(Unit::toString)
                            .toList()
                            .toString())
                    .append("\n");
            sb.append("  Elves (")
                    .append("%02d".formatted(elves.size()))
                    .append("): ")
                    .append(elves.stream()
                            .sorted(Unit.DISPLAY_COORDINATES_COMPARATOR)
                            .map(Unit::toString)
                            .toList()
                            .toString())
                    .append("\n");
            return sb.toString();
        }
    }

    private static final class Goblin extends Unit {
        private Goblin(int x, int y) {
            super(x, y);
        }
    }

    private static final class Elf extends Unit {
        private Elf(int x, int y) {
            super(x, y);
        }
    }

    private static class Unit {
        private static final Comparator<Unit> READING_COORDINATES_COMPARATOR = Comparator
                .comparing(Unit::y)
                .thenComparing(Unit::x);
        private static final Comparator<Unit> DISPLAY_COORDINATES_COMPARATOR = Comparator
                .comparing(Unit::x)
                .thenComparing(Unit::y);
        private static final int ATK = 3;

        private final int x;
        private final int y;
        private final int hp;

        private Unit(int x, int y) {
            this.x = x;
            this.y = y;
            this.hp = 200;
        }

        private static Set<CavernPath> pathfind(Cavern cavern, int x, int y, Collection<? extends Unit> targetUnits) {
            var startCoordinates = Coordinates.at(x, y);
            var startPath = CavernPath.of(x, y, x, y);
            var targetCoordinates = targetUnits.stream()
                    .map(Unit::coordinates)
                    .collect(Collectors.toSet());
            if (targetCoordinates.contains(startCoordinates)) return Set.of(startPath);

            var searched = new HashSet<Coordinates>();
            var results = new HashSet<CavernPath>();
            var queue = new PriorityQueue<>(List.of(startPath));
            while (!queue.isEmpty()) {
                var searchPath = queue.remove();
                var searchCoordinates = searchPath.end();
                LOGGER.debug(searchCoordinates);
                searched.add(searchCoordinates);
                if (targetCoordinates.contains(searchCoordinates)) {
                    if (!results.isEmpty()) {
                        if (results.iterator().next().distance < searchPath.distance) break;
                    }
                    results.add(searchPath);
                } else {
                    cavern.adjacent(searchPath.end()).stream()
                            .filter(adj -> adj.x() > 0 && adj.x() < cavern.width())
                            .filter(adj -> adj.y() > 0 && adj.x() < cavern.height())
                            .filter(Predicate.not(searched::contains))
                            .map(adj -> CavernPath.extend(searchPath, adj))
                            .forEach(queue::add);
                }
            }
            return results;
        }

        private static Coordinates path(Cavern cavern, int x, int y, Collection<? extends Unit> targetUnits) {
            var bestPath = cavern.adjacent(x, y).stream()
                    .map(p -> pathfind(cavern, x, y, targetUnits))
                    .flatMap(Set::stream)
                    .min(CavernPath.NATURAL_ORDER_COMPARATOR)
                    .orElseThrow();
            return bestPath.start();
        }

        private Coordinates coordinates() {
            return Coordinates.at(x, y);
        }

        private int x() {
            return x;
        }

        private int y() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        private static class Builder {
            private int x, y;

            private Builder setX(int x) {
                this.x = x;
                return this;
            }

            private Builder setY(int y) {
                this.y = y;
                return this;
            }

            private Goblin buildGoblin() {
                return new Goblin(x, y);
            }

            private Elf buildElf() {
                return new Elf(x, y);
            }
        }
    }

    private record Coordinates(int x, int y) {
        private static final Comparator<Coordinates> READING_ORDER_COMPARATOR = Comparator
                .comparing(Coordinates::y)
                .thenComparing(Coordinates::x);

        private static Coordinates at(int x, int y) {
            return new Coordinates(x, y);
        }

        private static Coordinates from(Coordinates coords) {
            return new Coordinates(coords.x(), coords.y());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinates that = (Coordinates) o;
            if (x != that.x) return false;
            return y == that.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public String toString() {
            return "(%d, %d}".formatted(x, y);
        }
    }

    private record CavernPath(Coordinates start, Coordinates end, int distance) implements Comparable<CavernPath> {
        private static final Comparator<CavernPath> NATURAL_ORDER_COMPARATOR = Comparator
                .comparing(CavernPath::distance)
                .thenComparing(CavernPath::start, Coordinates.READING_ORDER_COMPARATOR)
                .thenComparing(CavernPath::end, Coordinates.READING_ORDER_COMPARATOR);

        private static CavernPath of(int startX, int startY, int endX, int endY) {
            var start = Coordinates.at(startX, startY);
            var end = Coordinates.at(endX, endY);
            var distance = manhattan(start, end);
            return new CavernPath(start, end, distance);
        }

        // Extends the path by 1 step in either the x or y direction.
        private static CavernPath extend(CavernPath path, Coordinates end) {
            assert manhattan(path.end(), end) == 1;
            return new CavernPath(path.start(), end, path.distance() + 1);
        }

        // Returns the Manhattan distance between 2 coordinates.
        private static int manhattan(Coordinates start, Coordinates end) {
            return Math.abs(start.x() - end.x()) + Math.abs(start.y() - end.y());
        }

        // If multiple squares are in range and tied for being reachable in the fewest steps (distance),
        // the square which is first in reading order is chosen.
        @Override
        public int compareTo(CavernPath that) {
            return NATURAL_ORDER_COMPARATOR.compare(this, that);
        }
    }
}
