package net.wrlt.aoc2023.day05;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Ranges.OfLong.Range;
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
                    for (var map : maps) {
                        seed = map.apply(seed);
                    }
                    min = Math.min(min, seed);
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
                for (var map : maps) {
                    seeds = seeds.stream().flatMap(s -> map.apply(s).stream()).toList();
                }
                return seeds.stream().mapToLong(Range::start).min().orElseThrow();
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
        private List<Rule> rules = new ArrayList<>();

        @Override
        public String toString() {
            return rules.toString();
        }

        public long apply(long value) {
            for (var rule : rules) {
                var mapped = rule.map(value);
                if (mapped.isPresent()) {
                    return mapped.orElseThrow();
                }
            }
            return value;
        }

        public List<Range> apply(Range seed) {
            var output = new ArrayList<Range>();
            var inputs = new ArrayDeque<Range>();
            inputs.add(seed);
            for (var rule : rules) {
                int size = inputs.size();
                for (int i = 0; i < size; i++) {
                    var input = inputs.poll();
                    var result = rule.map(input);
                    if (result.mapped != null) {
                        output.add(result.mapped());
                    }
                    inputs.addAll(result.unmapped());
                }
                if (inputs.isEmpty()) {
                    break;
                }
            }
            output.addAll(inputs);
            return output;
        }

        public void addRange(long dst, long src, long length) {
            if (length == 0) {
                return;
            }
            var srcRange = new Range(src, src + length);
            var delta = Math.subtractExact(dst, src);
            rules.add(new Rule(srcRange, delta));
        }

        private static class Rule {
            private final Range src;

            private final long delta;

            public Rule(Range src, long delta) {
                this.src = src;
                this.delta = delta;
            }

            public OptionalLong map(long value) {
                if (src.start() <= value && value < src.end()) {
                    return OptionalLong.of(value + delta);
                }
                return OptionalLong.empty();
            }

            private Result map(Range range) {
                Range mapped;
                List<Range> unmapped;
                var i = src.intersect(range);
                if (i != null) {
                    mapped = new Range(i.start() + delta, i.end() + delta);
                    unmapped = range.substract(i);
                } else {
                    mapped = null;
                    unmapped = List.of(range);
                }
                return new Result(mapped, unmapped);
            }

            public record Result(Range mapped, List<Range> unmapped) {
            }
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
