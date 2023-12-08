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
                int index = 0;
                int steps = 0;
                String current = "AAA";
                while (!"ZZZ".equals(current)) {
                    var lr = map.get(current);
                    char c = instructions.charAt(index);
                    var next = lr[c == 'L' ? 1 : 2];
                    index = (index + 1) % instructions.length();
                    current = next;
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

                var currents = map.keySet().stream()
                        .filter(k -> k.endsWith("A"))
                        .toArray(String[]::new);

                var statusNodes = new ArrayList<Map<Status, Integer>>();
                for (int i = 0; i < currents.length; i++) {
                    statusNodes.add(new LinkedHashMap<>());
                }

                int total = currents.length;
                var stepsToCycleEntry = new int[total];
                int index = 0;
                int steps = 0;
                int finished = 0;
                int currentFinished = 0;
                while (currentFinished < total && finished < total) {
                    char c = instructions.charAt(index);
                    currentFinished = 0;
                    for (int i = 0; i < total; i++) {
                        if (currents[i] == null) {
                            continue;
                        }
                        var lr = map.get(currents[i]);
                        var next = lr[c == 'L' ? 1 : 2];
                        if (next.endsWith("Z")) {
                            currentFinished++;
                            var statusNode = statusNodes.get(i);
                            var status = new Status(index, next);
                            if (statusNode.containsKey(status)) {
                                stepsToCycleEntry[i] = statusNode.get(status);
                                next = null;
                                finished++;
                            } else {
                                statusNode.put(status, steps + 1);
                            }
                        }
                        currents[i] = next;
                    }
                    index = (index + 1) % instructions.length();
                    steps++;
                }

                if (currentFinished == total) {
                    return steps;
                }

                int gcd = stepsToCycleEntry[0];
                for (int i = 1; i < total; i++) {
                    gcd = gcd(gcd, stepsToCycleEntry[i]);
                }
                long lcm = gcd;
                for (int i = 0; i < total; i++) {
                    lcm = Math.multiplyExact(lcm, stepsToCycleEntry[i] / gcd);
                }

                return lcm;
            }
        }

        public static int gcd(int a, int b) {
            if (b == 0)
                return a;
            return gcd(b, a % b);
        }

        record Status(int instruction, String node) {
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
            map.put(nodes[0], nodes);
        }
        return map;
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
