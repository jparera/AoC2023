package net.wrlt.aoc2023.day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class Day17 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);
                return minLoss(map, false);
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);
                return minLoss(map, true);
            }
        }
    }

    static int minLoss(char[][] map, boolean part2) {
        var rows = map.length;
        var cols = map[0].length;

        var distances = new Integer[rows][cols][11][4];

        var heap = new PriorityQueue<int[]>((l, r) -> Integer.compare(l[0], r[0]));
        heap.offer(new int[] { 0, 0, 0, 0, Orientation.R.ordinal() });

        while (!heap.isEmpty()) {
            var current = heap.poll();
            var d = current[0];
            var r = current[1];
            var c = current[2];
            var count = current[3];
            var o = Orientation.values()[current[4]];

            var nexts = part2 ? o.nextsPart2(count) : o.nextsPart1(count);
            for (var next : nexts) {
                var nr = r + next.rowOffset;
                var nc = c + next.colOffset;
                var ncount = o.isSameDirection(next) ? count + 1 : 1;
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                    continue;
                }
                var cd = distances[nr][nc][ncount][next.ordinal()];
                var nd = (map[nr][nc] - '0') + d;
                if (cd == null || nd < cd) {
                    distances[nr][nc][ncount][next.ordinal()] = nd;
                    heap.offer(new int[] { nd, nr, nc, ncount, next.ordinal() });
                }
            }
        }

        int min = Integer.MAX_VALUE;
        for (int i = part2 ? 4 : 0; i < 11; i++) {
            for (int j = 0; j < 4; j++) {
                var d = distances[rows - 1][cols - 1][i][j];
                if (d != null) {
                    min = Math.min(min, d);
                }
            }
        }
        return min;
    }

    private enum Orientation {
        U(-1, 0),
        D(1, 0),
        R(0, 1),
        L(0, -1);

        private static final List<Orientation> HORIZONTAL = List.of(L, R);
        private static final List<Orientation> VERTICAL = List.of(U, D);

        private static final List<Orientation> UU = List.of(U);
        private static final List<Orientation> DD = List.of(D);
        private static final List<Orientation> RR = List.of(R);
        private static final List<Orientation> LL = List.of(L);

        private static final List<Orientation> ULR = List.of(U, L, R);
        private static final List<Orientation> DLR = List.of(D, L, R);
        private static final List<Orientation> RUD = List.of(R, U, D);
        private static final List<Orientation> LUD = List.of(L, U, D);

        int rowOffset;

        int colOffset;

        Orientation(int rowOffset, int colOffset) {
            this.rowOffset = rowOffset;
            this.colOffset = colOffset;
        }

        public boolean isSameDirection(Orientation o) {
            if (this == o) {
                return true;
            }
            return this.rowOffset == o.rowOffset || this.colOffset == o.colOffset;
        }

        public List<Orientation> nextsPart1(int count) {
            if (count == 3) {
                return switch (this) {
                    case U, D -> HORIZONTAL;
                    case L, R -> VERTICAL;
                };
            } else {
                return switch (this) {
                    case U -> ULR;
                    case D -> DLR;
                    case R -> RUD;
                    case L -> LUD;
                };
            }
        }

        public List<Orientation> nextsPart2(int count) {
            if (count == 10) {
                return switch (this) {
                    case U, D -> HORIZONTAL;
                    case L, R -> VERTICAL;
                };
            } else if (count < 4) {
                return switch (this) {
                    case U -> UU;
                    case D -> DD;
                    case R -> RR;
                    case L -> LL;
                };
            } else {
                return switch (this) {
                    case U -> ULR;
                    case D -> DLR;
                    case R -> RUD;
                    case L -> LUD;
                };
            }
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
