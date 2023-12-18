package net.wrlt.aoc2023.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day13 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            return parse(input).stream().mapToInt(Part1::reflection).sum();
        }

        private static int reflection(Block block) {
            return reflection(block.hs()) * 100 + reflection(block.vs());
        }

        private static int reflection(int[] values) {
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] == values[i] && isMirrow(values, i)) {
                    return i;
                }
            }
            return 0;
        }

        private static boolean isMirrow(int[] values, int index) {
            int left = index - 1;
            int right = index;
            while (left >= 0 && right < values.length) {
                if (values[left] != values[right]) {
                    return false;
                }
                left--;
                right++;
            }
            return true;
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            return parse(input).stream().mapToInt(Part2::reflection).sum();
        }

        static int reflection(Block block) {
            return reflection(block.hs()) * 100 + reflection(block.vs());
        }

        private static int reflection(int[] values) {
            for (int i = 1; i < values.length; i++) {
                var d = distance(values[i - 1], values[i]);
                if (d <= 1 && isMirrow(values, i)) {
                    return i;
                }
            }
            return 0;
        }

        private static boolean isMirrow(int[] values, int index) {
            boolean smudge = false;
            int left = index - 1;
            int right = index;
            while (left >= 0 && right < values.length) {
                var d = distance(values[left], values[right]);
                if (d > 1) {
                    return false;
                } else if (d == 1) {
                    if (!smudge) {
                        smudge = true;
                    } else {
                        return false;
                    }
                }
                left--;
                right++;
            }
            return smudge;
        }

        private static int distance(int v1, int v2) {
            return Integer.bitCount(v1 ^ v2);
        }
    }

    private static List<Block> parse(Path input) throws IOException {
        try (var lines = Files.lines(input)) {
            var it = lines.iterator();
            var blocks = new ArrayList<Block>();
            var buffer = new ArrayList<String>();
            while (it.hasNext()) {
                var line = it.next();
                if (!line.isEmpty()) {
                    buffer.add(line);
                }
                if (line.isEmpty() || !it.hasNext()) {
                    if (buffer.isEmpty()) {
                        continue;
                    }
                    blocks.add(Block.valueOf(buffer));
                    buffer.clear();
                }
            }
            return blocks;
        }
    }

    private record Block(int[] hs, int[] vs) {
        private static final char ROCK = '#';

        public static Block valueOf(List<String> lines) {
            int rows = lines.size();
            int cols = lines.get(0).length();
            var hs = new int[rows];
            var vs = new int[cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    var rock = lines.get(r).charAt(c) == ROCK ? 1 : 0;
                    hs[r] |= rock << (cols - 1 - c);
                    vs[c] |= rock << (rows - 1 - r);
                }
            }
            return new Block(hs, vs);
        }
    }
}
