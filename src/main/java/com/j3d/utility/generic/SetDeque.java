package com.j3d.utility.generic;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Set;

/**
 * A hybrid data structure that combines the behaviour of a {@link Set} and a {@link Deque}.
 * <p>
 * This class extends {@link ArrayDeque} to provide a double-ended queue, but overrides
 * its insertion methods to enforce uniqueness of elements, similar to a {@link Set}.
 * This is useful for scenarios where you need both ordered, queue-like access (LIFO/FIFO)
 * and the guarantee that no duplicate elements exist in the collection.
 *
 * @param <T> the type of elements held in this collection
 * @author Lehlogonolo Poole
 */
public class SetDeque<T> extends ArrayDeque<T> {

    /**
     * Constructs an empty SetDeque.
     */
    public SetDeque() {
        super();
    }

    /**
     * Constructs a SetDeque containing the elements of the specified collection,
     * with duplicates removed.
     *
     * @param c the collection whose elements are to be placed into this deque
     */
    public SetDeque(Collection<? extends T> c) {
        super();
        addAll(c);
    }

    /**
     * Adds the specified element to the end of this deque, only if it is not
     * already present in the deque.
     *
     * @param t the element to add
     * @return {@code true} if the element was added, {@code false} if the deque
     *         already contained the element
     */
    @Override
    public boolean add(T t) {
        if (contains(t)) {
            return false;
        }
        return super.add(t);
    }

    /**
     * Adds all the elements in the specified collection to the end of this deque,
     * ignoring any duplicates.
     *
     * @param c collection containing elements to be added to this deque
     * @return {@code true} if this deque changed as a result of the call
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean dequeChanged = false;
        for (T t : c) {
            // Use the overridden add() method to ensure uniqueness
            if (add(t)) {
                dequeChanged = true;
            }
        }
        return dequeChanged;
    }

    /**
     * Inserts the specified element at the front of this deque.
     * <p>
     * If the element already exists in the deque, it is first removed from its
     * current position and then inserted at the front, effectively repositioning it.
     *
     * @param t the element to add
     */
    @Override
    public void addFirst(T t) {
        // Ensure uniqueness by removing the element if it already exists
        remove(t);
        super.addFirst(t);
    }

    /**
     * Inserts the specified element at the end of this deque.
     * <p>
     * If the element already exists in the deque, it is first removed from its
     * current position and then inserted at the end, effectively repositioning it.
     *
     * @param t the element to add
     */
    @Override
    public void addLast(T t) {
        // Ensure uniqueness by removing the element if it already exists
        remove(t);
        super.addLast(t);
    }

    /**
     * Creates a shallow copy of this {@code SetDeque}.
     *
     * @return a new {@code SetDeque} instance containing the same elements in the same order
     */
    public SetDeque<T> copy() {
        return new SetDeque<>(this);
    }
}