package net.wrlt.aoc2023.day05;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day05 {
    public static class Part1 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var it = lines.iterator();
                var seeds = seeds(it);
                var maps = maps(it);
                long min = Long.MAX_VALUE;
                for (var seed : seeds) {
                    var mapInput = seed;
                    for (var map : maps) {
                        mapInput = map.map(mapInput);
                    }
                    min = Math.min(min, mapInput);
                }
                return min;
            }
        }

        private static long[] seeds(Iterator<String> it) {
            return Strings.numbers(it.next()).mapToLong(Long::parseLong).toArray();
        }
    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var it = lines.iterator();
                var seeds = seeds(it);
                var maps = maps(it);
                var mapInput = seeds;
                for (var map : maps) {
                    mapInput = map.map(mapInput);
                }
                return mapInput.stream().mapToLong(Range::start).min().orElseThrow();
            }
        }

        private static List<Range> seeds(Iterator<String> it) {
            var array = Strings.numbers(it.next()).mapToLong(Long::parseLong).toArray();
            var seeds = new ArrayList<Range>();
            for (int i = 0; i < array.length; i += 2) {
                seeds.add(new Range(array[i], array[i] + array[i + 1]));
            }
            return seeds;
        }
    }

    private static List<Map> maps(Iterator<String> it) {
        var maps = new ArrayList<Map>();
        while (it.hasNext()) {
            var line = it.next();
            if (line.isEmpty()) {
                continue;
            }
            if (line.endsWith(":")) {
                maps.add(new Map());
            } else {
                var range = Strings.numbers(line).mapToLong(Long::parseLong).toArray();
                maps.getLast().addRange(range[0], range[1], range[2]);
            }
        }
        return maps;
    }

    private static class Map {
        private List<RangeMap> ranges = new ArrayList<>();

        public long map(long value) {
            for (var range : ranges) {
                var mapped = range.map(value);
                if (mapped.isPresent()) {
                    return mapped.orElseThrow();
                }
            }
            return value;
        }

        public List<Range> map(List<Range> seeds) {
            var output = new ArrayList<Range>();
            var unmapped = seeds;
            for (var range : ranges) {
                var result = range.map(unmapped);
                output.addAll(result.mapped());
                unmapped = result.unmapped();
            }
            output.addAll(unmapped);
            return output;
        }

        @Override
        public String toString() {
            return ranges.toString();
        }

        public void addRange(long dst, long src, long length) {
            if (length == 0) {
                return;
            }
            ranges.add(new RangeMap(new Range(src, src + length), new Range(dst, dst + length)));
        }

        record RangeMap(Range src, Range dst) {
            public OptionalLong map(long value) {
                if (src.start <= value && value < src.end()) {
                    return OptionalLong.of(dst.start() + (value - src.start()));
                }
                return OptionalLong.empty();
            }

            public Result map(List<Range> values) {
                var mapped = new ArrayList<Range>();
                var unmapped = new ArrayList<Range>();
                for (var value : values) {
                    var result = map(value);
                    mapped.addAll(result.mapped());
                    unmapped.addAll(result.unmapped());
                }
                return new Result(mapped, unmapped);
            }

            private Result map(Range value) {
                var mapped = new ArrayList<Range>();
                var unmapped = new ArrayList<Range>();
                var intersection = src.intersect(value);
                if (!intersection.isEmpty()) {
                    var start = dst.start() + (intersection.start() - src.start());
                    var end = start + intersection.length();
                    mapped.add(new Range(start, end));
                    unmapped.addAll(value.substract(intersection));
                } else {
                    unmapped.add(value);
                }
                return new Result(mapped, unmapped);
            }

            record Result(List<Range> mapped, List<Range> unmapped) {
            }
        }
    }

    private record Range(long start, long end) {
        private static final Range EMPTY = new Range(0, 0);

        public long length() {
            return end - start;
        }

        public boolean isEmpty() {
            return length() == 0;
        }

        public List<Range> substract(Range range) {
            if (range.isEmpty() || range.end <= start || range.start >= end) {
                return List.of(this);
            }
            if (range.start >= start && range.end <= end) {
                var first = new Range(start, range.start);
                var last = new Range(range.end, end);
                var list = new ArrayList<Range>();
                if (!first.isEmpty()) {
                    list.add(first);
                }
                if (!last.isEmpty()) {
                    list.add(last);
                }
                return list;
            }
            Range result;
            if (range.start < start) {
                result = new Range(range.end, end);
            } else {
                result = new Range(start, range.start);
            }
            return result.isEmpty() ? List.of() : List.of(result);
        }

        public Range intersect(Range range) {
            if (isEmpty() || range.isEmpty()) {
                return EMPTY;
            }
            var r1 = this;
            var r2 = range;
            if (r2.start < r1.start) {
                var tmp = r1;
                r1 = r2;
                r2 = tmp;
            }
            if (r2.start >= r1.end) {
                return EMPTY;
            } else {
                return new Range(r2.start, Math.min(r1.end, r2.end));
            }
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
