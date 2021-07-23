package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static utils.DateUtils.*;
import static utils.RegexUtils.*;

public class AOC2018Day4 extends AOCDay<Integer> {
    public AOC2018Day4() throws IOException, URISyntaxException {
        super(4, 2018);
    }

    private static List<Entry> parseEntries(List<String> input) {
        return input.stream()
                .map(AOC2018Day4::parseEntry)
                .sorted()
                .toList();
    }

    private static Entry parseEntry(String input) {
        var regex = join(BEGINNING_OF_LINE,
                "\\[", String.join("-", REGEX_YYYY, REGEX_MM, REGEX_DD), " ",
                group(repeat(ANY_DIGIT, 2, 2)), ":",
                group(repeat(ANY_DIGIT, 2, 2)), "\\] ",
                LAZY_ANYTHING, END_OF_LINE
        );
        var result = Pattern.compile(regex)
                .matcher(input)
                .results()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Regex parsing failed - missing result."));
        assert result.groupCount() == 6 : "Regex parsing failed - missing capture groups: expected %d, found %d.".formatted(6, result.groupCount());
        String year = result.group(1), month = result.group(2), day = result.group(3);
        String hour = result.group(4), minute = result.group(5), event = result.group(6);
        return Entry.of(year, month, day, hour, minute, event);
    }

    private static int extractID(Entry entry) {
        assert entry.event.endsWith(" begins shift") : "Entry '%s' is not from a shift takeover.".formatted(entry.event);
        var regex = join("Guard #",
                group(min(ANY_DIGIT, 1)),
                " begins shift"
        );
        var id = Pattern.compile(regex)
                .matcher(entry.event)
                .results()
                .findFirst()
                .orElseThrow(() -> new AssertionError("ID extraction failed - missing result."))
                .group(1);
        return Integer.parseInt(id);
    }

    private static Collection<Guard> parseGuards(List<Entry> entries) {
        var guards = new HashMap<Integer, Guard>();

        // Create guards and calculate their durations asleep and awake.
        Guard guard = null;
        for (int i = 0, id = -1; i < entries.size() - 1; i++) {
            var current = entries.get(i);
            var next = entries.get(i + 1);
            if (current.event.endsWith("begins shift")) {
                id = extractID(current);
                guard = guards.computeIfAbsent(id, Guard::new);
            } else if (current.event.startsWith("falls asleep")) {
                assert id != -1 : "Missing guard ID.";
                assert !next.event.startsWith("falls asleep") : "Guard already asleep.";
                guard.asleep(current.dateTime, next.dateTime);
            } else if (current.event.startsWith("wakes up")) {
                assert id != -1 : "Missing guard ID.";
                assert !next.event.startsWith("wakes up") : "Guard already awake.";
            }
        }
        return guards.values();
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        var entries = parseEntries(input);
        var guards = parseGuards(entries);

        // Find the guard with the longest duration asleep.
        var sleepyhead = guards.stream()
                .max(Guard.COMPARE_MINUTES_ASLEEP)
                .orElseThrow(() -> new AssertionError("Missing sleepyhead guard."));
        return sleepyhead.id * sleepyhead.getMostFrequentMinuteAsleep().orElseThrow(() -> new AssertionError("Guard did not fall asleep at least once."));
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        var entries = parseEntries(input);
        var guards = parseGuards(entries);
        var sleepyhead = guards.stream()
                .peek(LOGGER::debug)
                .max(Comparator.comparing(guard -> guard.getMostFrequentMinuteAsleep()
                        .map(guard.midnight::get)
                        .orElse(0)))
                .orElseThrow(() -> new AssertionError("Missing sleepyhead guard."));
        return sleepyhead.id * sleepyhead.getMostFrequentMinuteAsleep().orElseThrow(() -> new AssertionError("Guard did not fall asleep at least once."));
    }

    private static class Guard {
        private static final Comparator<Guard> COMPARE_MINUTES_ASLEEP = Comparator.comparing(Guard::getMinutesAsleep);

        private final HashMap<Integer, Integer> midnight = new HashMap<>();
        private final int id;

        public Guard(int id) {
            this.id = id;
        }

        public void asleep(LocalDateTime start, LocalDateTime end) {
            int from, to;
            switch (start.getHour()) {
                case 23 -> from = 0;
                case 0 -> from = start.getMinute();
                default -> throw new AssertionError("Starting hour before 2300h");
            }
            switch (end.getHour()) {
                case 0 -> to = end.getMinute();
                case 1 -> to = 59;
                default -> throw new AssertionError("Ending hour after 0159h");
            }
            for (int i = from; i < to; i++) {
                midnight.merge(i, 1, Integer::sum);
            }
        }

        public int getMinutesAsleep() {
            return midnight.values().stream()
                    .reduce(Integer::sum)
                    .orElse(0);
        }

        public Optional<Integer> getMostFrequentMinuteAsleep() {
            return midnight.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey);
        }
    }

    private record Entry(LocalDateTime dateTime, String event) implements Comparable<Entry> {
        private static Entry of(String year, String month, String day, String hour, String minute, String event) {
            return new Entry(LocalDateTime.of(Integer.parseInt(year),
                    Integer.parseInt(month),
                    Integer.parseInt(day),
                    Integer.parseInt(hour),
                    Integer.parseInt(minute)),
                    event
            );
        }

        @Override
        public int compareTo(Entry that) {
            return Comparator.comparing(Entry::dateTime).compare(this, that);
        }
    }
}
