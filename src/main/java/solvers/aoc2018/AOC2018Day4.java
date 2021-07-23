package solvers.aoc2018;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static utils.DateUtils.*;
import static utils.RegexUtils.*;

public class AOC2018Day4 extends AOCDay<Integer> {
    public AOC2018Day4() throws IOException, URISyntaxException {
        super(4, 2018);
    }

    private static List<Entry> parse(List<String> input) {
        return input.stream()
                .map(AOC2018Day4::parse)
                .sorted()
                .toList();
    }

    private static Entry parse(String input) {
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

    @Override
    protected Integer solvePartOne(List<String> input) {
        parse(input).forEach(LOGGER::debug);
        return 0;
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        return 0;
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
