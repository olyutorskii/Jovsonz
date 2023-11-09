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
 * 既存の{@link java.util.Iterator}および{@link java.lang.Iterable}に対し、
 * 削除のできない変更操作不可なIteratorラッパを提供する。
 *
 * @param <E> コレクション内の要素型
 */
public class UnmodIterator<E> implements Iterator<E> {

    private final Iterator<E> rawIterator;

    /**
     * コンストラクタ。
     *
     * @param iterator ラップ元Iterator
     * @throws NullPointerException 引数がnull
     */
    public UnmodIterator(Iterator<E> iterator) {
        super();
        this.rawIterator = Objects.requireNonNull(iterator);
        return;
    }

    /**
     * 削除操作不可なラップIteratorを生成する。
     *
     * @param <E> コレクション内の要素型
     * @param iterator ラップ元Iterator
     * @return 変更操作不可なIterator
     * @throws NullPointerException 引数がnull
     */
    public static <E> Iterator<E> wrapUnmod(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);
        return new UnmodIterator<>(iterator);
    }

    /**
     * 削除操作不可なラップIterableを生成する。
     *
     * @param <E> コレクション内の要素型
     * @param iterable ラップ元Iterable
     * @return 変更操作不可なIteratorを生成するIterable
     * @throws NullPointerException 引数がnull
     */
    public static <E> Iterable<E> wrapUnmod(Iterable<E> iterable) {
        final Iterable<E> innerArg = Objects.requireNonNull(iterable);
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                Iterator<E> iterator = innerArg.iterator();
                return new UnmodIterator<>(iterator);
            }
        };
    }

    /**
     * Iterableに由来する削除操作不可なラップIteratorを生成する。
     *
     * @param <E> コレクション内の要素型
     * @param iterable Iterable
     * @return 変更操作不可なIterator
     * @throws NullPointerException 引数がnull
     */
    public static <E> Iterator<E> unmodIterator(Iterable<E> iterable) {
        Objects.requireNonNull(iterable);
        Iterator<E> iterator = iterable.iterator();
        return new UnmodIterator<>(iterator);
    }

    /**
     * {@inheritDoc}
     *
     * <p>反復子に次の要素があるか判定する。
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return this.rawIterator.hasNext();
    }

    /**
     * {@inheritDoc}
     *
     * <p>反復子の次の要素を取得する。
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException これ以上要素はない。
     */
    @Override
    public E next() {
        return this.rawIterator.next();
    }

    /**
     * {@inheritDoc}
     *
     * <p>必ず失敗し例外を投げる。
     *
     * @throws UnsupportedOperationException unsupported
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
