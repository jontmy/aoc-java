package utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetUtils {

    /**
     * Returns a new mutable set of the union of 2 sets.
     *
     * @param of  set A
     * @param and set B
     * @return A u B
     */
    public static <T> Set<T> union(Collection<T> of, Collection<T> and) {
        var set_union = new HashSet<>(of);
        set_union.addAll(and);
        return set_union;
    }

    /**
     * Returns a new mutable set of the intersection of 2 sets.
     *
     * @param of  set A
     * @param and set B
     * @return a new set of A n B
     */
    public static <T> Set<T> intersection(Collection<T> of, Collection<T> and) {
        var set_intersection = new HashSet<>(of);
        set_intersection.retainAll(and);
        return set_intersection;
    }

    /**
     * Returns a new mutable set of the intersection of all given sets.
     *
     * @throws IllegalArgumentException if fewer than 3 sets are passed as arguments
     */
    @SafeVarargs
    public static <T> Set<T> intersection(Collection<T> of, Collection<T> and, Collection<T>... more) {
        var sets_intersection = intersection(of, and);
        for (Collection<T> collection : more) {
            sets_intersection.retainAll(collection);
        }
        return sets_intersection;
    }

    /**
     * Mutates set A by intersecting it with set B and returns set A.
     *
     * @param mutable_set set A
     * @param with        set B
     * @return the mutated set of A after A n B
     */
    public static <T> Set<T> intersect(Set<T> mutable_set, Collection<T> with) {
        mutable_set.retainAll(with);
        return mutable_set;
    }

    /**
     * Returns <code>true</code> if two sets intersect, otherwise <code>false</code>.
     *
     * @param s1  the first set
     * @param s2  the second set
     * @param <T> the type of both sets
     * @return <code>true</code> if the two sets intersect, otherwise <code>false</code>.
     */
    public static <T> boolean intersects(Collection<T> s1, Collection<T> s2) {
        return intersection(s1, s2).size() != 0;
    }

    /**
     * Returns a new mutable set of the difference of 2 collections.
     *
     * @param of  set A
     * @param and set B
     * @param <T> the type of the elements in both sets
     * @return A - B
     */
    public static <T> Set<T> difference(Collection<T> of, Collection<T> and) {
        var set_difference = new HashSet<>(of);
        set_difference.removeAll(and);
        return set_difference;
    }

    /**
     * Returns a new mutable set of the addition of 2 sets.
     *
     * @param of  set A
     * @param and set B
     * @return A + B
     */
    public static <T, C extends Collection<T>> Set<T> addition(Collection<T> of, Collection<T> and) {
        var set_addition = new HashSet<>(of);
        set_addition.addAll(and);
        return set_addition;
    }
}
