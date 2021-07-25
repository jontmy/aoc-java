package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static utils.RegexUtils.*;

public class AOC2018Day9 extends AOCDay<Long> {
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
    protected Long solvePartOne(List<String> input) {
        var game = new MarbleGame(players, lastMarble);
        game.simulateGame();
        return game.winner();
    }

    @Override
    protected Long solvePartTwo(List<String> input) {
        var game = new MarbleGame(players, lastMarble * 100);
        game.simulateGame();
        return game.winner();
    }

    private static class MarbleGame {
        private final int players, lastMarble;
        private final long[] scores;
        private Marble cursor;
        private int currentPlayer, currentMarble;

        private MarbleGame(int players, int lastMarble) {
            assert players > 2;
            assert lastMarble > 2;
            var zero = new Marble(0, null, null);
            var two = new Marble(2, zero, null);
            var one = new Marble(1, two, zero);
            two.setNext(one);
            zero.setPrevious(one);
            zero.setNext(two);
            this.cursor = two;
            this.players = players;
            this.lastMarble = lastMarble;
            this.currentPlayer = 3;
            this.currentMarble = 3;
            this.scores = new long[players];
        }

        private void simulateTurn() {
            if (currentMarble % 23 == 0) {
                cursor = cursor.rotate(-7);
                scores[currentPlayer] += currentMarble + cursor.remove().getID();
                cursor = cursor.getNext();
            } else {
                cursor = cursor.rotate(+1);
                cursor = cursor.add(currentMarble, cursor, cursor.getNext());
            }
            currentMarble++;
            currentPlayer = ++currentPlayer % players;
        }

        private void simulateGame() {
            while (currentMarble <= lastMarble) simulateTurn();
        }

        public long winner() {
            return Arrays.stream(scores)
                    .max()
                    .orElseThrow();
        }
    }

    private static class Marble {
        private final int id;
        private Marble previous, next;

        private Marble(int id, Marble previous, Marble next) {
            this.id = id;
            this.previous = previous;
            this.next = next;
        }

        private Marble rotate(int delta) {
            if (delta == 0) return this;
            else if (delta < 0) {
                var cursor = this;
                for (int i = 0; i > delta; i--) {
                    cursor = cursor.getPrevious();
                }
                return cursor;
            } else {
                var cursor = this;
                for (int i = 0; i < delta; i++) {
                    cursor = cursor.getNext();
                }
                return cursor;
            }
        }

        private Marble add(int id, Marble previous, Marble next) {
            var added = new Marble(id, previous, next);
            previous.setNext(added);
            next.setPrevious(added);
            return added;
        }

        private Marble remove() {
            var previous = this.getPrevious();
            var next = this.getNext();
            previous.setNext(next);
            next.setPrevious(previous);
            return this;
        }

        private Marble getPrevious() {
            return previous;
        }

        private void setPrevious(Marble previous) {
            this.previous = previous;
        }

        private Marble getNext() {
            return next;
        }

        private void setNext(Marble next) {
            this.next = next;
        }

        public int getID() {
            return id;
        }
    }
}
