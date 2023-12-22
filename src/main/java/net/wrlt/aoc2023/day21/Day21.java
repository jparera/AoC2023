package net.wrlt.aoc2023.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day21 {
    public static class Part1 {
        private static int MAX_TILES = 0;

        public static long execute(Path input, int steps) throws IOException {
            var map = parse(input);
            var start = start(map);
            var distances = distances(map, start, MAX_TILES);
            var stepsParity = steps % 2;
            return distances.values().stream()
                    .filter(v -> v % 2 == stepsParity && v <= steps)
                    .count();
        }
    }

    public static class Part2 {
        public static long execute(Path input, int steps, int maxTiles) throws IOException {
            var map = parse(input);
            var start = start(map);
            var distances = distances(map, start, maxTiles);

            var n = map.length;
            var stepsParity = steps & 1;

            var count = 0L;

            var memoCorners = new HashMap<Integer, Long>();
            var memoEdges = new HashMap<Integer, Long>();
            for (var e : distances.entrySet()) {
                var plot = e.getKey();
                int d = e.getValue();
                if ((d & 1) == stepsParity && steps >= d) {
                    count += 1;
                }
                var tile = plot.tile();
                var tr = Math.abs(tile.row());
                var tc = Math.abs(tile.col());
                if (tr == maxTiles && tc == maxTiles) {
                    // Corner tile.
                    count += predictFromCorner(d, steps, n, memoCorners);
                } else if (tr == maxTiles || tc == maxTiles) {
                    // Edge tile.
                    count += predictFromEdge(d, steps, n, memoEdges);
                }
            }
            return count;
        }

        private static long predictFromCorner(int d, int steps, int n, Map<Integer, Long> memo) {
            var cached = memo.get(d);
            if (cached != null) {
                return cached;
            }
            var stepsParity = steps & 1;
            var max = (steps - d) / n;
            var count = 0L;
            for (int i = 1; i < max + 1; i++) {
                int nd = d + n * i;
                if (nd <= steps && (nd & 1) == stepsParity) {
                    count += (i + 1);
                }
            }
            memo.put(d, count);
            return count;
        }

        private static long predictFromEdge(int d, int steps, int n, Map<Integer, Long> memo) {
            var cached = memo.get(d);
            if (cached != null) {
                return cached;
            }
            var stepsParity = steps & 1;
            var max = (steps - d) / n;
            var count = 0L;
            for (int i = 1; i < max + 1; i++) {
                int nd = d + n * i;
                if (nd <= steps && (nd & 1) == stepsParity) {
                    count += 1;
                }
            }
            memo.put(d, count);
            return count;
        }
    }

    private static Map<Plot, Integer> distances(char[][] map, Plot start, int maxTiles) {
        var distances = new HashMap<Plot, Integer>();
        var queue = new ArrayDeque<Plot>();
        distances.put(start, 0);
        queue.offer(start);
        while (!queue.isEmpty()) {
            var current = queue.poll();
            var distance = distances.get(current);
            for (var offset : OFFSETS) {
                var np = current.offset(offset);
                if (Math.abs(np.tile().row()) > maxTiles
                        || Math.abs(np.tile().col()) > maxTiles
                        || np.isRock(map)
                        || distances.containsKey(np)) {
                    continue;
                }
                distances.put(np, distance + 1);
                queue.offer(np);
            }
        }
        return distances;
    }

    private static Plot start(char[][] map) {
        var n = map.length;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (map[r][c] == START) {
                    map[r][c] = PLOT;
                    return new Plot(new Tile(0, 0), r, c, n);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    record Plot(Tile tile, int row, int col, int n) {
        @Override
        public String toString() {
            return "(" + row + "," + col + ")";
        }

        public boolean isRock(char[][] map) {
            return map[row][col] == ROCK;
        }

        public Plot offset(int[] offset) {
            var t = tile;
            var r = row + offset[0];
            var c = col + offset[1];
            if (r < 0) {
                r += n;
                t = new Tile(tile.row() - 1, tile.col());
            } else if (r >= n) {
                r -= n;
                t = new Tile(tile.row() + 1, tile.col());
            } else if (c < 0) {
                c += n;
                t = new Tile(tile.row(), tile.col() - 1);
            } else if (c >= n) {
                c -= n;
                t = new Tile(tile.row(), tile.col() + 1);
            }
            return new Plot(t, r, c, n);
        }
    }

    record Tile(int row, int col) {
        @Override
        public String toString() {
            return "[" + row + "," + col + "]";
        }
    }

    private static char START = 'S';
    private static char PLOT = '.';
    private static char ROCK = '#';

    private static int[] N = new int[] { -1, 0 };
    private static int[] S = new int[] { 1, 0 };
    private static int[] W = new int[] { 0, -1 };
    private static int[] E = new int[] { 0, 1 };

    private static List<int[]> OFFSETS = List.of(N, S, W, E);

    private static char[][] parse(Path input) throws IOException {
        try (var lines = Files.lines(input)) {
            return lines.map(String::toCharArray).toArray(char[][]::new);
        }
    }
}
