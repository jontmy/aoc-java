package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class AOC2018Day15 extends AOCDay<Integer> {
    public AOC2018Day15() throws IOException, URISyntaxException {
        super(15, 2018);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var cavern = Cavern.parse(super.input);
        // assert cavern.goblins().size() == 20;
        // assert cavern.elves().size() == 10;
        // Unit.pathfind(cavern, 4, 15, cavern.elves().subList(cavern.elves().size() - 1, cavern.elves().size()));
        LOGGER.debug(cavern);
        var goblin = cavern.goblins().stream().min(Comparator.naturalOrder()).orElseThrow();
        var elf = cavern.elves().stream().max(Comparator.naturalOrder()).orElseThrow();
        LOGGER.info(goblin.pathfind(cavern, cavern.elves()));
        LOGGER.info(elf.pathfind(cavern, cavern.goblins()));
        // LOGGER.info(goblin.pathfind(cavern, List.of(elf)));
        for (int i = 0; i < 20; i++) {
            cavern.simulateRound();
            LOGGER.debug(cavern);
        }
        LOGGER.debug(cavern);
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
                    if (c == WALL || c == TRAVERSABLE) {
                        map[x][y] = c;
                    } else {
                        map[x][y] = TRAVERSABLE;
                        var unitBuilder = new Unit.Builder().setX(x).setY(y);
                        if (c == GOBLIN) goblins.add(unitBuilder.buildGoblin());
                        else if (c == ELF) elves.add(unitBuilder.buildElf());
                    }
                }
            }
            return new Cavern(map, width, height, goblins, elves);
        }

        private List<Coordinates> adjacent(int x, int y) {
            if (x < 0 || x > width) throw new IllegalArgumentException(String.valueOf(x));
            if (y < 0 || y > height) throw new IllegalArgumentException(String.valueOf(y));
            var adj = new ArrayList<Coordinates>();
            if (x + 1 >= 0 && x + 1 < width) adj.add(Coordinates.at(x + 1, y));
            if (x - 1 >= 0 && x - 1 < width) adj.add(Coordinates.at(x - 1, y));
            if (y + 1 >= 0 && y + 1 < height) adj.add(Coordinates.at(x, y + 1));
            if (y - 1 >= 0 && y - 1 < height) adj.add(Coordinates.at(x, y - 1));
            return adj;
        }

        private List<Coordinates> adjacent(Coordinates coordinates) {
            return adjacent(coordinates.x(), coordinates.y()).stream()
                    .map(c -> Coordinates.at(c.x(), c.y()))
                    .toList();
        }

        private void simulateRound() {
            var queue = new PriorityQueue<Unit>();
            queue.addAll(elves);
            queue.addAll(goblins);
            while (!queue.isEmpty()) {
                var unit = queue.remove();
                Optional<CavernPath> path;
                if (unit instanceof Goblin goblin) path = goblin.pathfind(this, elves);
                else if (unit instanceof Elf elf) path = elf.pathfind(this, goblins);
                else throw new IllegalStateException(unit.toString());
                path.ifPresent(unit::step);
            }
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append(width).append(" x ").append(height).append(" cavern:\n");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (Unit.unitPresentAt(x, y, goblins)) sb.append('G');
                    else if (Unit.unitPresentAt(x, y, elves)) sb.append('E');
                    else sb.append(map[x][y]);
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

    private static class Unit implements Comparable<Unit> {
        protected static final Comparator<Unit> READING_COORDINATES_COMPARATOR = Comparator
                .comparing(Unit::y)
                .thenComparing(Unit::x);
        protected static final Comparator<Unit> DISPLAY_COORDINATES_COMPARATOR = Comparator
                .comparing(Unit::x)
                .thenComparing(Unit::y);
        protected static final int ATK = 3;

        protected int x;
        protected int y;
        protected int hp;

        private Unit(int x, int y) {
            this.x = x;
            this.y = y;
            this.hp = 200;
        }

        private static boolean unitPresentAt(Coordinates coordinates, List<Unit> units) {
            return units.stream()
                    .map(Unit::coordinates)
                    .anyMatch(coordinates::equals);
        }

        private static boolean unitPresentAt(int x, int y, List<Unit> units) {
            return unitPresentAt(Coordinates.at(x, y), units);
        }


        // Returns the best path from a coordinate to any specified targets.
        protected Optional<CavernPath> pathfind(Cavern cavern, List<Unit> targetUnits) {
            var start = Coordinates.at(x, y);
            var adjacent = cavern.adjacent(start);
            var targets = targetUnits.stream()
                    .map(Unit::coordinates)
                    .map(cavern::adjacent)
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            // Check if this unit is already adjacent to a target unit.
            // If it is, return an empty Optional as this unit will not need to move.
            if (adjacent.stream().anyMatch(adj -> Unit.unitPresentAt(adj, targetUnits))) {
                return Optional.empty();
            }

            var paths = adjacent.stream()
                    .filter(adj -> cavern.map()[adj.x()][adj.y()] == Cavern.TRAVERSABLE)
                    .map(adj -> CavernPath.of(start, adj))
                    .toList();
            var searched = new HashSet<Coordinates>();
            searched.add(start);
            var results = new HashSet<CavernPath>();

            while (!paths.isEmpty()) {
                // Add the paths that end at a target coordinate to the set of results.
                results = paths.stream()
                        .parallel()
                        .filter(path -> targets.contains(path.end()))
                        .collect(Collectors.toCollection(HashSet::new));

                // Optimization: Return the paths that end on a target coordinate, if any, otherwise continue.
                if (!results.isEmpty()) break;

                // Mark the coordinates that have been searched.
                paths.stream()
                        .map(CavernPath::end)
                        .forEach(searched::add);

                // Add all branching paths - by adjacent tile extension - to the list of paths,
                // pruning those that end in coordinates that have already been searched,
                // and those that end in coordinates that are not traversable.
                paths = paths.stream()
                        .parallel()
                        .flatMap(path -> cavern.adjacent(path.end())
                                .stream()
                                .filter(adj -> cavern.map()[adj.x()][adj.y()] == Cavern.TRAVERSABLE)
                                .filter(adj -> !Unit.unitPresentAt(adj, cavern.elves()))
                                .filter(adj -> !Unit.unitPresentAt(adj, cavern.goblins()))
                                .map(path::extend))
                        .filter(path -> !searched.contains(path.end()))
                        .toList();
            }
            return results.stream().min(Comparator.naturalOrder());
        }

        protected void step(CavernPath path) {
            var step = path.step();
            assert CavernPath.manhattan(coordinates(), step) == 1;
            this.x = step.x();
            this.y = step.y();
        }

        protected Coordinates coordinates() {
            return Coordinates.at(x, y);
        }

        protected int x() {
            return x;
        }

        protected int y() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        @Override
        public int compareTo(Unit that) {
            return READING_COORDINATES_COMPARATOR.compare(this, that);
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

    private record Coordinates(int x, int y) implements Comparable<Coordinates> {
        private static final Comparator<Coordinates> READING_ORDER_COMPARATOR =
                Comparator.comparing(Coordinates::y)
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
        public int compareTo(Coordinates that) {
            return READING_ORDER_COMPARATOR.compare(this, that);
        }

        @Override
        public String toString() {
            return "(%d, %d}".formatted(x, y);
        }
    }

    private record CavernPath(Coordinates start, Coordinates step, Coordinates end,
                              int distance) implements Comparable<CavernPath> {
        private static final Comparator<CavernPath> NATURAL_ORDER_COMPARATOR =
                Comparator.comparing(CavernPath::distance)
                        .thenComparing(CavernPath::start, Coordinates.READING_ORDER_COMPARATOR)
                        .thenComparing(CavernPath::step, Coordinates.READING_ORDER_COMPARATOR)
                        .thenComparing(CavernPath::end, Coordinates.READING_ORDER_COMPARATOR);

        private static CavernPath of(Coordinates start, Coordinates step) {
            var distance = manhattan(start, step);
            return new CavernPath(start, step, Coordinates.from(step), distance);
        }

        // Returns the Manhattan distance between 2 coordinates.
        private static int manhattan(Coordinates start, Coordinates end) {
            return Math.abs(start.x() - end.x()) + Math.abs(start.y() - end.y());
        }

        // Extends the path by 1 step in either the x or y direction.
        private CavernPath extend(Coordinates end) {
            assert manhattan(this.end(), end) == 1
                    : "Absolute step distance of %d != 1.".formatted(manhattan(this.end(), end));
            return new CavernPath(this.start(), this.step(), end, this.distance() + 1);
        }

        // If multiple squares are in range and tied for being reachable in the fewest steps (distance),
        // the square which is first in reading order is chosen.
        @Override
        public int compareTo(CavernPath that) {
            return NATURAL_ORDER_COMPARATOR.compare(this, that);
        }

        @Override
        public String toString() {
            return "%s -> %s -> %d step(s) -> %s".formatted(start, step, distance - 1, end);
        }
    }
}
