package net.wrlt.aoc2023.day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day14 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            var platform = platform(input);
            int n = platform.length;
            int load = 0;
            for (int c = 0; c < n; c++) {
                int stop = 0;
                for (int r = 0; r < n; r++) {
                    var type = platform[r][c];
                    switch (type) {
                        case ROUNDED:
                            load += n - stop;
                            stop++;
                            break;
                        case CUBE:
                            stop = r + 1;
                            break;
                        default:
                            break;
                    }
                }
            }
            return load;
        }
    }

    public static class Part2 {
        private static final int CYCLES = 1_000_000_000;

        private static final int UNKNOWN = -1;

        public static int execute(Path input) throws IOException {
            var platform = platform(input);
            int cycle = 0;
            int mod = CYCLE.length - 1;
            var o = Orientation.NORTH;
            long step = 1;
            var period = -1;
            var status = new HashMap<List<Integer>, Integer>();
            while (cycle < CYCLES) {
                tilt(platform, o);
                o = CYCLE[(int) (step++ & mod)];
                if (o == Orientation.NORTH) {
                    cycle++;
                    if (period == UNKNOWN) {
                        var rocks = rocks(platform);
                        if (!status.containsKey(rocks)) {
                            status.put(rocks, cycle);
                        } else {
                            var ini = status.get(rocks);
                            period = cycle - ini;
                            var r = ((CYCLES - cycle) % period);
                            cycle = CYCLES - r;
                        }
                    }
                }
            }
            return load(platform);
        }

        private static List<Integer> rocks(char[][] platform) {
            int n = platform.length;
            var rocks = new ArrayList<Integer>();
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    if (platform[r][c] == ROUNDED) {
                        rocks.add(r * n + c);
                    }
                }
            }
            return rocks;
        }

        private static int load(char[][] platform) {
            int n = platform.length;
            int load = 0;
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    if (platform[r][c] == ROUNDED) {
                        load += n - r;
                    }
                }
            }
            return load;
        }

        private static void tilt(char[][] platform, Orientation o) {
            int n = platform.length;
            switch (o) {
                case NORTH:
                    for (int c = 0; c < n; c++) {
                        int stop = 0;
                        for (int r = 0; r < n; r++) {
                            var type = platform[r][c];
                            switch (type) {
                                case ROUNDED:
                                    platform[r][c] = EMPTY;
                                    platform[stop++][c] = ROUNDED;
                                    break;
                                case CUBE:
                                    stop = r + 1;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                case WEST:
                    for (int r = 0; r < n; r++) {
                        int stop = 0;
                        for (int c = 0; c < n; c++) {
                            var type = platform[r][c];
                            switch (type) {
                                case ROUNDED:
                                    platform[r][c] = EMPTY;
                                    platform[r][stop++] = ROUNDED;
                                    break;
                                case CUBE:
                                    stop = c + 1;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                case SOUTH:
                    for (int c = 0; c < n; c++) {
                        int stop = n - 1;
                        for (int r = n - 1; r >= 0; r--) {
                            var type = platform[r][c];
                            switch (type) {
                                case ROUNDED:
                                    platform[r][c] = EMPTY;
                                    platform[stop--][c] = ROUNDED;
                                    break;
                                case CUBE:
                                    stop = r - 1;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                case EAST:
                    for (int r = 0; r < n; r++) {
                        int stop = n - 1;
                        for (int c = n - 1; c >= 0; c--) {
                            var type = platform[r][c];
                            switch (type) {
                                case ROUNDED:
                                    platform[r][c] = EMPTY;
                                    platform[r][stop--] = ROUNDED;
                                    break;
                                case CUBE:
                                    stop = c - 1;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    private static final char ROUNDED = 'O';
    private static final char CUBE = '#';
    private static final char EMPTY = '.';

    private enum Orientation {
        NORTH,
        WEST,
        SOUTH,
        EAST,
    }

    private static Orientation[] CYCLE = {
            Orientation.NORTH,
            Orientation.WEST,
            Orientation.SOUTH,
            Orientation.EAST,
    };

    private static char[][] platform(Path input) throws IOException {
        try (var lines = Files.lines(input)) {
            return lines.map(String::toCharArray).toArray(char[][]::new);
        }
    }
}
