package utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Objects;

import static utils.RegexUtils.*;

@SuppressWarnings("unused")
public final class DateUtils {
    public static final String REGEX_D = group(set("0-3"), "?", ANY_DIGIT);
    public static final String REGEX_DD = group(set("0-3"), ANY_DIGIT);
    public static final String REGEX_MM = group(or("0[1-9]", "1[0-2]"));
    public static final String REGEX_MMM = group(or("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    ));
    public static final String REGEX_YY = group(repeat(ANY_DIGIT, 2, 2));
    public static final String REGEX_YYYY = group(repeat(ANY_DIGIT, 4, 4));
    public static final String REGEX_D_MMM_YYYY = String.join(" ", REGEX_D, REGEX_MMM, REGEX_YYYY);
    public static final String REGEX_DD_MMM_YYYY = String.join(" ", REGEX_DD, REGEX_MMM, REGEX_YYYY);

    public static String format(TemporalAccessor temporal, String format) {
        return DateTimeFormatter.ofPattern(format).format(temporal);
    }

    public static String formatDDMMYYY(LocalDate date) {
        return DateTimeFormatter.ofPattern("dd MMM uuuu").format(date);
    }

    public static String formatMMYYYY(YearMonth temporal) {
        return DateTimeFormatter.ofPattern("MMMM uuuu").format(temporal);
    }

    /**
     * Helper function that parses 2 date strings in the formats DDMM and YYYY, concatenating them.
     *
     * @param ddmmm <code>dd</code>, the 2 digit day of the month, and <code>mmm</code>, the 3 letter abbreviation of the month
     * @param yyyy  the 4 digit year
     * @return the <code>LocalDate</code> instance corresponding to the date given
     */
    public static LocalDate parseDate(String ddmmm, String yyyy) {
        return LocalDate.parse(
                ddmmm.trim(),
                new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("dd MMM")
                        .parseDefaulting(ChronoField.YEAR, Integer.parseInt(yyyy))
                        .toFormatter(Locale.ENGLISH));
    }

    public static LocalDate parseDate(String dd, String mm, String yyyy) {
        Objects.requireNonNull(dd);
        Objects.requireNonNull(mm);
        Objects.requireNonNull(yyyy);
        return LocalDate.of(Integer.parseInt(yyyy), Integer.parseInt(mm), Integer.parseInt(dd));
    }

    public static YearMonth parseYearMonth(String mmm, String yyyy) {
        return YearMonth.parse(mmm + yyyy,
                new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("MMMuuuu")
                        .parseCaseInsensitive()
                        .toFormatter(Locale.ENGLISH)
        );
    }
}
