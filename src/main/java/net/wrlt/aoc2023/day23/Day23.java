package net.wrlt.aoc2023.day23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day23 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);

                int n = map.length;

                var start = new Point(0, 1);
                var end = new Point(n - 1, n - 2);

                var visited = new HashMap<Point, Distance>();
                var stack = new ArrayDeque<Distance>();
                stack.push(new Distance(start, 0));

                int max = Integer.MIN_VALUE;
                while (!stack.isEmpty()) {
                    var current = stack.peek();
                    var point = current.point();
                    if (end.equals(point)) {
                        stack.poll();
                        max = Math.max(max, current.distance());
                        continue;
                    }
                    var d = visited.get(point);
                    if (d != null) {
                        if (d == current) {
                            visited.remove(point);
                        }
                        stack.pop();
                        continue;
                    }
                    visited.put(point, current);
                    switch (map[point.row()][point.col()]) {
                        case '>':
                            push(stack, map, current, E);
                            break;
                        case '<':
                            push(stack, map, current, W);
                            break;
                        case '^':
                            push(stack, map, current, N);
                            break;
                        case 'v':
                            push(stack, map, current, S);
                            break;
                        case '.':
                            push(stack, map, current, N);
                            push(stack, map, current, S);
                            push(stack, map, current, W);
                            push(stack, map, current, E);
                            break;
                    }
                }
                return max;
            }
        }

        private static void push(Deque<Distance> stack, char[][] map, Distance from, int[] offset) {
            var n = map.length;
            var nr = from.point().row() + offset[0];
            var nc = from.point().col() + offset[1];
            if (nr < 0 || nr >= n || nc < 0 || nc >= n || map[nr][nc] == '#') {
                return;
            }
            stack.push(new Distance(new Point(nr, nc), from.distance() + 1));
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);

                var n = map.length;
                var graph = graph(map);

                var start = new Point(0, 1);
                var end = new Point(n - 1, n - 2);

                var joins = graph.entrySet().stream()
                        .filter(e -> e.getValue().size() > 2)
                        .map(Map.Entry::getKey)
                        .toList();
                var vertices = new HashSet<Point>();
                vertices.add(start);
                vertices.add(end);
                vertices.addAll(joins);

                var distances = new int[n][n][][];
                for (var v : vertices) {
                    var distance = new ArrayList<int[]>();
                    var visited = new boolean[n][n];
                    var queue = new ArrayDeque<int[]>();
                    queue.push(new int[] { v.row(), v.col(), 0 });
                    while (!queue.isEmpty()) {
                        var current = queue.pop();
                        var r = current[0];
                        var c = current[1];
                        var d = current[2];

                        if (visited[r][c]) {
                            continue;
                        }
                        visited[r][c] = true;

                        var p = new Point(r, c);
                        if (vertices.contains(p) && !v.equals(p)) {
                            distance.add(new int[] { r, c, d });
                            continue;
                        }
                        for (var neighbor : graph.get(p)) {
                            var nr = neighbor.row();
                            var nc = neighbor.col();
                            queue.push(new int[] { nr, nc, d + 1 });
                        }
                    }
                    distances[v.row()][v.col()] = distance.stream().toArray(int[][]::new);
                }
                var max = new int[1];
                new DFS(end.row(), end.col(), max, distances, new boolean[n][n]).execute(start.row(), start.col(), 0);
                return max[0];
            }
        }

        private record DFS(int er, int ec, int[] max, int[][][][] distances, boolean[][] visited) {
            private void execute(int r, int c, int d) {
                if (er == r && ec == c) {
                    max[0] = Math.max(max[0], d);
                    return;
                }
                if (visited[r][c]) {
                    return;
                }
                visited[r][c] = true;
                for (var neighbor : distances[r][c]) {
                    var nr = neighbor[0];
                    var nc = neighbor[1];
                    var nd = neighbor[2];
                    execute(nr, nc, d + nd);
                }
                visited[r][c] = false;
            }
        }

        private static Map<Point, List<Point>> graph(char[][] map) {
            var n = map.length;
            var graph = new HashMap<Point, List<Point>>();
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    if (map[r][c] == '#') {
                        continue;
                    }
                    var p = new Point(r, c);
                    var neighbours = new ArrayList<Point>();
                    for (var offset : OFFSETS) {
                        var nr = p.row() + offset[0];
                        var nc = p.col() + offset[1];
                        if (nr < 0 || nr >= n || nc < 0 || nc >= n || map[nr][nc] == '#') {
                            continue;
                        }
                        neighbours.add(new Point(nr, nc));
                    }
                    graph.put(p, neighbours);
                }
            }
            return graph;
        }
    }

    private static final int[] N = { -1, 0 };
    private static final int[] S = { 1, 0 };
    private static final int[] E = { 0, 1 };
    private static final int[] W = { 0, -1 };

    private static final int[][] OFFSETS = { N, S, E, W };

    record Distance(Point point, int distance) {
    }

    record Point(int row, int col) {
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
