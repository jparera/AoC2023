package net.wrlt.aoc2023.day03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day03 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            var schematic = parse(input);
            var numbers = new ArrayList<Integer>();
            int rows = schematic.length;
            int columns = schematic[0].length;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    var c = schematic[row][col];
                    if (Character.isDigit(c)) {
                        var symbols = new HashSet<Symbol>();
                        var number = number(schematic, row, col, 0, symbols);
                        if (!symbols.isEmpty()) {
                            numbers.add(number);
                        }
                    }
                }
            }
            return numbers.stream()
                    .mapToInt(Integer::intValue)
                    .reduce(0, Math::addExact);
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            var schematic = parse(input);
            var gears = new HashMap<Symbol, List<Integer>>();
            int rows = schematic.length;
            int columns = schematic[0].length;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    var c = schematic[row][col];
                    if (Character.isDigit(c)) {
                        var symbols = new HashSet<Symbol>();
                        var number = number(schematic, row, col, 0, symbols);
                        if (!symbols.isEmpty()) {
                            for (var symbol : symbols) {
                                if (symbol.type() == GEAR) {
                                    gears.computeIfAbsent(symbol, pos -> new ArrayList<>())
                                            .add(number);
                                }
                            }
                        }
                    }
                }
            }
            return gears.values().stream()
                    .filter(values -> values.size() == 2)
                    .mapToInt(values -> values.stream()
                            .mapToInt(Integer::intValue)
                            .reduce(1, Math::multiplyExact))
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

    private static int number(char[][] schematic, int row, int col, int value, Set<Symbol> symbols) {
        if (checkBounds(schematic, row, col)) {
            return value;
        }
        var c = schematic[row][col];
        if (Character.isDigit(c)) {
            schematic[row][col] = SPACE;
            for (var offset : OFFSETS) {
                int srow = row + offset[0];
                int scol = col + offset[1];
                if (isSymbol(schematic, srow, scol)) {
                    symbols.add(new Symbol(srow, scol, schematic[srow][scol]));
                }
            }
            value = value * 10 + (c - '0');
            return number(schematic, row, col + 1, value, symbols);
        }
        return value;
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
