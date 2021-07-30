package solvers.aoc2018.day15;

import java.util.Comparator;

record Coordinates(int x, int y) implements Comparable<Coordinates> {
    protected static final Comparator<Coordinates> READING_ORDER_COMPARATOR =
            Comparator.comparing(Coordinates::y).thenComparing(Coordinates::x);
    protected static final Comparator<Coordinates> DISPLAY_ORDER_COMPARATOR =
            Comparator.comparing(Coordinates::x).thenComparing(Coordinates::y);

    protected static Coordinates at(int x, int y) {
        return new Coordinates(x, y);
    }

    protected static Coordinates from(Coordinates coords) {
        return new Coordinates(coords.x(), coords.y());
    }

    // Returns the Manhattan distance between 2 coordinates.
    protected static int manhattan(Coordinates start, Coordinates end) {
        return Math.abs(start.x() - end.x()) + Math.abs(start.y() - end.y());
    }

    protected int manhattan(Coordinates that) {
        return manhattan(this, that);
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
        return "(%d, %d)".formatted(x, y);
    }
}
