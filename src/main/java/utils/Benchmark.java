package utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Benchmark<T, R> {
    private final T input;
    private final Function<T, R> function;
    private int runs = 0;
    private long min = 0, max = 0, sum = 0; // durations in μs

    public Benchmark(T input, Function<T, R> function) {
        this.input = input;
        this.function = function;
    }

    public static <T, R> Benchmark<T, R> of(T input, Function<T, R> function) {
        return new Benchmark<>(input, function);
    }

    public Pair<R, Double> run() {
        var start = System.nanoTime();
        R result = function.apply(input);
        var end = System.nanoTime();
        var duration = TimeUnit.NANOSECONDS.toMicros(end - start);
        runs++;
        sum += duration;
        min = min(min, duration);
        max = max(max, duration);
        return Pair.of(result, (double) sum / runs / 1000);
    }

    public Pair<R, Double> run(int runs) {
        R result = run().left();
        for (int i = 1; i < runs; i++) {
            var intermediate = run().left();
            assert intermediate.equals(result) : "Results differ between runs: '%s' vs '%s' on run #%d".formatted(intermediate, result, this.runs);
        }
        return Pair.of(result, (double) sum / runs / 1000);
    }
}
