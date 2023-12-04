package net.wrlt.aoc2023.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Streams {
    public static <T> Stream<IndexedElement<T>> enumerate(Stream<T> src) {
        var ssplit = src.spliterator();
        long size = ssplit.getExactSizeIfKnown();
        var it = Spliterators.iterator(ssplit);
        Spliterator<IndexedElement<T>> split;
        if (size > 0) {
            split = Spliterators.spliterator(
                    new IndexedElementIterator<>(it),
                    size,
                    ssplit.characteristics());
        } else {
            split = Spliterators.spliteratorUnknownSize(
                    new IndexedElementIterator<>(it),
                    ssplit.characteristics());
        }
        return StreamSupport.stream(split, false).onClose(src::close);
    }

    private static class IndexedElementIterator<T> implements Iterator<IndexedElement<T>> {
        private final Iterator<T> it;

        private int index;

        public IndexedElementIterator(Iterator<T> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public IndexedElement<T> next() {
            return new IndexedElement<T>(index++, it.next());
        }
    }

    public record IndexedElement<T>(int index, T element) {

    }
}
