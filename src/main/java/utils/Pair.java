package utils;

import java.util.function.Function;

public record Pair<L, R>(L left, R right) {
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public static <L, R> Pair<L, R> from(Pair<L, R> pair) {
        return new Pair<>(pair.left(), pair.right());
    }

    public <S, T> Pair<S, T> map(Function<L, S> leftMapper, Function<R, T> rightMapper) {
        var left = leftMapper.apply(this.left());
        var right = rightMapper.apply(this.right());
        return Pair.of(left, right);
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(left.toString(), right.toString());
    }
}
