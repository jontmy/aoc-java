package solvers.aoc2018;

import solvers.AOCDay;
import utils.Pair;
import utils.RegexUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.RegexUtils.*;

public class AOC2018Day9 extends AOCDay<Integer> {
    private final int players, lastMarble;

    public AOC2018Day9() throws IOException, URISyntaxException {
        super(9, 2018);
        assert input.size() == 1;
        var regex = join(group(min(ANY_DIGIT, 1)),
                " players; last marble is worth ",
                group(min(ANY_DIGIT, 1)),
                " points"
        );
        var match = Pattern.compile(regex)
                .matcher(input.get(0))
                .results()
                .findFirst()
                .orElseThrow();
        players = Integer.parseInt(match.group(1));
        lastMarble = Integer.parseInt(match.group(2));
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var game = new MarbleGame(players, lastMarble);
        game.simulateGame();
        return Arrays.stream(game.scores)
                .max()
                .orElseThrow();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
    }

    private static class MarbleGame {
        private final CircularArrayList<Integer> circle;
        private final int players, lastMarble;
        private final int[] scores;
        private int currentPlayer, currentMarble;

        private MarbleGame(int players, int lastMarble) {
            this.circle = new CircularArrayList<>();
            this.players = players;
            this.lastMarble = lastMarble;
            this.currentPlayer = 0;
            this.currentMarble = 0;
            this.scores = new int[players];
            addMarble(currentMarble++);
        }

        private void simulateTurn() {
            if (currentMarble % 23 == 0) {
                scores[currentPlayer] += currentMarble + circle.remove(-7);
            } else {
                addMarble(currentMarble);
            }
        }

        private void simulateGame() {
            for (int i = 1; i <= lastMarble; i++) {
                simulateTurn();
                currentMarble++;
                currentPlayer = ++currentPlayer % players;
                // LOGGER.debug("[{}] {}", currentPlayer, circle);
            }
        }

        private void addMarble(int marble) {
            assert marble <= lastMarble;
            if (circle.size() == 0) {
                circle.add(marble);
                circle.rotate(0, false);
            } else {
                circle.add(1, marble);
            }
        }

        public int[] scores() {
            return scores;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static class CircularArrayList<T> {
        private final List<T> backing;
        private int pointer;

        private CircularArrayList() {
            this.backing = new ArrayList<>();
            this.pointer = -1;
        }

        private T get() {
            assert pointer < size() : "pointer is not smaller than the size of the list";
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            assert !backing.isEmpty() : "get() called on empty list";
            return backing.get(pointer);
        }

        private T get(int relativeIndex) {
            assert pointer < size() : "pointer is not smaller than the size of the list";
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            assert !backing.isEmpty() : "get() called on empty list";
            int index = (pointer + relativeIndex) % size();
            while (index < 0) index += size();
            return backing.get(index);
        }

        private void add(T element) {
            assert pointer < size() : "pointer is not smaller than the size of the list";
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            backing.add(++pointer, element);
            assert pointer < size() : "pointer is not smaller than the size of the list";
        }

        private void add(int relativeIndex, T element) {
            assert pointer < size() : "pointer is not smaller than the size of the list";
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            int index = ((pointer + relativeIndex) % size()) + 1;
            while (index < 0) index += size();
            backing.add(index, element);
            pointer = index;
            assert pointer < size() : "pointer is not smaller than the size of the list";
        }

        private T remove() {
            assert pointer < size() : "pointer is not smaller than the size of the list";
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            assert !backing.isEmpty() : "remove() called on empty list";
            var removed = backing.remove(pointer);
            if (backing.isEmpty()) pointer = -1;
            else pointer = (pointer - 1) % size();
            return removed;
        }

        private T remove(int relativeIndex) {
            assert pointer < size() : "pointer is not smaller than the size of the list";
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            assert !backing.isEmpty() : "remove() called on empty list";

            rotate(relativeIndex, true);
            var removed = remove();
            if (backing.isEmpty()) pointer = -1;
            else rotate(1, true);
            return removed;
        }

        private void rotate(int index, boolean isRelative) {
            assert !backing.isEmpty() || pointer == -1 : "list is empty, but pointer is not -1";
            assert !backing.isEmpty() : "rotate() called on empty list";
            if (isRelative) {
                pointer = (pointer + index) % size();
                while (pointer < 0) pointer += size();
            } else {
                if (index < 0 || index >= size()) throw new IllegalArgumentException(String.valueOf(index));
                pointer = index;
            }
        }

        private void clear() {
            backing.clear();
            pointer = -1;
        }

        private int size() {
            return backing.size();
        }

        @Override
        public String toString() {
            if (backing.isEmpty()) return "[]";
            var sb = new StringBuilder("[");
            backing.subList(0, pointer)
                    .forEach(e -> sb.append(e).append(", "));
            sb.append("*(").append(backing.get(pointer)).append(")*, ");
            backing.subList(pointer + 1, size())
                    .forEach(e -> sb.append(e).append(", "));
            if (sb.charAt(sb.length() - 2) == ',' && sb.charAt(sb.length() - 1) == ' ') {
                sb.deleteCharAt(sb.length() - 2).deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        var circle = new CircularArrayList<Integer>();
        assert circle.size() == 0;
        assert circle.pointer == -1;

        circle.add(0);
        LOGGER.debug(circle);
        assert circle.size() == 1;
        assert circle.pointer == 0;

        circle.add(1);
        LOGGER.debug(circle);
        assert circle.size() == 2;
        assert circle.pointer == 1;

        circle.add(0, 2);
        LOGGER.debug(circle);
        assert circle.size() == 3;
        assert circle.pointer == 2;

        circle.add(1, 3);
        LOGGER.debug(circle);
        assert circle.size() == 4;
        assert circle.pointer == 1;

        circle.add(-1, 4);
        LOGGER.debug(circle);
        assert circle.size() == 5;
        assert circle.pointer == 1;

        circle.add(-3, 5);
        LOGGER.debug(circle);
        assert circle.size() == 6;
        assert circle.pointer == 4;

        String elements;
        elements = IntStream.range(0, circle.size() * 2)
                .map(circle::get)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", "));
        LOGGER.debug(elements);
        elements = IntStream.range(0, circle.size() * 2)
                .map(i -> circle.get(i * -1))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", "));
        LOGGER.debug(elements);

        int element = circle.get();
        for (int i = 1; i < circle.size(); i++) {
            assert element == circle.get();
            element = circle.get();
        }
        LOGGER.info("Testing clockwise rotate()");
        LOGGER.debug(circle);
        for (int i = 0; i < circle.size() * 2; i++) {
            circle.rotate(2, true);
            LOGGER.debug(circle);
        }
        LOGGER.info("Testing counter-clockwise rotate()");
        LOGGER.debug(circle);
        for (int i = 0; i < circle.size() * 2; i++) {
            circle.rotate(-2, true);
            LOGGER.debug(circle);
        }

        LOGGER.info("Testing in-place remove()");
        LOGGER.debug(circle);
        while (circle.size() > 0) {
            circle.remove();
            LOGGER.debug(circle);
        }

        for (int i = 1; i < 20; i++) {
            circle.add(i);
        }
        LOGGER.info("Testing absolute rotate() with 19 elements");
        circle.rotate(9, false);
        LOGGER.debug(circle);

        LOGGER.info("Testing relative clockwise remove()");
        LOGGER.debug(circle);
        int delta = -1;
        while (circle.size() > 0) {
            circle.remove(++delta);
            LOGGER.debug("relative +" + delta + " -> " + circle);
        }
        LOGGER.debug(circle);


        LOGGER.info("Testing relative counter-clockwise remove()");
        for (int i = 1; i < 20; i++) {
            circle.add(i);
        }
        LOGGER.debug(circle);
        delta = 1;
        while (circle.size() > 0) {
            circle.remove(--delta);
            LOGGER.debug("relative " + delta + " -> " + circle);
        }
        LOGGER.debug(circle);
    }
}
