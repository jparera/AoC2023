package net.wrlt.aoc2023.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GroupsOfLines {
    public static Stream<Stream<String>> stream(Path path) throws IOException {
        var lines = Files.lines(path);
        var it = new ItImpl(lines.sequential().iterator());
        var split = Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED);
        return StreamSupport.stream(split, false).onClose(() -> lines.close());
    }

    private static class ItImpl implements Iterator<Stream<String>> {
        private Iterator<String> lines;

        private String line = "";

        private ItImpl(Iterator<String> lines) {
            this.lines = lines;
            nextGroup();
        }

        @Override
        public boolean hasNext() {
            return !line.isBlank();
        }

        @Override
        public Stream<String> next() {
            if (this.line.isBlank()) {
                throw new NoSuchElementException();
            }
            var group = new ArrayList<String>();
            do {
                group.add(this.line);
                consumeLine();
            } while (!this.line.isBlank());
            nextGroup();
            return group.stream();
        }

        private void nextGroup() {
            while (line.isBlank() && consumeLine()) {
            }
        }

        private boolean consumeLine() {
            if (!lines.hasNext()) {
                this.line = "";
                return false;
            }
            this.line = lines.next();
            return true;
        }
    }

    public static class OfInt {
        public static IntStream reduce(Path path, IntBinaryOperator op) throws IOException {
            return stream(path, op);
        }

        public static IntStream sum(Path path) throws IOException {
            return stream(path, (result, value) -> Math.addExact(result, value));
        }

        public static IntStream count(Path path) throws IOException {
            return stream(path, (result, value) -> Math.incrementExact(result));
        }

        private static IntStream stream(Path path, IntBinaryOperator op) throws IOException {
            var lines = Files.lines(path);
            var it = new ItImpl(lines.sequential().iterator(), op);
            var split = Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED);
            return StreamSupport.intStream(split, false).onClose(() -> lines.close());
        }

        private static class ItImpl implements PrimitiveIterator.OfInt {
            private final Iterator<String> lines;

            private final IntBinaryOperator op;

            private String line = "";

            private ItImpl(Iterator<String> lines, IntBinaryOperator op) {
                this.lines = lines;
                this.op = op;
                nextGroup();
            }

            @Override
            public boolean hasNext() {
                return !line.isBlank();
            }

            @Override
            public int nextInt() {
                if (this.line.isBlank()) {
                    throw new NoSuchElementException();
                }
                int result = 0;
                do {
                    result = op.applyAsInt(result, Integer.parseInt(this.line));
                    consumeLine();
                } while (!this.line.isBlank());
                nextGroup();
                return result;
            }

            private void nextGroup() {
                while (line.isBlank() && consumeLine()) {
                }
            }

            private boolean consumeLine() {
                if (!lines.hasNext()) {
                    this.line = "";
                    return false;
                }
                this.line = lines.next();
                return true;
            }
        }
    }

    public static class OfLong {
        public static LongStream reduce(Path path, LongBinaryOperator op) throws IOException {
            return stream(path, op);
        }

        public static LongStream sum(Path path) throws IOException {
            return stream(path, (result, value) -> Math.addExact(result, value));
        }

        public static LongStream count(Path path) throws IOException {
            return stream(path, (result, value) -> Math.incrementExact(result));
        }

        private static LongStream stream(Path path, LongBinaryOperator op) throws IOException {
            var lines = Files.lines(path);
            var it = new ItImpl(lines.sequential().iterator(), op);
            var split = Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED);
            return StreamSupport.longStream(split, false).onClose(() -> lines.close());
        }

        private static class ItImpl implements PrimitiveIterator.OfLong {
            private final Iterator<String> lines;

            private final LongBinaryOperator op;

            private String line = "";

            private ItImpl(Iterator<String> lines, LongBinaryOperator op) {
                this.lines = lines;
                this.op = op;
                nextGroup();
            }

            @Override
            public boolean hasNext() {
                return !line.isBlank();
            }

            @Override
            public long nextLong() {
                if (this.line.isBlank()) {
                    throw new NoSuchElementException();
                }
                long result = 0;
                do {
                    result = op.applyAsLong(result, Long.parseLong(this.line));
                    consumeLine();
                } while (!this.line.isBlank());
                nextGroup();
                return result;
            }

            private void nextGroup() {
                while (line.isBlank() && consumeLine()) {
                }
            }

            private boolean consumeLine() {
                if (!lines.hasNext()) {
                    this.line = "";
                    return false;
                }
                this.line = lines.next();
                return true;
            }
        }
    }
}