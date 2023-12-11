package net.wrlt.aoc2023.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class Day10 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);
                return loopLength(map);
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var map = lines.map(String::toCharArray).toArray(char[][]::new);

                var loop = loop(map);
                cleanExtraPipes(map);
                markOrientation(loop, map);
                markSides(loop, map);
                fillGaps(map, INSIDE);

                return count(map, INSIDE);
            }
        }
    }

    private static int ROW = 0;
    private static int COL = 1;
    private static int VALUE = 2;
    private static int INDEX = 3;

    private static char START = 'S';
    private static char GROUND = '.';
    private static char LOOP = 'X';

    private static char INSIDE = 'I';

    private static int[] NORTH = { -1, 0, 'N' };
    private static int[] SOUTH = { 1, 0, 'S' };
    private static int[] WEST = { 0, -1, 'W' };
    private static int[] EAST = { 0, 1, 'E' };

    private static char[] PIPES = { '|', '-', 'J', 'L', '7', 'F' };

    private static int[][][] GO = {
            { NORTH, SOUTH }, // N-S (|)
            { EAST, WEST }, // W-E (-)
            { NORTH, WEST }, // N-W (J)
            { NORTH, EAST }, // N-E (L)
            { SOUTH, WEST }, // S-W (7)
            { SOUTH, EAST }, // S-E (F)
    };

    private static int[][][] GO_MAP = new int[256][][];
    static {
        GO_MAP['|'] = GO[0];
        GO_MAP['-'] = GO[1];
        GO_MAP['J'] = GO[2];
        GO_MAP['L'] = GO[3];
        GO_MAP['7'] = GO[4];
        GO_MAP['F'] = GO[5];
    }

    static int loopLength(char[][] map) {
        var start = findStart(map);
        var steps = -1;
        var queue = new ArrayDeque<int[]>();
        queue.offer(start);
        map[start[ROW]][start[COL]] = LOOP;
        while (!queue.isEmpty()) {
            steps++;
            var size = queue.size();
            while (size-- > 0) {
                var current = queue.poll();
                var neigbours = GO_MAP[current[VALUE]];
                for (var neigbour : neigbours) {
                    var next = go(current, neigbour, map);
                    if (next[VALUE] != GROUND && next[VALUE] != LOOP) {
                        map[next[ROW]][next[COL]] = LOOP;
                        queue.offer(next);
                    }
                }
            }
        }
        return steps;
    }

    static List<int[]> loop(char[][] map) {
        var start = findStart(map);
        var list = new ArrayList<int[]>();
        var stack = new ArrayDeque<int[]>();
        stack.push(start);
        while (!stack.isEmpty()) {
            var current = stack.pop();
            var value = map[current[ROW]][current[COL]];
            if (value == GROUND || value == LOOP) {
                continue;
            }
            map[current[ROW]][current[COL]] = LOOP;
            list.add(current);
            var neigbours = GO_MAP[current[VALUE]];
            for (int i = neigbours.length - 1; i >= 0; i--) {
                var next = go(current, neigbours[i], map);
                stack.push(next);
            }
        }
        return list;
    }

    private static int cleanExtraPipes(char[][] map) {
        int count = 0;
        int rows = map.length;
        int cols = map[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] != LOOP) {
                    map[r][c] = GROUND;
                }
            }
        }
        return count;
    }

    private static void markOrientation(List<int[]> loop, char[][] map) {
        for (int i = 0; i < loop.size(); i++) {
            var current = loop.get(i);
            var o = orientation(current, loop.get((i + 1) % loop.size()));
            current[VALUE] = map[current[ROW]][current[COL]] = (char) o[VALUE];
        }
    }

    private static void markSides(List<int[]> loop, char[][] map) {
        var topLeft = findTopLeft(loop, map);
        var offset = topLeft[INDEX] + 1;
        var last = topLeft;
        var po = orientation(last);
        var sign = 1;
        var previousInside = insideOrientation(po, sign);
        for (int i = 0; i < loop.size(); i++) {
            var current = loop.get((i + offset) % loop.size());

            var o = orientation(current);
            var north = po == NORTH || o == NORTH;
            var south = po == SOUTH || o == SOUTH;
            var east = po == EAST || o == EAST;
            var west = po == WEST || o == WEST;
            var corner = po != o;
            if (corner && ((north && west) || (south && east))) {
                sign *= -1;
            }

            var inside = insideOrientation(o, sign);
            if (corner) {
                var p = go(current, previousInside, map);
                if (p[VALUE] == GROUND) {
                    set(p, INSIDE, map);
                }
                var diagonal = new int[] { previousInside[ROW] + inside[ROW], previousInside[COL] + inside[COL] };
                p = go(current, diagonal, map);
                if (p[VALUE] == GROUND) {
                    set(p, INSIDE, map);
                }
            }
            var p = go(current, inside, map);
            if (p[VALUE] == GROUND) {
                set(p, INSIDE, map);
            }
            previousInside = inside;
            po = o;
        }
    }

    private static int[] insideOrientation(int[] o, int sign) {
        if (o == NORTH || o == SOUTH) {
            return sign == 1 ? EAST : WEST;
        } else {
            return sign == 1 ? SOUTH : NORTH;
        }
    }

    private static void fillGaps(char[][] map, char side) {
        int rows = map.length;
        int cols = map[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == side) {
                    fill(r, c, map[r][c], map);
                }
            }
        }
    }

    private static void fill(int r, int c, char side, char[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return;
        }
        map[r][c] = side;
        var p = new int[] { r, c, side };
        var n = go(p, NORTH, map);
        if (n[VALUE] == GROUND) {
            fill(n[ROW], n[COL], side, map);
        }
        var s = go(p, SOUTH, map);
        if (s[VALUE] == GROUND) {
            fill(s[ROW], s[COL], side, map);
        }
        var w = go(p, WEST, map);
        if (w[VALUE] == GROUND) {
            fill(w[ROW], w[COL], side, map);
        }
        var e = go(p, EAST, map);
        if (e[VALUE] == GROUND) {
            fill(e[ROW], e[COL], side, map);
        }
    }

    private static int count(char[][] map, char side) {
        int count = 0;
        int rows = map.length;
        int cols = map[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == side) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int[] orientation(int[] p) {
        return switch (p[VALUE]) {
            case 'N' -> NORTH;
            case 'S' -> SOUTH;
            case 'E' -> EAST;
            case 'W' -> WEST;
            default -> null;
        };
    }

    private static int[] orientation(int[] current, int[] next) {
        int rd = next[ROW] - current[ROW];
        int cd = next[COL] - current[COL];
        if (rd > 0) {
            return SOUTH;
        } else if (rd < 0) {
            return NORTH;
        } else if (cd > 0) {
            return EAST;
        } else {
            return WEST;
        }
    }

    private static int[] findTopLeft(List<int[]> loop, char[][] map) {
        var topLeft = loop.get(0);
        int minRow = topLeft[ROW];
        int minCol = topLeft[COL];
        int index = 0;
        for (int i = 0; i < loop.size(); i++) {
            var current = loop.get(i);
            if (current[ROW] < minRow) {
                topLeft = current;
                minRow = current[ROW];
                minCol = current[COL];
                index = i;
            } else if (current[ROW] == minRow && current[COL] < minCol) {
                topLeft = current;
                minRow = current[ROW];
                minCol = current[COL];
                index = i;
            }
        }
        return new int[] { minRow, minCol, map[minRow][minCol], index };
    }

    private static int[] findStart(char[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        var start = new int[3];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == START) {
                    start[ROW] = r;
                    start[COL] = c;

                    var n = go(start, NORTH, map);
                    var s = go(start, SOUTH, map);
                    var w = go(start, WEST, map);
                    var e = go(start, EAST, map);

                    var in = new HashSet<int[]>();
                    if (n[VALUE] == '|' || n[VALUE] == '7' || n[VALUE] == 'F') {
                        in.add(NORTH);
                    }
                    if (s[VALUE] == '|' || s[VALUE] == 'L' || s[VALUE] == 'J') {
                        in.add(SOUTH);
                    }
                    if (w[VALUE] == '-' || w[VALUE] == 'L' || w[VALUE] == 'F') {
                        in.add(WEST);
                    }
                    if (e[VALUE] == '-' || e[VALUE] == 'J' || e[VALUE] == '7') {
                        in.add(EAST);
                    }

                    for (char pipe : PIPES) {
                        var go = GO_MAP[pipe];
                        if (in.contains(go[0]) && in.contains(go[1])) {
                            start[VALUE] = map[r][c] = pipe;
                            break;
                        }
                    }
                }
            }
        }
        return start;
    }

    private static void set(int[] p, char value, char[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        var r = p[ROW];
        var c = p[COL];
        if (r >= 0 && r < rows && c >= 0 && c < cols) {
            map[r][c] = value;
        }
    }

    private static int[] go(int[] p, int[] offset, char[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        var r = p[ROW] + offset[ROW];
        var c = p[COL] + offset[COL];
        char v = GROUND;
        if (r >= 0 && r < rows && c >= 0 && c < cols) {
            v = map[r][c];
        }
        return new int[] { r, c, v };
    }

    private static Character[][] copy(char[][] matrix) {
        var copy = new Character[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = new Character[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                copy[i][j] = matrix[i][j];
            }
        }
        return copy;
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
