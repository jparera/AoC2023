package net.wrlt.aoc2023.day22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Ranges.OfInt.Range;
import net.wrlt.aoc2023.util.Strings;

public class Day22 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var bricks = lines.map(Brick::valueOf).toList();
                var dependencies = dependencies(bricks);
                var unsafeBricks = unsafeBricks(dependencies);
                return bricks.size() - unsafeBricks.size();
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var bricks = lines.map(Brick::valueOf).toList();
                var dependencies = dependencies(bricks);
                return unsafeBricks(dependencies).stream()
                        .mapToInt(brick -> fallingBlocks(brick, dependencies))
                        .sum();
            }
        }

        private static int fallingBlocks(Integer brick, List<Set<Integer>> dependencies) {
            var n = dependencies.size();
            var falling = new HashSet<Integer>();
            falling.add(brick);
            for (int i = brick; i < n; i++) {
                var depends = dependencies.get(i);
                if (depends.isEmpty()) {
                    continue;
                }
                int count = 0;
                for (var depend : depends) {
                    if (falling.contains(depend)) {
                        count++;
                    }
                }
                if (depends.size() == count) {
                    falling.add(i + 1);
                }
            }
            return falling.size() - 1;
        }
    }

    static Set<Integer> unsafeBricks(List<Set<Integer>> dependencies) {
        var unsafe = new HashSet<Integer>();
        var n = dependencies.size();
        for (int i = 0; i < n; i++) {
            var depends = dependencies.get(i);
            if (depends.size() == 1) {
                unsafe.add(depends.iterator().next());
            }
        }
        return unsafe;
    }

    static List<Set<Integer>> dependencies(List<Brick> bricks) {
        int n = bricks.size();
        var heights = new int[10][10];
        var support = new int[10][10];
        var dependencies = new ArrayList<Set<Integer>>(n);
        var groundedBricks = new ArrayList<Brick>(n);

        bricks = bricks.stream().sorted(Brick.orderByZ()).toList();

        for (var brick : bricks) {
            int[] max = { -1 };
            brick.iterateXY((x, y) -> {
                max[0] = Math.max(max[0], heights[x][y]);
            });
            var grounded = brick.fallTo(max[0] + 1);
            groundedBricks.add(grounded);

            var id = groundedBricks.size();
            var supports = new HashSet<Integer>();
            brick.iterateXY((x, y) -> {
                if (heights[x][y] == max[0] && support[x][y] != 0) {
                    supports.add(support[x][y]);
                }
            });
            dependencies.add(supports);
            brick.iterateXY((x, y) -> {
                if (heights[x][y] == max[0] && support[x][y] != 0) {
                    supports.add(support[x][y]);
                }
            });
            brick.iterateXY((x, y) -> {
                heights[x][y] = max[0] + brick.z().length();
                support[x][y] = id;
            });
        }
        return dependencies;
    }

    private record Brick(Range x, Range y, Range z) {
        public Brick fallTo(int z) {
            return new Brick(x, y, new Range(z, z + this.z.length()));
        }

        public void iterateXY(IntBiConsumer consumer) {
            for (int x = this.x.start(); x < this.x.end(); x++) {
                for (int y = this.y.start(); y < this.y.end(); y++) {
                    consumer.accept(x, y);
                }
            }
        }

        public static Comparator<Brick> orderByZ() {
            return (l, r) -> Integer.compare(l.z().start(), r.z().start());
        }

        public static Brick valueOf(String line) {
            var values = Strings.numbers(line).mapToInt(Integer::parseInt).toArray();
            var x = range(values[0], values[3]);
            var y = range(values[1], values[4]);
            var z = range(values[2], values[5]);
            return new Brick(x, y, z);
        }

        private static Range range(int start, int end) {
            var from = Math.min(start, end);
            var len = Math.abs(start - end) + 1;
            return new Range(from, from + len);
        }
    }

    public interface IntBiConsumer {
        void accept(int x, int y);
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
