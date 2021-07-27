package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AOC2018Day13 extends AOCDay<String> {
    private final CartTrackSystem initial;

    public AOC2018Day13() throws IOException, URISyntaxException {
        super(13, 2018);
        this.initial = CartTrackSystem.parse(super.input);
        LOGGER.debug(initial);
    }

    @Override
    protected String solvePartOne(List<String> input) {
        var system = CartTrackSystem.from(initial);
        return "";
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        var system = CartTrackSystem.from(initial);
        return "";
    }

    private record CartTrackSystem(int width, int height, char[][] tracks, List<Cart> carts) {
        private CartTrackSystem(int width, int height, char[][] tracks, List<Cart> carts) {
            this.width = width;
            this.height = height;
            this.carts = carts.stream()
                    .map(Cart::from)
                    .collect(Collectors.toList());
            var trx = new char[width][height];
            for (int x = 0; x < width; x++) {
                System.arraycopy(tracks[x], 0, trx[x], 0, height);
            }
            this.tracks = trx;
        }

        private static CartTrackSystem of(int width, int height, char[][] tracks, List<Cart> carts) {
            return new CartTrackSystem(width, height, tracks, carts);
        }

        private static CartTrackSystem from(CartTrackSystem system) {
            return new CartTrackSystem(system.width(), system.height(), system.tracks(), system.carts());
        }

        private static CartTrackSystem parse(List<String> input) {
            var height = input.size();
            var width = input.stream()
                    .map(String::length)
                    .reduce(Integer::max)
                    .orElseThrow();
            var carts = new ArrayList<Cart>();
            var tracks = new char[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    var track = x < input.get(y).length() ? input.get(y).charAt(x) : ' ';
                    switch (track) {
                        case ' ', '/', '\\', '|', '-', '+' -> tracks[x][y] = track;
                        case '<' -> {
                            tracks[x][y] = '-';
                            carts.add(Cart.of(x, y, Cart.Direction.LEFTWARD));
                        }
                        case '>' -> {
                            tracks[x][y] = '-';
                            carts.add(Cart.of(x, y, Cart.Direction.RIGHTWARD));
                        }
                        case '^' -> {
                            tracks[x][y] = '|';
                            carts.add(Cart.of(x, y, Cart.Direction.UPWARD));
                        }
                        case 'v' -> {
                            tracks[x][y] = '|';
                            carts.add(Cart.of(x, y, Cart.Direction.DOWNWARD));
                        }
                        default -> throw new AssertionError("Malformed track at %s: %s".formatted(Pair.of(x, y), track));
                    }
                }
            }
            return CartTrackSystem.of(width, height, tracks, carts);
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append(width).append("x").append(height).append(" cart-and-track system:\n");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    sb.append(tracks[x][y]);
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private static final class Cart {
        private int x, y;
        private Direction direction;
        private Intersection choice;

        private Cart(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.choice = Intersection.LEFT;
        }

        private static Cart of(int x, int y, Direction direction) {
            return new Cart(x, y, direction);
        }

        private static Cart from(Cart cart) {
            return new Cart(cart.x(), cart.y(), cart.direction());
        }

        private int x() {
            return x;
        }

        private int y() {
            return y;
        }

        private Direction direction() {
            return direction;
        }

        // Moves 1 step in the current direction.
        private void move() {

        }

        // Rotates to face a specified direction, then moves 1 step in that direction.
        private void move(Direction direction) {

        }

        private enum Direction {LEFTWARD, RIGHTWARD, UPWARD, DOWNWARD}

        private enum Intersection {LEFT, STRAIGHT, RIGHT}
    }
}
