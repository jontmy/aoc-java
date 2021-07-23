package utils;

@SuppressWarnings("unused")
public class RegexUtils {
    public static final String BEGINNING_OF_LINE = "^";
    public static final String END_OF_LINE = "$";

    public static final String ANY_DIGIT = "\\d";
    public static final String NOT_DIGIT = "\\D";
    public static final String ANY_WHITESPACE = "\\s";
    public static final String NOT_WHITESPACE = "\\S";
    public static final String ANY_ALPHANUMERIC = "\\w";
    public static final String NOT_ALPHA_NUMERIC = "\\W";
    public static final String ANY_CHARACTER = ".";

    public static final String LAZY_ANYTHING = "(.*?)";

    public static String join(String... regexes) {
        return String.join("", regexes);
    }

    public static String or(String either, String or) {
        return "%s|%s".formatted(either, or);
    }

    public static String or(String first, String... options) {
        var sb = new StringBuilder(first);
        for (String option : options) {
            sb.append("|").append(option);
        }
        return sb.toString();
    }

    public static String set(String regex) {
        return "[%s]".formatted(regex);
    }

    public static String set(String... regexes) {
        return "[%s]".formatted(join(regexes));
    }

    public static String group(String regex) {
        return "(%s)".formatted(regex);
    }

    public static String group(String... regexes) {
        return "(%s)".formatted(join(regexes));
    }

    public static String ungroup(String regex) {
        return new StringBuilder(regex)
                .deleteCharAt(regex.lastIndexOf(")"))
                .deleteCharAt(regex.indexOf("("))
                .toString();
    }

    public static String min(String regex, int min) {
        if (min < 0) throw new IllegalArgumentException("Minimum repetition count (%d) is less than 0.".formatted(min));
        return switch (min) {
            case 0 -> "%s*".formatted(regex);
            case 1 -> "%s+".formatted(regex);
            default -> "%s{%d,}".formatted(regex, min);
        };
    }

    public static String max(String regex, int max) {
        if (max < 2) throw new IllegalArgumentException("Maximum repetition count (%d) is less than 2.".formatted(max));
        else return "%s{0,%d}".formatted(regex, max);
    }

    public static String repeat(String regex, int min, int max) {
        if (min < 0) throw new IllegalArgumentException("Minimum repetition count (%d) is less than 0.".formatted(min));
        if (max < 1) throw new IllegalArgumentException("Maximum repetition count (%d) is less than 1.".formatted(max));
        if (max < min)
            throw new IllegalArgumentException("Maximum repetition count (%d) is less than minimum repetition count (%d).".formatted(max, min));
        if (min == 0 && max == 1) return "%s?".formatted(regex);
        else if (min == max) return "%s{%d}".formatted(regex, min);
        else return "%s{%d,%d}".formatted(regex, min, max);
    }

    public static String positiveLookahead(String regex) {
        return "(?=%s)".formatted(regex);
    }

    public static String positiveLookbehind(String regex) {
        return "(?<=%s)".formatted(regex);
    }

    public static String negativeLookahead(String regex) {
        return "(?!%s)".formatted(regex);
    }

    public static String negativeLookbehind(String regex) {
        return "(?<!%s)".formatted(regex);
    }
}
