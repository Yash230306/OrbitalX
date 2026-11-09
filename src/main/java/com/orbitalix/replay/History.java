package com.orbitalix.replay;

import com.orbitalix.util.RingBuffer;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/** Bounded Memento history preserves immutable values in chronological order. */
public final class History<T> implements Iterable<T> {
    private final RingBuffer<T> entries;
    public History(int capacity) { entries = new RingBuffer<>(capacity); }
    public void add(T value) { entries.add(Objects.requireNonNull(value)); }
    public int size() { return entries.size(); }
    public int capacity() { return entries.capacity(); }
    public T get(int index) { return entries.get(index); }
    public void clear() { entries.clear(); }
    public Stream<T> stream() { return entries.stream(); }
    @Override public Iterator<T> iterator() { return entries.iterator(); }
}
