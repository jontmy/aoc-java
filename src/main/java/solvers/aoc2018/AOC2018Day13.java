package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
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
        while (true) {
            if (system.tickInterruptingAfterCollision()) break;
        }
        var collisions = system.collisions();
        assert !collisions.isEmpty() : "System was interrupted but no collisions were found.";
        assert collisions.size() == 1 : "System was interrupted but more than 1 collision was found.";
        var collision = collisions.get(0);
        assert collision.left().x() == collision.right().x() && collision.left().y() == collision.right().y() : "Non-identical collision coordinates.";
        return collision.left().x() + "," + collision.left().y();
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
                        default -> throw new AssertionError("Malformed track at (%s, %s): %s".formatted(x, y, track));
                    }
                }
            }
            return CartTrackSystem.of(width, height, tracks, carts);
        }

        // Moves a cart 1 step based on the track it is currently on.
        // Returns true if a collision occurred after the move, otherwise false.
        private boolean move(Cart cart) {
            switch (tracks[cart.x()][cart.y()]) {
                case '|', '-' -> cart.move();
                case '/' -> {
                    switch (cart.direction()) {
                        case LEFTWARD, RIGHTWARD -> cart.move(Cart.Intersection.LEFT);
                        case UPWARD, DOWNWARD -> cart.move(Cart.Intersection.RIGHT);
                    }
                }
                case '\\' -> {
                    switch (cart.direction()) {
                        case LEFTWARD, RIGHTWARD -> cart.move(Cart.Intersection.RIGHT);
                        case UPWARD, DOWNWARD -> cart.move(Cart.Intersection.LEFT);
                    }
                }
                case '+' -> cart.move(cart.choose());
            }
            // Efficient collision check - true if and only if any 2 or more carts share the same position.
            return carts.stream().distinct().count() != carts.size();
        }

        // Moves all carts 1 step from a top-down, then left-to-right order, interrupting if any carts collide.
        // Returns true if any cart collided with another, false if every cart moved without colliding with another.
        private boolean tickInterruptingAfterCollision() {
            Collections.sort(carts); // order from top-down, then left-to-right
            for (Cart cart : carts) {
                if (move(cart)) return true;
            }
            return false;
        }

        // Returns the pairs of carts that have collided in the current state.
        // O(n^2) check for collisions. Use 'carts.stream().distinct().count() != carts.size()' instead of
        // 'collisions().isEmpty()' to test for collisions.
        private List<Pair<Cart, Cart>> collisions() {
            var collisions = new ArrayList<Pair<Cart, Cart>>();
            for (int i = 0; i < carts.size(); i++) {
                var first = carts.get(i);
                for (int j = i + 1; j < carts.size(); j++) {
                    var second = carts.get(j);
                    if (first.equals(second)) collisions.add(Pair.of(first, second));
                }
            }
            return collisions;
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append(width).append("x").append(height).append(" cart-and-track system:\n");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Cart present = null;
                    for (Cart cart : carts) {
                        if (cart.x() == x && cart.y() == y) present = cart;
                    }
                    if (present == null) {
                        sb.append(tracks[x][y]);
                    }
                    else {
                        switch (present.direction) {
                            case LEFTWARD -> sb.append("<");
                            case RIGHTWARD -> sb.append(">");
                            case UPWARD -> sb.append("^");
                            case DOWNWARD -> sb.append("v");
                        }
                    }
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
                case UPWARD -> y--;
                case DOWNWARD -> y++;
            }
        }

        // Rotates to face a specified direction (Intersection.LEFT or Intersection.RIGHT), then moves 1 step in that direction.
        // Disallows 180 degree rotations (U-turns).
        // Does not check that the move is valid, i.e., that there is a track 1 step in that direction.
        private void move(Intersection rotation) {
            switch (rotation) {
                case LEFT -> direction = switch(direction) {
                    case LEFTWARD -> Direction.DOWNWARD;
                    case RIGHTWARD -> Direction.UPWARD;
                    case UPWARD -> Direction.LEFTWARD;
                    case DOWNWARD -> Direction.RIGHTWARD;
                };
                case RIGHT -> direction = switch(direction) {
                    case LEFTWARD -> Direction.UPWARD;
                    case RIGHTWARD -> Direction.DOWNWARD;
                    case UPWARD -> Direction.RIGHTWARD;
                    case DOWNWARD -> Direction.LEFTWARD;
                };
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cart cart = (Cart) o;
            if (x != cart.x) return false;
            return y == cart.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
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
