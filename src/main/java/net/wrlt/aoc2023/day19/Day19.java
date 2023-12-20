package net.wrlt.aoc2023.day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Ranges.OfInt.Range;

public class Day19 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            var system = parse(input);
            return system.run();
        }
    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            var system = parse(input);

            var fullRange = new Range(MIN_RANGE, MAX_RANGE);
            var parts = Map.of("x", fullRange, "m", fullRange, "a", fullRange, "s", fullRange);

            var accepted = system.run(List.of(parts));
            return accepted.stream()
                    .mapToLong(a -> a.values().stream()
                            .mapToLong(Range::length)
                            .reduce(1, Math::multiplyExact))
                    .reduce(0, Math::addExact);
        }
    }

    private static ElfSystem parse(Path input) throws IOException {
        var splits = Files.readString(input).split("\\r\\n\\r\\n|\\n\\n");
        var workflows = splits[0].split("\\r?\\n");
        var parts = splits[1].split("\\r?\\n");
        return new ElfSystem(
                Arrays.asList(workflows).stream()
                        .map(Workflow::valueOf)
                        .collect(Collectors.toMap(w -> w.label(), w -> w)),
                Arrays.asList(parts).stream().map(Day19::valueOfParts).toList());
    }

    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 4001;

    private static final Pattern ELEMENT = Pattern.compile("[A-Za-z0-9=:><]+");

    static class ElfSystem {
        private final Map<String, Workflow> workflows;

        private List<Map<String, Range>> parts;

        public ElfSystem(Map<String, Workflow> workflows, List<Map<String, Range>> parts) {
            this.workflows = workflows;
            this.parts = parts;
        }

        public int run() {
            var accepted = run(this.parts);
            return accepted.stream()
                    .flatMapToInt(m -> m.values().stream().mapToInt(Range::start))
                    .sum();
        }

        public List<Map<String, Range>> run(List<Map<String, Range>> parts) {
            var accepted = new ArrayList<Map<String, Range>>();
            var queue = new ArrayDeque<PendingParts>();
            for (var part : parts) {
                queue.offer(new PendingParts("in", part));
            }
            while (!queue.isEmpty()) {
                var current = queue.poll();
                var dones = workflows.get(current.label()).apply(current.parts());
                for (var done : dones) {
                    switch (done.label()) {
                        case "A":
                            accepted.add(done.parts);
                            break;
                        case "R":
                            break;
                        default:
                            queue.offer(done);
                            break;
                    }
                }
            }
            return accepted;
        }
    }

    record Workflow(String label, List<Rule> rules) {
        public List<PendingParts> apply(Map<String, Range> parts) {
            var done = new ArrayList<PendingParts>();
            var queue = new ArrayDeque<Map<String, Range>>();
            queue.offer(parts);
            for (var rule : rules) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    var pendings = rule.apply(queue.poll());
                    for (var pending : pendings) {
                        if (pending.label() == null) {
                            queue.offer(pending.parts());
                        } else {
                            done.add(pending);
                        }
                    }
                }
                if (queue.isEmpty()) {
                    return done;
                }
            }
            throw new IllegalStateException();
        }

        public static Workflow valueOf(String workflow) {
            var elements = elements(workflow).toList();
            var it = elements.iterator();
            var label = it.next();
            var rules = new ArrayList<Rule>();
            while (it.hasNext()) {
                rules.add(Rule.valueOf(it.next()));
            }
            return new Workflow(label, rules);
        }
    }

    interface Rule {
        List<PendingParts> apply(Map<String, Range> parts);

        static Rule valueOf(String rule) {
            var splits = rule.split(":");
            if (splits.length == 1) {
                var sendToLabel = splits[0];
                return parts -> List.of(new PendingParts(sendToLabel, parts));
            } else {
                if (splits[0].contains(">")) {
                    var gt = splits[0].split(">");
                    var category = gt[0];
                    var sendToLabel = splits[1];
                    var condition = new Range(Integer.parseInt(gt[1]) + 1, MAX_RANGE);
                    return compare(category, sendToLabel, condition);
                } else {
                    var lt = splits[0].split("<");
                    var category = lt[0];
                    var sendToLabel = splits[1];
                    var condition = new Range(MIN_RANGE, Integer.parseInt(lt[1]));
                    return compare(category, sendToLabel, condition);
                }
            }
        }

        private static Rule compare(String category, String sendToLabel, Range condition) {
            return parts -> {
                var range = parts.get(category);
                var sendToRange = condition.intersect(range);
                var sendToNext = range.substract(sendToRange);
                var list = new ArrayList<PendingParts>(2);
                if (sendToRange != null) {
                    var sendToParts = new HashMap<>(parts);
                    sendToParts.put(category, sendToRange);
                    list.add(new PendingParts(sendToLabel, sendToParts));
                }
                if (!sendToNext.isEmpty()) {
                    var sendToParts = new HashMap<>(parts);
                    sendToParts.put(category, sendToNext.get(0));
                    list.add(new PendingParts(null, sendToParts));
                }
                return list;
            };
        }
    }

    record PendingParts(String label, Map<String, Range> parts) {

    }

    static Map<String, Range> valueOfParts(String parts) {
        return elements(parts)
                .map(e -> e.split("="))
                .collect(Collectors.toMap(e -> e[0],
                        e -> {
                            int value = Integer.parseInt(e[1]);
                            return new Range(value, value + 1);
                        }));
    }

    static Stream<String> elements(String line) {
        return ELEMENT.matcher(line).results().map(MatchResult::group);
    }
}
