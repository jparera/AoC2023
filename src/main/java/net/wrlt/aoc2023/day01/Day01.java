package net.wrlt.aoc2023.day01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings.Chars;

public class Day01 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return lines.mapToInt(Part1::valueOf).sum();
            }
        }

        private static int valueOf(String line) {
            int first = Chars.first(line, Character::isDigit).orElseThrow();
            int last = Chars.last(line, Character::isDigit).orElseThrow();
            return (first - '0') * 10 + (last - '0');
        }
    }

    public static class Part2 {
        private static final String[] NAMES = {
                "zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine" };

        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return lines.mapToInt(Part2::valueOf).sum();
            }
        }

        private static int valueOf(String line) {
            int first = firstDigit(line);
            int last = lastDigit(line);
            return first * 10 + last;
        }

        private static int firstDigit(String input) {
            for (int i = 0; i < input.length(); i++) {
                var digit = digit(input, i);
                if (digit != -1) {
                    return digit;
                }
            }
            return -1;
        }

        private static int lastDigit(String input) {
            for (int i = input.length() - 1; i >= 0; i--) {
                var digit = digit(input, i);
                if (digit != -1) {
                    return digit;
                }
            }
            return -1;
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

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
