package utils;

public record Pair<L, R>(L left, R right) {
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public static <L, R> Pair<L, R> from(Pair<L, R> pair) {
        return new Pair<>(pair.left(), pair.right());
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(left.toString(), right.toString());
    }
}
