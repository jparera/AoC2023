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
import java.util.Set;
import java.util.stream.Stream;

public class Day23 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);

                int n = map.length;

                var start = new Point(0, 1);
                var end = new Point(n - 1, n - 2);

                var stack = new ArrayDeque<Distance>();
                stack.push(new Distance(start, 0));

                var path = new ArrayList<Point>();
                int max = -1;
                while (!stack.isEmpty()) {
                    var current = stack.poll();
                    var point = current.point();
                    path.subList(current.distance(), path.size()).clear();
                    if (end.equals(point)) {
                        max = Math.max(max, path.size());
                        continue;
                    }
                    if (path.contains(point)) {
                        continue;
                    }
                    path.add(point);
                    switch (map[point.row()][point.col()]) {
                        case '>':
                            move(stack, map, current, E);
                            break;
                        case '<':
                            move(stack, map, current, W);
                            break;
                        case '^':
                            move(stack, map, current, N);
                            break;
                        case 'v':
                            move(stack, map, current, S);
                            break;
                        case '.':
                            move(stack, map, current, N);
                            move(stack, map, current, S);
                            move(stack, map, current, W);
                            move(stack, map, current, E);
                            break;
                    }
                }
                return max;
            }
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

                var distances = new HashMap<Point, List<Distance>>();
                for (var v : vertices) {
                    var distance = new ArrayList<Distance>();
                    var visited = new HashSet<Point>();
                    var queue = new ArrayDeque<Distance>();
                    queue.push(new Distance(v, 0));
                    while (!queue.isEmpty()) {
                        var current = queue.pop();
                        var p = current.point();
                        if (visited.contains(p)) {
                            continue;
                        }
                        visited.add(p);
                        if (vertices.contains(p) && !v.equals(p)) {
                            distance.add(current);
                            continue;
                        }
                        for (var neighbor : graph.get(p)) {
                            queue.push(new Distance(neighbor, current.distance() + 1));
                        }
                    }
                    distances.put(v, distance);
                }
                return dfs(start, end, distances, new HashSet<Point>());
            }
        }

        private static int dfs(Point node, Point end, Map<Point, List<Distance>> distances, Set<Point> visited) {
            if (node.equals(end)) {
                return 0;
            }
            if (visited.contains(node)) {
                return Integer.MIN_VALUE;
            }
            visited.add(node);
            int max = Integer.MIN_VALUE;
            for (var d : distances.get(node)) {
                max = Math.max(max, dfs(d.point(), end, distances, visited) + d.distance());
            }
            visited.remove(node);
            return max;
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

    private static void move(Deque<Distance> stack, char[][] map, Distance from, int[] offset) {
        var n = map.length;
        var nr = from.point().row() + offset[0];
        var nc = from.point().col() + offset[1];
        if (nr < 0 || nr >= n || nc < 0 || nc >= n || map[nr][nc] == '#') {
            return;
        }
        stack.push(new Distance(new Point(nr, nc), from.distance() + 1));
    }

    record Distance(Point point, int distance) {
    }

    record Point(int row, int col) {
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
