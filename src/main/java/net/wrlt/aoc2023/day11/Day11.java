package net.wrlt.aoc2023.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Day11 {
    public static class Part1 {
        public static long execute(Path input) throws IOException {
            return Part2.execute(input, 2);
        }
    }

    public static class Part2 {
        public static long execute(Path input, int expansionRate) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);

                var rows = map.length;
                var cols = map[0].length;

                var galaxies = new ArrayList<int[]>();
                var rowSpaces = new int[rows];
                var colSpaces = new int[cols];

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if (map[r][c] == SPACE) {
                            rowSpaces[r]++;
                            colSpaces[c]++;
                        } else {
                            galaxies.add(new int[] { r, c });
                        }
                    }
                }

                for (int i = 0, sum = -1; i < rows; i++) {
                    rowSpaces[i] = rowSpaces[i] == rows ? expansionRate : 1;
                    sum += rowSpaces[i];
                    rowSpaces[i] = sum;
                }

                for (int i = 0, sum = -1; i < cols; i++) {
                    colSpaces[i] = colSpaces[i] == cols ? expansionRate : 1;
                    sum += colSpaces[i];
                    colSpaces[i] = sum;
                }

                for (var galaxy : galaxies) {
                    galaxy[ROW] = rowSpaces[galaxy[ROW]];
                    galaxy[COL] = colSpaces[galaxy[COL]];
                }

                long sum = 0;
                for (int i = 0; i < galaxies.size(); i++) {
                    for (int j = i + 1; j < galaxies.size(); j++) {
                        sum = Math.addExact(sum, manhattan(galaxies.get(i), galaxies.get(j)));
                    }
                }
                return sum;
            }
        }
    }

    private static final int ROW = 0;
    private static final int COL = 1;

    private static final char SPACE = '.';

    private static int manhattan(int[] p1, int[] p2) {
        return Math.abs(p1[ROW] - p2[ROW]) + Math.abs(p1[COL] - p2[COL]);
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
