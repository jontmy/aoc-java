package solvers.aoc2018.day15;

import java.util.Comparator;

record Path(Coordinates start, Coordinates step, Coordinates end,
            int distance) implements Comparable<Path> {
    private static final Comparator<Path> NATURAL_ORDER_COMPARATOR =
            Comparator.comparing(Path::distance)
                    .thenComparing(Path::start, Coordinates.READING_ORDER_COMPARATOR)
                    .thenComparing(Path::step, Coordinates.READING_ORDER_COMPARATOR)
                    .thenComparing(Path::end, Coordinates.READING_ORDER_COMPARATOR);

    protected static Path of(Coordinates start, Coordinates step) {
        var distance = start.manhattan(step);
        return new Path(start, step, Coordinates.from(step), distance);
    }

    // Extends the path by 1 step in either the x or y direction.
    protected Path extend(Coordinates end) {
        assert this.end().manhattan(end) == 1 : "Extension distance of %d != 1.".formatted(this.end().manhattan(end));
        return new Path(this.start(), this.step(), end, this.distance() + 1);
    }

    // If multiple squares are in range and tied for being reachable in the fewest steps (distance),
    // the square which is first in reading order is chosen.
    @Override
    public int compareTo(Path that) {
        return NATURAL_ORDER_COMPARATOR.compare(this, that);
    }

    @Override
    public String toString() {
        return "%s -> %s -> %d step(s) -> %s".formatted(start, step, distance - 1, end);
    }
}
