package net.wrlt.aoc2023.day08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day08 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var it = lines.iterator();
                var instructions = it.next();
                var map = map(it);
                var current = "AAA";
                int index = 0;
                int steps = 0;
                while (!"ZZZ".equals(current)) {
                    int lr = instructions.charAt(index) == 'L' ? 0 : 1;
                    current = map.get(current)[lr];
                    index = (index + 1) % instructions.length();
                    steps++;
                }
                return steps;
            }
        }

    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var it = lines.iterator();
                var instructions = it.next();
                var map = map(it);

                var endsWithA = new ArrayList<String>();
                for (var key : map.keySet()) {
                    if (key.charAt(2) == 'A') {
                        endsWithA.add(key);
                    }
                }

                var nodes = endsWithA.toArray(String[]::new);
                var statusNodes = new ArrayList<Map<Status, Integer>>();
                for (int i = 0; i < nodes.length; i++) {
                    statusNodes.add(new LinkedHashMap<>());
                }

                int total = nodes.length;
                var stepsToFirstEndInCycle = new int[total];
                int index = 0;
                int steps = 0;
                int finishedPaths = 0;
                int numEndings = 0;
                while (numEndings < total && finishedPaths < total) {
                    int lr = instructions.charAt(index) == 'L' ? 0 : 1;
                    numEndings = 0;
                    for (int i = 0; i < total; i++) {
                        var node = nodes[i];
                        if (node == null) {
                            continue;
                        }
                        nodes[i] = map.get(node)[lr];
                        if (node.charAt(2) == 'Z') {
                            numEndings++;
                            var statusNode = statusNodes.get(i);
                            var status = new Status(node, index);
                            var stepsNode = statusNode.get(status);
                            if (stepsNode != null) {
                                stepsToFirstEndInCycle[i] = stepsNode;
                                finishedPaths++;
                                nodes[i] = null;
                            } else {
                                statusNode.put(status, steps);
                            }
                        }
                    }
                    index = (index + 1) % instructions.length();
                    steps++;
                }

                if (numEndings == total) {
                    return steps;
                }

                // The remainder and the cycle length are the same.
                // In this case the result it is the LCM of all remainder/cycle.

                int gcd = stepsToFirstEndInCycle[0];
                for (int i = 1; i < total; i++) {
                    gcd = gcd(gcd, stepsToFirstEndInCycle[i]);
                }
                long lcm = gcd;
                for (int i = 0; i < total; i++) {
                    lcm = Math.multiplyExact(lcm, stepsToFirstEndInCycle[i] / gcd);
                }

                return lcm;
            }
        }

        public static int gcd(int a, int b) {
            if (b == 0)
                return a;
            return gcd(b, a % b);
        }

        record Status(String node, int index) {
        }
    }

    private static Map<String, String[]> map(Iterator<String> it) {
        var map = new HashMap<String, String[]>();
        while (it.hasNext()) {
            var line = it.next();
            if (line.isBlank()) {
                continue;
            }
            var nodes = Strings.aphanumerics(line).toArray(String[]::new);
            map.put(nodes[0], new String[] { nodes[1], nodes[2] });
        }
        return map;
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
