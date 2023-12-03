package net.wrlt.aoc2023.day03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Day03 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            return partNumbers(input).values().stream()
                    .flatMap(v -> v.stream())
                    .mapToInt(Integer::intValue)
                    .sum();
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            return partNumbers(input).entrySet().stream()
                    .filter(e -> e.getKey().type() == GEAR && e.getValue().size() == 2)
                    .mapToInt(e -> e.getValue().stream().mapToInt(Integer::intValue).reduce(1, Math::multiplyExact))
                    .sum();
        }
    }

    private static final char GEAR = '*';

    private static final char SPACE = '.';

    private static final int[][] OFFSETS = {
            { -1, 0 }, // TOP
            { -1, -1 }, // TOP - LEFT
            { -1, 1 }, // TOP - RIGHT
            { 0, -1 }, // LEFT
            { 0, 1 }, // RIGHT
            { 1, 0 }, // BOTTOM
            { 1, -1 }, // BOTTOM - LEFT
            { 1, 1 }, // BOTTOM - RIGHT
    };

    record Symbol(int row, int col, char type) {

    }

    private static Map<Symbol, List<Integer>> partNumbers(Path input) throws IOException {
        var schematic = parse(input);
        var numbers = new HashMap<Symbol, List<Integer>>();
        int rows = schematic.length;
        int columns = schematic[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (Character.isDigit(schematic[row][col])) {
                    int number = 0;
                    var start = col;
                    while (col < columns && Character.isDigit(schematic[row][col])) {
                        number = number * 10 + (schematic[row][col] - '0');
                        col++;
                    }
                    var symbol = adjacentSymbol(schematic, row, start, col).orElse(null);
                    if (symbol != null) {
                        numbers.computeIfAbsent(symbol, k -> new ArrayList<>()).add(number);
                    }
                }
            }
        }
        return numbers;
    }

    private static Optional<Symbol> adjacentSymbol(char[][] schematic, int row, int start, int end) {
        for (var offset : OFFSETS) {
            for (int col = start; col < end; col++) {
                var srow = row + offset[0];
                var scol = col + offset[1];
                if (isSymbol(schematic, srow, scol)) {
                    return Optional.of(new Symbol(srow, scol, schematic[srow][scol]));
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isSymbol(char[][] schematic, int row, int col) {
        if (checkBounds(schematic, row, col)) {
            return false;
        }
        var c = schematic[row][col];
        return !Character.isDigit(c) && c != SPACE;
    }

    private static boolean checkBounds(char[][] schematic, int row, int col) {
        return row < 0 || row >= schematic.length || col < 0 || col >= schematic[0].length;
    }

    private static char[][] parse(Path input) throws IOException {
        try (var lines = Files.lines(input)) {
            return lines.map(String::toCharArray).toArray(char[][]::new);
        }
    }
}
