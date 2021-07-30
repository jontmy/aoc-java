package solvers.aoc2018.day15;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import utils.SetUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

abstract class Unit implements Comparable<Unit> {
    protected static final Comparator<Unit> READING_ORDER_COMPARATOR =
            Comparator.comparing(Unit::y).thenComparing(Unit::x);
    protected static final Comparator<Unit> DISPLAY_ORDER_COMPARATOR =
            Comparator.comparing(Unit::x).thenComparing(Unit::y);
    protected static final int INITIAL_HP = 200;
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Unit.class);
    protected final Cavern cavern;
    protected final int atk;

    protected int x;
    protected int y;
    protected int hp;

    protected Unit(Cavern cavern, Coordinates coordinates, int atk) {
        Objects.requireNonNull(cavern);
        this.cavern = cavern;
        this.atk = atk;

        Objects.requireNonNull(coordinates);
        if (coordinates.x() < 0) throw new IllegalArgumentException(coordinates.toString());
        if (coordinates.y() < 0) throw new IllegalArgumentException(coordinates.toString());
        this.x = coordinates.x();
        this.y = coordinates.y();
        this.hp = INITIAL_HP;
    }

    // Returns the coordinates of the squares adjacent to the square this unit is on.
    private List<Coordinates> adjacent() {
        return this.cavern.adjacent(this.x, this.y);
    }

    // Each unit begins its turn by identifying all possible targets (enemy units).
    protected abstract List<? extends Unit> enemies();

    // Then, the unit identifies all the open squares (.) that are in range of each target;
    // these are the squares which are adjacent (immediately up, down, left, or right) to any target and
    // which aren't already occupied by a wall or another unit.
    private List<Coordinates> targets() {
        return enemies().stream()
                .map(Unit::coordinates)
                .flatMap(c -> cavern.adjacent(c).stream())
                .filter(c -> cavern.at(c) == Cavern.TRAVERSABLE)
                .toList();
    }

    // Returns true if this unit is adjacent to an enemy unit, otherwise false.
    private boolean isInRangeOfEnemy() {
        var adjacent = adjacent();
        return enemies().stream()
                .map(Unit::coordinates)
                .anyMatch(adjacent::contains);
    }

    // Returns the best square that is in range of an enemy for this unit to lock on to, if it exists.
    // If multiple squares are in range and tied for being reachable in the fewest steps,
    // the square which is first in reading order is chosen.
    private Optional<Coordinates> target() {
        // Case 1: No enemies are present.
        if (this.enemies().isEmpty()) return Optional.empty();

        // Case 2: Enemies are present, but none are targetable.
        if (this.targets().isEmpty()) return Optional.empty();

        // Case 3: Enemies are present and targetable, and this unit is already adjacent to one, i.e., on a target square.
        assert this.enemies().stream()
                .map(Unit::coordinates)
                .noneMatch(this.coordinates()::equals)
                : "Unit should not target itself.";
        if (this.isInRangeOfEnemy()) return Optional.of(this.coordinates());

        // Case 4: Enemies are present and targetable, but this unit is not in range of an enemy.
        var targets = this.targets();
        var queue = Set.of(this.coordinates());
        var searched = new HashSet<>(Set.of(this.coordinates()));
        Set<Coordinates> targetLocks;

        // Find the nearest square(s) to be in range of an enemy (the target squares) by breadth-first-search.
        do {
            // Replace the search queue with the squares that are adjacent to the previously searched squares.
            queue = cavern.adjacent(queue)
                    .stream()
                    .filter(not(searched::contains))
                    .filter(c -> cavern.at(c) == Cavern.TRAVERSABLE)
                    .collect(Collectors.toSet());

            // Add the target squares in the queue to the set of target locks.
            targetLocks = SetUtils.intersection(queue, targets);

            // Mark the squares in the queue as searched.
            searched.addAll(queue);

            // Repeat until all squares in the cavern have been searched, or target square(s) are found.
        } while (targetLocks.isEmpty() && !queue.isEmpty());

        // Return the best target square, if one exists, based on reading order,
        // as all target locked squares are equidistant from this unit's coordinates.
        return targetLocks.stream().min(Coordinates.READING_ORDER_COMPARATOR);
    }

    // Returns the distance of the shortest path from between 2 specified coordinates.
    private Optional<Integer> distance(Coordinates start, Coordinates end) {
        // Case 1: Any of the coordinates specified are out of bounds.
        if (start.x() < 0 || start.x() >= this.cavern.width())
            throw new IndexOutOfBoundsException(start.toString());
        if (start.y() < 0 || start.y() >= this.cavern.height())
            throw new IndexOutOfBoundsException(start.toString());
        if (end.x() < 0 || end.x() >= this.cavern.width())
            throw new IndexOutOfBoundsException(end.toString());
        if (end.y() < 0 || end.y() >= this.cavern.height())
            throw new IndexOutOfBoundsException(end.toString());

        // Case 2: Both coordinates are equal.
        if (start.equals(end)) return Optional.of(0);

        // Case 3: The square at the starting coordinates is completely surrounded by un-traversable terrain.
        if (this.cavern.adjacent(start).stream()
                .noneMatch(c -> this.cavern.at(c) == Cavern.TRAVERSABLE)) {
            return Optional.empty();
        }

        // Case 4: The square at the ending coordinates is completely surrounded by un-traversable terrain.
        if (this.cavern.adjacent(end).stream()
                .noneMatch(c -> this.cavern.at(c) == Cavern.TRAVERSABLE)) {
            return Optional.empty();
        }

        // Case 5: Otherwise, a path from the starting coordinates to the ending coordinates may exist.
        var queue = Set.of(start);
        var searched = new HashSet<>(Set.of(start));

        for (int distance = 1; !queue.isEmpty(); distance++) {
            // Replace the search queue with the squares that are adjacent to the previously searched squares.
            queue = cavern.adjacent(queue)
                    .stream()
                    .filter(not(searched::contains))
                    .filter(c -> cavern.at(c) == Cavern.TRAVERSABLE)
                    .collect(Collectors.toSet());

            // If a valid path can be found, return the distance found.
            if (queue.contains(end)) return Optional.of(distance);

            // Mark the squares in the queue as searched.
            searched.addAll(queue);
        }

        // Case 5: No path from this unit to the coordinates specified exists.
        return Optional.empty();
    }

    // Returns the single best step toward the target, if it exists.
    // If multiple steps would put the unit equally closer to its destination,
    // this unit chooses the step which is first in reading order.
    private Optional<Coordinates> pathfind(Coordinates end) {
        if (this.coordinates().equals(end)) return Optional.empty();
        var distances = new HashMap<Coordinates, Integer>();
        for (Coordinates start : adjacent()) {
            if (this.cavern.at(start) != Cavern.TRAVERSABLE) continue;
            this.distance(start, end).ifPresent(d -> distances.put(start, d));
        }
        if (distances.isEmpty()) return Optional.empty();
        var shortestDistance = distances.values().stream()
                .min(Comparator.naturalOrder())
                .orElseThrow();
        return distances.entrySet().stream()
                .filter(e -> e.getValue().equals(shortestDistance))
                .map(Map.Entry::getKey)
                .min(Coordinates.READING_ORDER_COMPARATOR);
    }

    // Returns true if this unit moved this turn, otherwise false.
    private boolean move() {
        // To move, the unit first considers the squares that are in range and
        // determines which of those squares it could reach in the fewest steps.
        var target = target();
        if (target.isPresent()) {
            LOGGER.debug("{} acquired a target lock on {}.", this, target.get());
        } else {
            // If the unit cannot reach (find an open path to) any of the squares that are in range, it ends its turn.
            LOGGER.debug("{} was unable to acquire a target lock on any enemy.", this);
        }

        // The unit then takes a single step toward the chosen square along the shortest path to that square.
        var move = target.flatMap(this::pathfind);
        if (move.isPresent()) {
            LOGGER.info("Moving 1 step toward target to {}.", move.get());
            assert this.coordinates().manhattan(move.get()) == 1 : "Unit may only move 1 step.";
            this.x = move.get().x();
            this.y = move.get().y();
        }
        return target.isPresent() && move.isPresent();
    }

    // Returns true if this unit attacked an enemy this turn, otherwise false.
    private boolean attack() {
        // To attack, the unit first determines all the targets that are in range of it
        // by being immediately adjacent to it.
        var adjacent = adjacent();
        var enemies = enemies().stream()
                .filter(enemy -> adjacent.contains(enemy.coordinates()))
                .toList();

        // If there are no such targets, the unit ends its turn.
        if (enemies.isEmpty()) {
            LOGGER.debug("{} has no enemies in range to attack.", this);
            return false;
        }

        // Otherwise, the adjacent target with the fewest hit points is selected.
        // In a tie, the adjacent target with the fewest hit points which is first in reading order is selected.
        var enemy = enemies.stream()
                .min(Comparator.comparing(Unit::hp)
                        .thenComparing(Unit::coordinates, Coordinates.READING_ORDER_COMPARATOR))
                .orElseThrow();
        LOGGER.debug("{} attacks {}!", this, enemy);

        // The unit deals damage equal to its attack power to the selected target,
        // reducing its hit points by that amount.
        enemy.hp -= this.atk;
        LOGGER.warn("{} deals {} damage to {}.", this, this.atk, enemy);

        // If this reduces its hit points to 0 or fewer, the selected target dies, taking no further turns.
        if (enemy.hp <= 0) {
            LOGGER.error("{} kills {}!", this, enemy);
            this.enemies().remove(enemy);
        }
        return true;
    }

    // Combat proceeds in rounds; in each round, each unit that is still alive takes a turn,
    // resolving all of its actions before the next unit's turn begins.
    // Returns true if this unit moved and/or attacked this turn, otherwise false.
    protected boolean act() {
        LOGGER.debug("{}'s turn...", this);

        // On each unit's turn, it tries to move into range of an enemy On each unit's turn,
        // it tries to move into range of an enemy (if it isn't already) and then attack (if it is in range).
        var moved = move();
        var attacked = attack();
        return moved || attacked;
    }

    protected int x() {
        return this.x;
    }

    protected int y() {
        return this.y;
    }

    protected int hp() {
        return this.hp;
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
        return "(%d, %d, %s HP)".formatted(x, y, hp);
    }
}
