package com.anamika.app.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Iterator;
import java.util.Set;

/**
 * Circular Set of Generics
 *
 * @param <T>
 */
public class CircularSet<T> implements Iterator<T> {
    private final Iterator<T> elements;

    public CircularSet(final Set<T> elements) {
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        builder.addAll(elements);
        this.elements = Iterables.cycle(builder.build()).iterator();
    }

    @Override
    public boolean hasNext() {
        return this.elements.hasNext();
    }

    @Override
    public T next() {
        final T element = this.elements.next();
        return element;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
