package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AOC2018Day15 extends AOCDay<Integer> {
    public AOC2018Day15() throws IOException, URISyntaxException {
        super(15, 2018);
        var cavern = Cavern.parse(super.input);
        assert cavern.goblins().size() == 20;
        assert cavern.elves().size() == 10;
        LOGGER.debug(cavern);
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
                            .sorted(Unit.COMPARE_BY_DISPLAY_COORDINATES)
                            .map(Unit::toString)
                            .toList()
                            .toString())
                    .append("\n");
            sb.append("  Elves (")
                    .append("%02d".formatted(elves.size()))
                    .append("): ")
                    .append(elves.stream()
                            .sorted(Unit.COMPARE_BY_DISPLAY_COORDINATES)
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
        private static final Comparator<Unit> COMPARE_BY_READING_COORDINATES = Comparator
                .comparing(Unit::y)
                .thenComparing(Unit::x);
        private static final Comparator<Unit> COMPARE_BY_DISPLAY_COORDINATES = Comparator
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
}
