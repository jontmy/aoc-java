package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.RegexUtils.*;

public class AOC2018Day6 extends AOCDay<Long> {
    public AOC2018Day6() throws IOException, URISyntaxException {
        super(6, 2018);
    }

    private List<Destination> parse(List<String> input) {
        var regex = join(group(min(ANY_DIGIT, 1)),
                ", ", group(min(ANY_DIGIT, 1)));
        var pattern = Pattern.compile(regex);
        var unadjusted = input.stream()
                .map(pattern::matcher)
                .flatMap(matcher -> matcher.results()
                        .findFirst()
                        .stream())
                .map(match -> Destination.ofCoordinates(
                        Integer.parseInt(match.group(1)),
                        Integer.parseInt(match.group(2))))
                .toList();
        var minX = unadjusted.stream()
                .map(Destination::x)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        var minY = unadjusted.stream()
                .map(Destination::y)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        var minXY = Destination.ofCoordinates(minX, minY);
        return IntStream.range(0, unadjusted.size())
                .mapToObj(id -> unadjusted.get(id).relative(minXY, id + 1))
                .toList();
    }

    @Override
    protected Long solvePartOne(List<String> input) {
        var destinations = parse(input);
        var cm = new CoordinateMap(destinations);

        // Plots the coordinate map of the IDs of the closest destination from each coordinate, or -2 if tied,
        // or -3 if the coordinate lies in an infinite area.
        var copy = new int[cm.height][cm.length];
        for (int i = 0; i < cm.height; i++) {
            for (int j = 0; j < cm.length; j++) {
                var closestDestination = cm.closestDestination(j, i);
                cm.map[i][j] = closestDestination.map(Destination::id).orElse(-2);
                copy[i][j] = cm.map[i][j];
            }
        }
        // Check for coordinates in infinite areas.
        for (int i = 0; i < cm.height; i++) {
            for (int j = 0; j < cm.length; j++) {
                if (cm.map[i][j] < 0) continue;
                if (!CoordinateMap.finite(j, i, copy)) cm.map[i][j] = -3;
            }
        }
        return cm.areasByID().get(cm.largestAreaID());
    }

    @Override
    protected Long solvePartTwo(List<String> input) {
        var destinations = parse(input);
        var cm = new CoordinateMap(destinations);
        return IntStream.range(0, cm.height)
                .mapToObj(y -> IntStream.range(0, cm.length).mapToObj(x -> Pair.of(x, y)))
                .flatMap(Function.identity())
                .map(xy -> cm.totalManhattan(xy.left(), xy.right()))
                .filter(totalDistance -> totalDistance < 10000)
                .count();
    }

    private static class CoordinateMap {
        private final List<Destination> destinations;
        private final int length, height;
        private final int[][] map;

        private CoordinateMap(List<Destination> destinations) {
            this.destinations = List.copyOf(destinations);
            this.length = destinations.stream()
                    .map(Destination::x)
                    .max(Comparator.naturalOrder())
                    .orElseThrow() + 1;
            this.height = destinations.stream()
                    .map(Destination::y)
                    .max(Comparator.naturalOrder())
                    .orElseThrow() + 1;
            this.map = new int[height][length];
        }

        // Returns true if the coordinate on the map lies in a finite area, or false if it lies in an infinite area.
        // An area is finite if it is interrupted by another area (ID) in all 4 of the cardinal directions.
        private static boolean finite(int x, int y, int[][] map) {
            var id = map[y][x];
            var height = map.length;
            var length = map[0].length;
            var west = Arrays.stream(map[y], 0, x)
                    .anyMatch(areaID -> areaID != id);
            var east = Arrays.stream(map[y], x + 1, length)
                    .anyMatch(areaID -> areaID != id);
            var north = IntStream.range(0, y)
                    .map(i -> map[i][x])
                    .anyMatch(areaID -> areaID != id);
            var south = IntStream.range(y + 1, height)
                    .map(i -> map[i][x])
                    .anyMatch(areaID -> areaID != id);
            return north && south && east && west;
        }

        // Returns the closest destination, or an empty optional if closest distance is tied between 2 destinations.
        // Distance is calculated by Destination::manhattan.
        private Optional<Destination> closestDestination(int x, int y) {
            var manhattans = destinations.stream()
                    .map(dest -> dest.manhattan(x, y))
                    .sorted(Comparator.naturalOrder())
                    .limit(2)
                    .toList();
            if (manhattans.get(0).equals(manhattans.get(1)))
                return Optional.empty(); // closest distance tied between 2 destinations
            var destination = destinations.stream()
                    .filter(dest -> dest.manhattan(x, y) == manhattans.get(0))
                    .toList();
            assert destination.size() == 1;
            return Optional.of(destination.get(0));
        }

        // Returns the total Manhattan distance from a coordinate to every destination.
        private int totalManhattan(int x, int y) {
            return destinations.stream()
                    .map(dest -> dest.manhattan(x, y))
                    .reduce(Integer::sum)
                    .orElse(0);
        }

        private Map<Integer, Long> areasByID() {
            return Arrays.stream(map)
                    .flatMapToInt(Arrays::stream)
                    .boxed()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }

        private Integer largestAreaID() {
            return areasByID().entrySet().stream()
                    .filter(entry -> entry.getKey() > 0)
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElseThrow();
        }
    }

    private record Destination(Pair<Integer, Integer> coordinates, int id) {
        private Destination(Pair<Integer, Integer> coordinates, int id) {
            this.coordinates = Pair.from(coordinates);
            this.id = id;
        }

        private static Destination ofCoordinates(int x, int y) {
            return new Destination(Pair.of(x, y), -1);
        }

        private static Destination ofCoordinatesAndID(int x, int y, int id) {
            return new Destination(Pair.of(x, y), id);
        }

        private Destination relative(Destination that, int newID) {
            return Destination.ofCoordinatesAndID(this.x() - that.x(), this.y() - that.y(), newID);
        }

        private int manhattan(int x, int y) {
            return Math.abs(this.x() - x) + Math.abs(this.y() - y);
        }

        private int x() {
            return coordinates.left();
        }

        private int y() {
            return coordinates.right();
        }

        @Override
        public String toString() {
            return id() + " -> " + coordinates;
        }
    }
}
