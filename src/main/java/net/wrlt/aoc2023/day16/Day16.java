package net.wrlt.aoc2023.day16;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day16 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var contraption = lines.map(String::toCharArray).toArray(char[][]::new);
                return energized(contraption, 0, 0, Orientation.RW);
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var contraption = lines.map(String::toCharArray).toArray(char[][]::new);
                var n = contraption.length;
                var starts = new ArrayList<int[]>();
                for (int i = 0; i < n; i++) {
                    starts.add(new int[] { 0, i, Orientation.DW.ordinal() });
                    starts.add(new int[] { n - 1, i, Orientation.UW.ordinal() });
                    starts.add(new int[] { i, 0, Orientation.RW.ordinal() });
                    starts.add(new int[] { i, n - 1, Orientation.LW.ordinal() });
                }
                return starts.stream().parallel()
                        .mapToInt(start -> energized(contraption, start))
                        .max().orElseThrow();
            }
        }
    }

    private static int energized(char[][] contraption, int[] start) {
        return energized(contraption, start[0], start[1], Orientation.values()[start[2]]);
    }

    private static int energized(
            char[][] contraption,
            int row,
            int col,
            Orientation o) {
        var n = contraption.length;
        var visited = new boolean[n][n][4];

        beam(contraption, row, col, o, visited);

        var energized = 0;
        for (int r = 0; r < n; r++) {
            next: for (int c = 0; c < n; c++) {
                for (int i = 0; i < Orientation.values().length; i++) {
                    if (visited[r][c][i]) {
                        energized++;
                        continue next;
                    }
                }
            }
        }
        return energized;
    }

    private static void beam(
            char[][] contraption,
            int row,
            int col,
            Orientation o,
            boolean[][][] visited) {
        var n = contraption.length;
        if (row < 0 || row >= n || col < 0 || col >= n) {
            return;
        }
        if (visited[row][col][o.ordinal()]) {
            return;
        }
        visited[row][col][o.ordinal()] = true;

        var nexts = switch (contraption[row][col]) {
            case '/' -> switch (o) {
                case DW -> List.of(Orientation.LW);
                case RW -> List.of(Orientation.UW);
                case UW -> List.of(Orientation.RW);
                case LW -> List.of(Orientation.DW);
            };
            case '\\' -> switch (o) {
                case DW -> List.of(Orientation.RW);
                case RW -> List.of(Orientation.DW);
                case UW -> List.of(Orientation.LW);
                case LW -> List.of(Orientation.UW);
            };
            case '-' -> switch (o) {
                case DW, UW -> List.of(Orientation.RW, Orientation.LW);
                case RW, LW -> List.of(o);

            };
            case '|' -> switch (o) {
                case DW, UW -> List.of(o);
                case RW, LW -> List.of(Orientation.UW, Orientation.DW);
            };
            default -> List.of(o);
        };

        for (var next : nexts) {
            beam(contraption, row + next.rowOffset(), col + next.colOffset(), next, visited);
        }
    }

    private enum Orientation {
        RW(0, 1),
        DW(1, 0),
        UW(-1, 0),
        LW(0, -1);

        private int rowOffset;

        private int colOffset;

        Orientation(int rowOffset, int colOffset) {
            this.rowOffset = rowOffset;
            this.colOffset = colOffset;
        }

        public int rowOffset() {
            return rowOffset;
        }

        public int colOffset() {
            return colOffset;
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
