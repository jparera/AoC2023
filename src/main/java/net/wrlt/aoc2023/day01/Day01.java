package net.wrlt.aoc2023.day01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day01 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return lines.mapToInt(line -> valueOf(line, i -> digit(line, i))).sum();
            }
        }

        private static int digit(String input, int index) {
            var c = input.charAt(index);
            if (Character.isDigit(c)) {
                return c - '0';
            }
            return -1;
        }
    }

    public static class Part2 {
        private static final String[] NAMES = {
                "zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine" };

        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return lines.mapToInt(line -> valueOf(line, i -> digit(line, i))).sum();
            }
        }

        private static int digit(String input, int index) {
            var c = input.charAt(index);
            if (Character.isDigit(c)) {
                return c - '0';
            }
            for (int i = 1; i < NAMES.length; i++) {
                if (input.startsWith(NAMES[i], index)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static int valueOf(String line, IntUnaryOperator digitMapper) {
        int first = Strings.fwIndex(line, digitMapper)
                .filter(digit -> digit > 0)
                .findFirst()
                .orElseThrow();
        int last = Strings.bwIndex(line, digitMapper)
                .filter(digit -> digit > 0)
                .findFirst()
                .orElseThrow();
        return first * 10 + last;
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
