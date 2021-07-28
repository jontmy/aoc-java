package utils;

import java.util.Objects;
import java.util.function.Function;

public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> that = (Pair<?, ?>) o;
        if (!Objects.equals(this.left, that.left)) return false;
        return Objects.equals(this.right, that.right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(left.toString(), right.toString());
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }
}
