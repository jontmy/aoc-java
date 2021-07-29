package solvers.aoc2018.day15;

import java.util.*;
import java.util.stream.Collectors;

abstract class Unit implements Comparable<Unit> {
    protected static final Comparator<Unit> READING_ORDER_COMPARATOR =
            Comparator.comparing(Unit::y).thenComparing(Unit::x);
    protected static final Comparator<Unit> DISPLAY_ORDER_COMPARATOR =
            Comparator.comparing(Unit::x).thenComparing(Unit::y);
    protected static final int ATK = 3;

    protected final Cavern cavern;
    protected int x;
    protected int y;
    protected int hp;

    protected Unit(Cavern cavern, Coordinates coordinates) {
        Objects.requireNonNull(cavern);
        this.cavern = cavern;

        Objects.requireNonNull(coordinates);
        if (coordinates.x() < 0) throw new IllegalArgumentException(coordinates.toString());
        if (coordinates.y() < 0) throw new IllegalArgumentException(coordinates.toString());
        this.x = coordinates.x();
        this.y = coordinates.y();

        this.hp = 200;
    }

    protected void move(Coordinates coordinates) {
        move(coordinates.x(), coordinates.y());
    }

    protected void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void move(Path path) {
        var step = path.step();
        assert this.coordinates().manhattan(step) == 1;
        this.x = step.x();
        this.y = step.y();
    }

    protected Optional<Path> pathfind(char target) {
        if (target != Cavern.ELF && target != Cavern.GOBLIN) throw new IllegalArgumentException(String.valueOf(target));
        var start = Coordinates.at(x, y);
        var adjacent = cavern.adjacent(start);
        var targets = (target == Cavern.ELF ? cavern.elves() : cavern.goblins())
                .stream()
                .map(Unit::coordinates)
                .map(cavern::adjacent)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // Check if this unit is already adjacent to a target unit.
        // If it is, return an empty Optional as this unit will not need to move.
        if (adjacent.stream().anyMatch(adj -> cavern.at(adj) == target)) {
            return Optional.empty();
        }

        var paths = adjacent.stream()
                .filter(adj -> cavern.map()[adj.x()][adj.y()] == Cavern.TRAVERSABLE)
                .map(adj -> Path.of(start, adj))
                .toList();
        var searched = new HashSet<Coordinates>();
        searched.add(start);
        var results = new HashSet<Path>();

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
                    .map(Path::end)
                    .forEach(searched::add);

            // Add all branching paths - by adjacent tile extension - to the list of paths,
            // pruning those that end in coordinates that have already been searched,
            // and those that end in coordinates that are not traversable.
            paths = paths.stream()
                    .parallel()
                    .flatMap(path -> cavern.adjacent(path.end())
                            .stream()
                            .filter(adj -> cavern.at(adj) == Cavern.TRAVERSABLE)
                            .map(path::extend))
                    .filter(path -> !searched.contains(path.end()))
                    .toList();
        }
        return results.stream().min(Comparator.naturalOrder());
    }

    protected int x() {
        return x;
    }

    protected int y() {
        return y;
    }

    protected Coordinates coordinates() {
        return Coordinates.at(x, y);
    }

    @Override
    public int compareTo(Unit that) {
        return READING_ORDER_COMPARATOR.compare(this, that);
    }

    @Override
    public String toString() {
        return coordinates().toString();
    }
}
