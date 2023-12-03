package net.wrlt.aoc2023.day03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day03 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            return partNumbers(input).values().stream()
                    .parallel()
                    .flatMap(v -> v.stream())
                    .mapToInt(PartNumber::value)
                    .sum();
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            return partNumbers(input).entrySet().stream()
                    .parallel()
                    .filter(e -> e.getKey().type() == GEAR && e.getValue().size() == 2)
                    .mapToInt(e -> e.getValue().stream().mapToInt(PartNumber::value).reduce(1, Math::multiplyExact))
                    .sum();
        }
    }

    private static final char GEAR = '*';

    private static final char SPACE = '.';

    private record Symbol(int row, int col, char type) {

    }

    private record PartNumber(int value, Symbol symbol) {

    }

    private static Map<Symbol, List<PartNumber>> partNumbers(Path input) throws IOException {
        var schematic = parse(input);
        int rows = schematic.length;
        int columns = schematic[0].length;
        return Stream.iterate(0, Math::incrementExact).limit(rows)
                .parallel()
                .flatMap(row -> {
                    var partNumbers = new ArrayList<PartNumber>();
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
                                partNumbers.add(new PartNumber(number, symbol));
                            }
                        }
                    }
                    return partNumbers.stream();
                }).collect(Collectors.groupingBy(PartNumber::symbol));
    }

    private static Optional<Symbol> adjacentSymbol(char[][] schematic, int currentRow, int start, int end) {
        for (int row = currentRow - 1; row <= currentRow + 1; row++) {
            for (int col = start - 1; col <= end; col++) {
                if (isSymbol(schematic, row, col)) {
                    return Optional.of(new Symbol(row, col, schematic[row][col]));
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
