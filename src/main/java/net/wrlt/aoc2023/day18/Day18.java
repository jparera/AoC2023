package net.wrlt.aoc2023.day18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Day18 {
    public static class Part1 {
        public static long execute(Path input) throws IOException {
            var instructions = parse(input, Day18::part1);
            return volume(instructions);
        }
    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            var instructions = parse(input, Day18::part2);
            return volume(instructions);
        }
    }

    public static long volume(List<Instruction> instructions) {
        var previousRow = 0L;
        var previousCol = 0L;
        var perimeter = 0L;
        var rowXcol = 0L;
        var colXrow = 0L;
        for (var instruction : instructions) {
            perimeter += instruction.meters();
            var offset = instruction.offset();
            var row = previousRow + (offset[0] * instruction.meters());
            var col = previousCol + (offset[1] * instruction.meters());
            rowXcol += previousRow * col;
            colXrow += previousCol * row;
            previousRow = row;
            previousCol = col;
        }
        return ((perimeter + (Math.abs(rowXcol - colXrow))) / 2) + 1;
    }

    private static final Pattern INSTRUCTION = Pattern.compile("([UDLR]) (\\d+) \\(#([0-9A-Fa-f]+)\\)");

    private static final int[] U = { -1, 0 };
    private static final int[] D = { 1, 0 };
    private static final int[] L = { 0, -1 };
    private static final int[] R = { 0, 1 };

    static List<Instruction> parse(Path input, Function<String, Instruction> mapper) throws IOException {
        try (var lines = Files.lines(input)) {
            return lines.map(mapper).toList();
        }
    }

    static Instruction part1(String line) {
        var matcher = INSTRUCTION.matcher(line);
        if (matcher.matches()) {
            return new Instruction(
                    switch (matcher.group(1).charAt(0)) {
                        case 'U' -> U;
                        case 'D' -> D;
                        case 'L' -> L;
                        case 'R' -> R;
                        default -> throw new IllegalArgumentException();
                    },
                    Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    static Instruction part2(String line) {
        var matcher = INSTRUCTION.matcher(line);
        if (matcher.matches()) {
            return new Instruction(
                    switch (matcher.group(3).charAt(5)) {
                        case '0' -> R;
                        case '1' -> D;
                        case '2' -> L;
                        case '3' -> U;
                        default -> throw new IllegalArgumentException();
                    },
                    Integer.parseInt(matcher.group(3).substring(0, 5), 16));
        }
        return null;
    }

    record Instruction(int[] offset, int meters) {

    }
}
