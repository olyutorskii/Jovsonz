/*
 * unmodifiable iterator wrap
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Make imuutable wrapper to {@link java.util.Iterator} and {@link java.lang.Iterable}.
 *
 * @param <E> generics type of collection
 */
public class UnmodIterator<E> implements Iterator<E> {

    private final Iterator<E> rawIterator;


    /**
     * Constructor.
     *
     * @param iterator Iterator
     * @throws NullPointerException argument is null
     */
    public UnmodIterator(Iterator<E> iterator) {
        super();
        this.rawIterator = Objects.requireNonNull(iterator);
        return;
    }


    /**
     * Wrapping {@link java.util.Iterator} to immutable Iterator.
     *
     * @param <E> generics type of collection
     * @param iterator Iterator
     * @return immutable Iterator
     * @throws NullPointerException argument is null
     */
    public static <E> Iterator<E> wrapUnmod(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);
        return new UnmodIterator<>(iterator);
    }

    /**
     * Wrapping {@link java.lang.Iterable} to immutable Iterable.
     *
     * @param <E> generics type of collection
     * @param iterable Iterable
     * @return immutable Iterable
     * @throws NullPointerException argument is null
     */
    public static <E> Iterable<E> wrapUnmod(Iterable<E> iterable) {
        Objects.requireNonNull(iterable);
        Iterator<E> iterator = iterable.iterator();
        return () -> {
            return new UnmodIterator<>(iterator);
        };
    }

    /**
     * Make immutable {@link java.util.Iterator} from {@link java.lang.Iterable}.
     *
     * @param <E> generics type of collection
     * @param iterable Iterable
     * @return immutable Iterator
     * @throws NullPointerException argument is null
     */
    public static <E> Iterator<E> unmodIterator(Iterable<E> iterable) {
        Objects.requireNonNull(iterable);
        Iterator<E> iterator = iterable.iterator();
        return new UnmodIterator<>(iterator);
    }


    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return this.rawIterator.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public E next() {
        return this.rawIterator.next();
    }

    /**
     * Attempts to delete, and fails.
     *
     * <p>It will always fail.
     *
     * @throws UnsupportedOperationException unsupported
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
