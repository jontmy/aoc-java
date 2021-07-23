package utils;

public record Pair<L, R>(L left, R right) {
    public static <T, U> Pair<T, U> of(T left, U right) {
        return new Pair<>(left, right);
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(left.toString(), right.toString());
    }
}
