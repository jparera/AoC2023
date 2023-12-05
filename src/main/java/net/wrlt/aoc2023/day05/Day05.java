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
        private List<RangeMap> rangeMaps = new ArrayList<>();

        @Override
        public String toString() {
            return rangeMaps.toString();
        }

        public long map(long value) {
            for (var rangeMap : rangeMaps) {
                var mapped = rangeMap.map(value);
                if (mapped.isPresent()) {
                    return mapped.orElseThrow();
                }
            }
            return value;
        }

        public List<Range> map(List<Range> seeds) {
            var output = new ArrayList<Range>();
            var unmapped = seeds;
            for (var rangeMap : rangeMaps) {
                var result = rangeMap.map(unmapped);
                output.addAll(result.mapped());
                unmapped = result.unmapped();
            }
            output.addAll(unmapped);
            return output;
        }

        public void addRange(long dst, long src, long length) {
            if (length == 0) {
                return;
            }
            var srcRange = new Range(src, src + length);
            var dstRange = new Range(dst, dst + length);
            rangeMaps.add(new RangeMap(srcRange, dstRange));
        }

        private static class RangeMap {
            private final Range src;

            private final Range dst;

            public RangeMap(Range src, Range dst) {
                this.src = src;
                this.dst = dst;
            }

            @Override
            public String toString() {
                return String.format("RangeMap[src=%s, dst=%s]", src, dst);
            }

            public OptionalLong map(long value) {
                if (src.start <= value && value < src.end()) {
                    return OptionalLong.of(dst.start() + (value - src.start()));
                }
                return OptionalLong.empty();
            }

            public Result map(List<Range> ranges) {
                var mapped = new ArrayList<Range>();
                var unmapped = new ArrayList<Range>();
                for (var range : ranges) {
                    var result = map(range);
                    mapped.addAll(result.mapped());
                    unmapped.addAll(result.unmapped());
                }
                return new Result(mapped, unmapped);
            }

            private Result map(Range range) {
                List<Range> mapped = List.of();
                List<Range> unmapped = List.of();
                var intersection = src.intersect(range);
                if (!intersection.isEmpty()) {
                    mapped = List.of(toDst(intersection));
                    unmapped = range.substract(intersection);
                } else {
                    unmapped = List.of(range);
                }
                return new Result(mapped, unmapped);
            }

            private Range toDst(Range range) {
                var start = dst.start() + (range.start() - src.start());
                var end = start + range.length();
                return new Range(start, end);
            }

            record Result(List<Range> mapped, List<Range> unmapped) {
            }
        }
    }

    private record Range(long start, long end) {
        private static final Range EMPTY = new Range(0, 0);

        public long length() {
            return Math.subtractExact(end, start);
        }

        public boolean isEmpty() {
            return length() <= 0;
        }

        public List<Range> substract(Range range) {
            var s = Math.max(this.start, range.start);
            var e = Math.min(this.end, range.end);
            var len = Math.subtractExact(e, s);
            if (len <= 0) {
                return List.of(this);
            }
            if (len == this.length()) {
                return List.of();
            }
            var ranges = new ArrayList<Range>();
            if (Math.subtractExact(s, this.start) > 0) {
                ranges.add(new Range(this.start, s));
            }
            if (Math.subtractExact(this.end, e) > 0) {
                ranges.add(new Range(e, this.end));
            }
            return ranges;
        }

        public Range intersect(Range range) {
            var s = Math.max(start, range.start);
            var e = Math.min(end, range.end);
            return e - s > 0 ? new Range(s, e) : Range.EMPTY;
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
