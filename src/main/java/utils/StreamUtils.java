package utils;

import java.util.stream.IntStream;

public final class StreamUtils {
    /**
     * Returns a new sequential ordered {@link IntStream} between 2 bounds by a decremental step of 1.
     *
     * @param startInclusive the (inclusive) lower bound
     * @param endExclusive   the (exclusive) upper bound
     * @return a new sequential ordered {@link IntStream} between 2 bounds by a decremental step of 1
     * @see <a href="stackoverflow.com/questions/24010109/java-8-stream-reverse-order/24011264#24011264">source - Stack Overflow</a>
     */
    public static IntStream reversedRange(int startInclusive, int endExclusive) {
        return IntStream.range(startInclusive, endExclusive)
                .map(i -> endExclusive - i + startInclusive - 1);
    }

    /**
     * Returns a new sequential ordered {@link IntStream} between 2 bounds by a decremental step of 1.
     *
     * @param startInclusive the (inclusive) lower bound
     * @param endInclusive   the (inclusive) upper bound
     * @return a new sequential ordered {@link IntStream} between 2 bounds by a decremental step of 1
     * @see <a href="stackoverflow.com/questions/24010109/java-8-stream-reverse-order/24011264#24011264">source - Stack Overflow</a>
     */
    public static IntStream reversedRangeClosed(int startInclusive, int endInclusive) {
        return IntStream.rangeClosed(startInclusive, endInclusive)
                .map(i -> endInclusive - i + startInclusive);
    }
}
