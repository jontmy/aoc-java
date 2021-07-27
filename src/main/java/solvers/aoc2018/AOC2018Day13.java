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
    }

    @Override
    protected String solvePartOne(List<String> input) {
        var system = CartTrackSystem.from(initial);
        while (true) {
            if (system.tickInterruptingAfterCollision()) break;
        }
        var collisions = system.collisions();
        assert !collisions.isEmpty() : "System was interrupted but no collisions were found.";
        assert collisions.size() == 2 : "System was interrupted but more than 2 carts collided.";
        assert system.collisionsOccurred();
        var iterator = collisions.iterator();
        var first = iterator.next();
        var second = iterator.next();
        assert first.collidesWith(second) : "Carts do not collide.";
        return first.x() + "," + first.y();
    }

    @Override
    protected String solvePartTwo(List<String> input) {
        var system = CartTrackSystem.from(initial);
        while (system.carts().size() > 1) {
            system.tickRemovingCollidedCarts();
        }
        assert system.carts().size() == 1;
        var cart = system.carts().get(0);
        return cart.x() + "," + cart.y();
    }

    private record CartTrackSystem(int width, int height, char[][] tracks, List<Cart> carts) {
        private CartTrackSystem(int width, int height, char[][] tracks, List<Cart> carts) {
            this.width = width;
            this.height = height;
            this.carts = carts.stream()
                    .map(Cart::from)
                    .collect(Collectors.toList());
            this.tracks = tracks;
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
                            carts.add(Cart.of(carts.size(), x, y, Cart.Direction.LEFTWARD));
                        }
                        case '>' -> {
                            tracks[x][y] = '-';
                            carts.add(Cart.of(carts.size(), x, y, Cart.Direction.RIGHTWARD));
                        }
                        case '^' -> {
                            tracks[x][y] = '|';
                            carts.add(Cart.of(carts.size(), x, y, Cart.Direction.UPWARD));
                        }
                        case 'v' -> {
                            tracks[x][y] = '|';
                            carts.add(Cart.of(carts.size(), x, y, Cart.Direction.DOWNWARD));
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
            return collisionsOccurred();
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

        // Moves all carts 1 step from a top-down, then left-to-right order.
        // Carts are removed instantaneously after they collide.
        private void tickRemovingCollidedCarts() {
            Collections.sort(carts); // order from top-down, then left-to-right

            // Run simulations for this tick until all colliding carts are removed.
            CartTrackSystem simulation = CartTrackSystem.from(this);
            while (simulation.tickInterruptingAfterCollision()) {
                this.carts().removeAll(simulation.collisions());
                simulation = CartTrackSystem.from(this);
            }

            // Run an actual tick.
            carts.forEach(this::move);
            assert !this.collisionsOccurred();
        }

        private boolean collisionsOccurred() {
            var distinct = carts.stream()
                    .map(c -> Pair.of(c.x(), c.y()))
                    .distinct()
                    .count();
            return distinct != carts.size();
        }

        // Returns the set of carts that have collided with another cart in the current state.
        // O(n^2) check for collisions. Use collisionsOccurred() instead of collisions().isEmpty() to test for collisions.
        private Set<Cart> collisions() {
            var collisions = new HashSet<Cart>();
            for (int i = 0; i < carts.size(); i++) {
                var first = carts.get(i);
                for (int j = i + 1; j < carts.size(); j++) {
                    var second = carts.get(j);
                    if (first.collidesWith(second)) {
                        collisions.add(first);
                        collisions.add(second);
                    }
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
                    } else {
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
        private final int id;
        private int x, y;
        private Direction direction;
        private Intersection choice;

        private Cart(int id, int x, int y, Direction direction) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.choice = Intersection.LEFT; // it turns left the first time [it encounters an intersection]
        }

        private Cart(int id, int x, int y, Direction direction, Intersection choice) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.choice = choice;
        }

        private static Cart of(int id, int x, int y, Direction direction) {
            return new Cart(id, x, y, direction);
        }

        private static Cart from(Cart cart) {
            return new Cart(cart.id(), cart.x(), cart.y(), cart.direction(), cart.choice);
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
                case LEFT -> direction = switch (direction) {
                    case LEFTWARD -> Direction.DOWNWARD;
                    case RIGHTWARD -> Direction.UPWARD;
                    case UPWARD -> Direction.LEFTWARD;
                    case DOWNWARD -> Direction.RIGHTWARD;
                };
                case RIGHT -> direction = switch (direction) {
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

        private int id() {
            return id;
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

        private boolean collidesWith(Cart that) {
            return this.x() == that.x() && this.y() == that.y();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cart cart = (Cart) o;
            return id == cart.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        // Carts on the top row move first (acting from left to right), then carts on the second row move
        // (again from left to right), then carts on the third row, and so on.
        public int compareTo(Cart that) {
            return Comparator.comparing(Cart::y).thenComparing(Cart::x).compare(this, that);
        }

        @Override
        public String toString() {
            return "Cart " + id + " @ " + "x = " + x + ", y = " + y + ", heading " +
                    direction.toString().toLowerCase() + ", and will proceed " +
                    choice.toString().toLowerCase() + " at the next intersection.";
        }

        private enum Direction {LEFTWARD, RIGHTWARD, UPWARD, DOWNWARD}

        private enum Intersection {LEFT, STRAIGHT, RIGHT}
    }
}
