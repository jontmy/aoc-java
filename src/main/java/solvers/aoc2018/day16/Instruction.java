package solvers.aoc2018.day16;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static utils.RegexUtils.ANY_DIGIT;
import static utils.RegexUtils.min;

record Instruction(int opcode, int i1, int i2, int o) {
    private static final Pattern REGEX_NUMBERS = Pattern.compile(min(ANY_DIGIT, 1));

    protected static Instruction parse(String line) {
        var tmp = REGEX_NUMBERS.matcher(line)
                .results()
                .map(MatchResult::group)
                .mapToInt(Integer::parseInt)
                .toArray();
        assert tmp.length == 4;
        return new Instruction(tmp[0], tmp[1], tmp[2], tmp[3]);
    }

    @Override
    public String toString() {
        return "(%d, %d, %d, %d)".formatted(opcode, i1, i2, o);
    }
}
