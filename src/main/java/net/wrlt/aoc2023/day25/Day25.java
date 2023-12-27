package net.wrlt.aoc2023.day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day25 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var file = lines.map(l -> Strings.aphanumerics(l).toList())
                        .collect(Collectors.toMap(l -> l.get(0), l -> l.subList(1, l.size())));

                var wires = file.entrySet().stream()
                        .flatMap(e -> e.getValue().stream().map(c -> new String[] { e.getKey(), c }))
                        .toList();

                var graph = new HashMap<String, Set<String>>();
                var ws = new HashMap<String, Map<String, Integer>>();
                for (var wire : wires) {
                    ws.computeIfAbsent(wire[0], k -> new HashMap<>()).computeIfAbsent(wire[1], k -> 1);
                    ws.computeIfAbsent(wire[1], k -> new HashMap<>()).computeIfAbsent(wire[0], k -> 1);
                    graph.computeIfAbsent(wire[0], k -> new HashSet<>()).add(wire[1]);
                    graph.computeIfAbsent(wire[1], k -> new HashSet<>()).add(wire[0]);
                }
                return minimumCut(graph, ws);
            }
        }
    }

    static int minimumCut(Map<String, Set<String>> graph, Map<String, Map<String, Integer>> ws) {
        List<String> all = new ArrayList<>(graph.keySet());
        var n = all.size();
        var partitions = new HashMap<String, List<String>>();
        for (var node : all) {
            partitions.put(node, new ArrayList<>(List.of(node)));
        }
        while (partitions.size() > 2) {
            var cut = minimumCutPhase(partitions, ws, graph);
            if (cut.cost() == 3) {
                var size = cut.partition().size();
                return size * (n - size);
            }
        }
        return -1;
    }

    private static Cut minimumCutPhase(
            Map<String, List<String>> partitions,
            Map<String, Map<String, Integer>> ws,
            Map<String, Set<String>> graph) {
        var distances = new HashMap<String, Integer>();
        var nodes = new PriorityQueue<String>(
                (l, r) -> Integer.compare(distances.getOrDefault(r, 0), distances.getOrDefault(l, 0)));
        nodes.add(partitions.keySet().iterator().next());
        var done = new LinkedHashSet<String>();
        while (done.size() < partitions.size()) {
            var node = nodes.poll();
            if (done.contains(node)) {
                continue;
            }
            done.add(node);
            var nws = ws.get(node);
            for (var neighbor : graph.get(node)) {
                if (done.contains(neighbor)) {
                    continue;
                }
                var distance = distances.getOrDefault(neighbor, 0);
                distances.put(neighbor, distance + nws.get(neighbor));
                nodes.add(neighbor);
            }
        }

        var t = done.removeLast();
        var s = done.getLast();
        int cost = 0;
        for (var value : ws.get(t).values()) {
            cost += value;
        }
        var cut = new Cut(cost, partitions.get(t));

        var sns = graph.get(s);
        var tns = graph.get(t);
        var swns = ws.get(s);
        var twns = ws.get(t);
        for (var tn : tns) {
            var ttns = graph.get(tn);
            ttns.remove(t);
            var tnwns = ws.get(tn);
            tnwns.remove(t);
            var twn = twns.get(tn);
            if (!tn.equals(s)) {
                sns.add(tn);
                ttns.add(s);
                var swn = swns.computeIfAbsent(tn, k -> 0);
                swns.put(tn, swn + twn);
                tnwns.put(s, swn + twn);
            }
        }

        ws.remove(t);
        graph.remove(t);

        partitions.get(s).addAll(partitions.get(t));
        partitions.remove(t);

        return cut;
    }

    record Cut(int cost, List<String> partition) {

    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
