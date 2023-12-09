package net.wrlt.aoc2023.day09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day09 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return lines
                        .map(Strings::integers)
                        .map(numbers -> numbers.mapToInt(Integer::parseInt).toArray())
                        .mapToInt(Part1::predictNext)
                        .sum();
            }
        }

        private static int predictNext(int[] numbers) {
            var len = numbers.length;
            int count = 0;
            while (count < len) {
                count = 0;
                for (int i = 1; i < len; i++) {
                    numbers[i - 1] = numbers[i] - numbers[i - 1];
                    if (numbers[i - 1] == 0) {
                        count++;
                    }
                }
                len--;
            }
            int sum = 0;
            for (int i = len; i < numbers.length; i++) {
                sum += numbers[i];
            }
            return sum;
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return lines
                        .map(Strings::integers)
                        .map(numbers -> numbers.mapToInt(Integer::parseInt).toArray())
                        .mapToInt(Part2::predictNext)
                        .sum();
            }
        }

        private static int predictNext(int[] numbers) {
            var start = 0;
            int count = 0;
            while (count < (numbers.length - start)) {
                count = 0;
                for (int i = numbers.length - 1; i > start; i--) {
                    numbers[i] = numbers[i] - numbers[i - 1];
                    if (numbers[i] == 0) {
                        count++;
                    }
                }
                start++;
            }
            int sum = 0;
            for (int i = start - 1; i >= 0; i--) {
                sum = numbers[i] - sum;
            }
            return sum;
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
