package net.wrlt.aoc2023.day24;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day24 {
    public static class Part1 {
        public static int execute(Path input, double min, double max) throws IOException {
            try (var lines = parse(input)) {
                var stones = lines.map(HailStone::valueOf).toList();
                var n = stones.size();
                int count = 0;
                for (int i = 0; i < n; i++) {
                    var s1 = stones.get(i);
                    for (int j = i + 1; j < n; j++) {
                        var s2 = stones.get(j);
                        var interseccion = s1.willCrossXY(s2);
                        if (interseccion != null) {
                            var x = interseccion.p().x();
                            var y = interseccion.p().y();
                            if (x >= min && x <= max && y >= min && y <= max) {
                                count++;
                            }
                        }
                    }
                }
                return count;
            }
        }
    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var stones = lines.map(HailStone::valueOf).toList();

                var rows = new ArrayList<double[]>();
                rows.addAll(Arrays.stream(substract(rows(stones.get(0)), rows(stones.get(1)))).toList());
                rows.addAll(Arrays.stream(substract(rows(stones.get(0)), rows(stones.get(2)))).toList());
                var matrix = rows.toArray(double[][]::new);

                gauss(matrix);

                var px = (long) Math.rint(matrix[0][6]);
                var py = (long) Math.rint(matrix[1][6]);
                var pz = (long) Math.rint(matrix[2][6]);

                return px + py + pz;
            }
        }

        private static double[][] rows(HailStone s) {
            // px py pz vx vy vz c
            var p = s.p();
            var v = s.v();
            return new double[][] {
                    { 0, v.z(), -v.y(), 0, -p.z(), p.y(), p.y() * v.z() - p.z() * v.y() },
                    { -v.z(), 0, v.x(), p.z(), 0, -p.x(), p.z() * v.x() - p.x() * v.z() },
                    { v.y(), -v.x(), 0, -p.y(), p.x(), 0, p.x() * v.y() - p.y() * v.x() },
            };
        }

        private static double[][] substract(double[][] a, double[][] b) {
            int rows = a.length;
            int cols = a[0].length;
            var s = new double[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    s[r][c] = a[r][c] - b[r][c];
                }
            }
            return s;
        }

        private static void gauss(double[][] matrix) {
            int rows = matrix.length;
            int cols = matrix[0].length;
            for (int r = 0; r < rows; r++) {
                var i = r;
                var sorted = Arrays.stream(matrix, r, rows).toArray(double[][]::new);
                Arrays.sort(sorted, (left, right) -> Double.compare(right[i], left[i]));
                for (int rr = 0; rr < sorted.length; rr++) {
                    matrix[r + rr] = sorted[rr];
                }

                double v = matrix[r][r];
                for (int c = 0; c < cols; c++) {
                    matrix[r][c] = matrix[r][c] / v;
                }
                for (int rr = r + 1; rr < rows; rr++) {
                    v = matrix[rr][r];
                    for (int c = 0; c < cols; c++) {
                        matrix[rr][c] -= v * matrix[r][c];
                    }
                }
            }
            for (int r = rows - 1; r >= 0; r--) {
                for (int rr = 0; rr < r; rr++) {
                    double v = matrix[rr][r];
                    for (int c = 0; c < cols; c++) {
                        matrix[rr][c] -= v * matrix[r][c];
                    }
                }
            }
        }
    }

    private record HailStone(Position p, Vector v, Vector a, Vector b) {

        private static final Double EPSILON = 0.000001;

        @SuppressWarnings("unused")
        public Intersection willCross(HailStone o) {
            if (!a.crossProduct(o.a).isZero()) {
                var xy = (o.b.y() - b.y()) / (a.y() - o.a.y());
                var xz = (o.b.z() - b.z()) / (a.z() - o.a.z());
                if (Math.abs(xy - xz) <= EPSILON) {
                    var t1 = (xy - p.x()) / v.x();
                    var t2 = (xy - o.p.x()) / o.v.x();
                    if (t1 >= 0 && t2 >= 0) {
                        var y = a.y() * xy + b.y();
                        var z = a.z() * xy + b.z();
                        return new Intersection(new Position(xy, y, z), t1, t2);
                    }
                }
            }
            return null;
        }

        public Intersection willCrossXY(HailStone o) {
            if (a.y() != o.a.y()) {
                var x = (o.b.y() - b.y()) / (a.y() - o.a.y());
                var t1 = (x - p.x()) / v.x();
                var t2 = (x - o.p.x()) / o.v.x();
                if (t1 >= 0 && t2 >= 0) {
                    var y = a.y() * x + b.y();
                    return new Intersection(new Position(x, y, 0), t1, t2);
                }
            }
            return null;
        }

        public static HailStone valueOf(String line) {
            var values = Strings.integers(line).toList();
            var px = Double.parseDouble(values.get(0));
            var py = Double.parseDouble(values.get(1));
            var pz = Double.parseDouble(values.get(2));

            var vx = Double.parseDouble(values.get(3));
            var vy = Double.parseDouble(values.get(4));
            var vz = Double.parseDouble(values.get(5));

            var ax = 1;
            var ay = vy / vx;
            var az = vz / vx;

            var bx = 0;
            var by = py - ay * px;
            var bz = pz - az * px;

            var p = new Position(px, py, pz);
            var v = new Vector(vx, vy, vz);
            var a = new Vector(ax, ay, az);
            var b = new Vector(bx, by, bz);
            return new HailStone(p, v, a, b);
        }
    }

    private record Intersection(Position p, double t1, double t2) {
        @SuppressWarnings("unused")
        public boolean isCollision() {
            return t1 == t2;
        }
    }

    private record Position(double x, double y, double z) {
    }

    private record Vector(double x, double y, double z) {
        public Vector crossProduct(Vector o) {
            var x = this.y * o.z - this.z * o.y;
            var y = this.z * o.x - this.x * o.z;
            var z = this.x * o.y - this.y * o.x;
            return new Vector(x, y, z);
        }

        public boolean isZero() {
            return x == 0 && y == 0 && z == 0;
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
