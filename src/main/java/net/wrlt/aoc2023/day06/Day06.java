package net.wrlt.aoc2023.day06;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day06 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var races = races(lines);
                int result = 1;
                for (var race : races) {
                    result *= solutions(race);
                }
                return result;
            }
        }

        private static Race[] races(Stream<String> lines) {
            var it = lines.iterator();
            var times = Strings.numbers(it.next()).mapToInt(Integer::parseInt).toArray();
            var distances = Strings.numbers(it.next()).mapToInt(Integer::parseInt).toArray();
            var n = times.length;
            var races = new Race[n];
            for (int i = 0; i < n; i++) {
                races[i] = new Race(times[i], distances[i]);
            }
            return races;
        }
    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var races = races(lines);
                int result = 1;
                for (var race : races) {
                    result *= solutions(race);
                }
                return result;
            }
        }

        private static List<Race> races(Stream<String> lines) {
            var it = lines.iterator();
            var time = Strings.numbers(it.next()).collect(Collectors.joining());
            var distance = Strings.numbers(it.next()).collect(Collectors.joining());
            return List.of(new Race(Integer.parseInt(time), Long.parseLong(distance)));
        }
    }

    private static int solutions(Race race) {
        var record = race.distanceRecord();
        double half_duration = race.duration() / (double) 2;
        double half_duration_squared = half_duration * half_duration;
        double sqrt = Math.sqrt(half_duration_squared - record);
        long t1 = (long) Math.ceil(half_duration - sqrt);
        long t2 = (long) Math.floor(half_duration + sqrt);
        while (t1 * (race.duration() - t1) <= record) {
            t1++;
        }
        while (t2 * (race.duration() - t2) <= record) {
            t2--;
        }
        return (int) (t2 - t1 + 1);
    }

    private record Race(int duration, long distanceRecord) {

    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
