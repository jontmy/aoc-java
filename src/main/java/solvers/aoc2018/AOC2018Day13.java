package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
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
                        // Tracks consist of straight paths (| and -), curves (/ and \), and intersections (+).
                        // Curves connect exactly two perpendicular tracks.
                        // Intersections occur when two perpendicular paths cross.
                        case ' ', '/', '\\', '|', '-', '+' -> tracks[x][y] = track;
                        // Several carts are also on the tracks. Carts always face either up (^), down (v), left (<), or right (>).
                        // On your initial map, the track under each cart is a straight path matching the direction the cart is facing.
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
            sb.append("\nCarts:\n");
            for (Cart cart : carts) {
                sb.append(cart).append("\n");
            }
            return sb.toString();
        }
    }

    private static final class Cart implements Comparable<Cart> {
        private int x, y;
        private Direction direction;
        private Intersection choice;

        private Cart(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.choice = Intersection.LEFT; // it turns left the first time [it encounters an intersection]
        }

        private static Cart of(int x, int y, Direction direction) {
            return new Cart(x, y, direction);
        }

        private static Cart from(Cart cart) {
            return new Cart(cart.x(), cart.y(), cart.direction());
        }

        // Moves 1 step in the current direction.
        // Does not check that the move is valid, i.e., that there is a track 1 step in that direction.
        private void move() {
            switch (direction) {
                case LEFTWARD -> x--;
                case RIGHTWARD -> x++;
                case UPWARD -> y++;
                case DOWNWARD -> y--;
            }
        }

        // Rotates to face a specified direction, then moves 1 step in that direction.
        // Disallows 180 degree rotations (U-turns).
        // Does not check that the move is valid, i.e., that there is a track 1 step in that direction.
        private void move(Direction rotated) {
            switch (rotated) {
                case LEFTWARD, RIGHTWARD -> {
                    if (direction == Direction.DOWNWARD || direction == Direction.UPWARD) direction = rotated;
                    else throw new IllegalArgumentException(rotated.toString());
                }
                case UPWARD, DOWNWARD -> {
                    if (direction == Direction.LEFTWARD || direction == Direction.RIGHTWARD) direction = rotated;
                    else throw new IllegalArgumentException(rotated.toString());
                }
            }
            move();
        }

        // Each time a cart has the option to turn (by arriving at any intersection), it turns left the first time,
        // goes straight the second time, turns right the third time, and then repeats those directions starting again,
        // with left the fourth time, straight the fifth time, and so on.
        private Intersection choose() {
            var outcome = choice;
            choice = switch (outcome) {
                case LEFT -> Intersection.STRAIGHT;
                case STRAIGHT -> Intersection.RIGHT;
                case RIGHT -> Intersection.LEFT;
            };
            return outcome;
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

        @Override
        // Carts on the top row move first (acting from left to right), then carts on the second row move
        // (again from left to right), then carts on the third row, and so on.
        public int compareTo(Cart that) {
            return Comparator.comparing(Cart::y).thenComparing(Cart::x).compare(this, that);
        }

        @Override
        public String toString() {
            return "Cart @ " + "x = " + x + ", y = " + y + ", heading " +
                    direction.toString().toLowerCase() + ", and will proceed " +
                    choice.toString().toLowerCase() + " at the next intersection.";
        }

        private enum Direction {LEFTWARD, RIGHTWARD, UPWARD, DOWNWARD}

        private enum Intersection {LEFT, STRAIGHT, RIGHT}
    }
}
