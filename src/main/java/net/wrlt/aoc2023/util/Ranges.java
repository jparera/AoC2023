package net.wrlt.aoc2023.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Ranges {
    public static class OfInt {
        public record Range(int start, int end) {
            public Range {
                if (start >= end) {
                    throw new IllegalArgumentException(String.format("Invalid range [%s, %s)", start, end));
                }
            }

            public int length() {
                return Math.subtractExact(end, start);
            }

            public IntStream stream() {
                return IntStream.range(start, end);
            }

            public Range intersect(Range r) {
                if (r == null) {
                    return null;
                }
                var start = Math.max(this.start, r.start);
                var end = Math.min(this.end, r.end);
                var len = Math.subtractExact(end, start);
                if (len <= 0) {
                    return null;
                }
                return new Range(start, end);
            }

            public List<Range> add(Range r) {
                if (r == null) {
                    return List.of(this);
                }
                var start = Math.max(this.start, r.start);
                var end = Math.min(this.end, r.end);
                var len = Math.subtractExact(end, start);
                if (len <= 0) {
                    if (this.end == r.start || r.end == this.start) {
                        start = Math.min(this.start, r.start);
                        end = Math.max(this.end, r.end);
                        return List.of(new Range(start, end));
                    }
                    return List.of(this, r);
                }
                if (len == length() && len == r.length()) {
                    return List.of(this);
                }
                start = Math.min(this.start, r.start);
                end = Math.max(this.end, r.end);
                return List.of(new Range(start, end));
            }

            public List<Range> substract(Range r) {
                if (r == null) {
                    return List.of(this);
                }
                var start = Math.max(this.start, r.start);
                var end = Math.min(this.end, r.end);
                var len = Math.subtractExact(end, start);
                if (len <= 0) {
                    return List.of(this);
                }
                if (len == this.length()) {
                    return List.of();
                }
                var result = new ArrayList<Range>();
                if (Math.subtractExact(start, this.start) > 0) {
                    result.add(new Range(this.start, start));
                }
                if (Math.subtractExact(this.end, end) > 0) {
                    result.add(new Range(end, this.end));
                }
                return result;
            }
        }
    }

    public static class OfLong {
        public record Range(long start, long end) {
            public Range {
                if (start >= end) {
                    throw new IllegalArgumentException(String.format("Invalid range [%s, %s)", start, end));
                }
            }

            public long length() {
                return Math.subtractExact(end, start);
            }

            public LongStream stream() {
                return LongStream.range(start, end);
            }

            public Range intersect(Range r) {
                if (r == null) {
                    return null;
                }
                var start = Math.max(this.start, r.start);
                var end = Math.min(this.end, r.end);
                var len = Math.subtractExact(end, start);
                if (len <= 0) {
                    return null;
                }
                return new Range(start, end);
            }

            public List<Range> add(Range r) {
                if (r == null) {
                    return List.of(this);
                }
                var start = Math.max(this.start, r.start);
                var end = Math.min(this.end, r.end);
                var len = Math.subtractExact(end, start);
                if (len <= 0) {
                    if (this.end == r.start || r.end == this.start) {
                        start = Math.min(this.start, r.start);
                        end = Math.max(this.end, r.end);
                        return List.of(new Range(start, end));
                    }
                    return List.of(this, r);
                }
                if (len == length() && len == r.length()) {
                    return List.of(this);
                }
                start = Math.min(this.start, r.start);
                end = Math.max(this.end, r.end);
                return List.of(new Range(start, end));
            }

            public List<Range> substract(Range r) {
                if (r == null) {
                    return List.of(this);
                }
                var start = Math.max(this.start, r.start);
                var end = Math.min(this.end, r.end);
                var len = Math.subtractExact(end, start);
                if (len <= 0) {
                    return List.of(this);
                }
                if (len == this.length()) {
                    return List.of();
                }
                var result = new ArrayList<Range>();
                if (Math.subtractExact(start, this.start) > 0) {
                    result.add(new Range(this.start, start));
                }
                if (Math.subtractExact(this.end, end) > 0) {
                    result.add(new Range(end, this.end));
                }
                return result;
            }
        }
    }

}
