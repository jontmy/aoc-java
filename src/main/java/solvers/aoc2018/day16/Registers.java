package solvers.aoc2018.day16;

import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static utils.RegexUtils.ANY_DIGIT;
import static utils.RegexUtils.min;

final class Registers {
    private static final Pattern REGEX_NUMBERS = Pattern.compile(min(ANY_DIGIT, 1));
    private final int[] internals;

    private Registers() {
        this.internals = new int[4];
    }

    private Registers(int[] internals) {
        this.internals = internals;
    }

    protected static Registers blank() {
        return new Registers();
    }

    protected static Registers of(int a, int b, int c, int d) {
        return new Registers(new int[]{a, b, c, d});
    }

    protected static Registers from(Registers registers) {
        return new Registers(Arrays.copyOf(registers.internals(), 4));
    }

    protected static Registers parse(String line) {
        var internals = REGEX_NUMBERS.matcher(line)
                .results()
                .map(MatchResult::group)
                .mapToInt(Integer::parseInt)
                .toArray();
        assert internals.length == 4;
        return new Registers(internals);
    }

    protected int dereference(int pointer) {
        return this.internals[pointer];
    }

    protected void write(int pointer, int value) {
        this.internals[pointer] = value;
    }

    protected int[] internals() {
        return this.internals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registers registers = (Registers) o;
        return Arrays.equals(internals, registers.internals);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(internals);
    }

    @Override
    public String toString() {
        return Arrays.toString(internals);
    }
}
