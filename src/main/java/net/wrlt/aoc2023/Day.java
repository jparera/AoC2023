package net.wrlt.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Day {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return 0;
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                return 0;
            }
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
