package net.wrlt.aoc2023.day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day15 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var steps = parse(input)) {
                return steps.mapToInt(Day15::hash).sum();
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var steps = parse(input)) {
                var boxes = new HashMap<Integer, LinkedList<Lens>>();
                for (int i = 0; i < 256; i++) {
                    boxes.put(i, new LinkedList<>());
                }
                var it = steps.iterator();
                while (it.hasNext()) {
                    var step = it.next();
                    var parts = Strings.aphanumerics(step).toArray(String[]::new);
                    var label = parts[0];
                    var hash = hash(label);
                    var box = boxes.get(hash);
                    if (parts.length == 2) {
                        // Operation character is equals
                        int length = Integer.parseInt(parts[1]);
                        var found = new boolean[1];
                        box.forEach(lens -> {
                            if (lens.getLabel().equals(label)) {
                                found[0] = true;
                                lens.setLength(length);
                            }
                        });
                        if (!found[0]) {
                            box.add(new Lens(label, length));
                        }
                    } else {
                        // Operation character is dash
                        box.removeIf(lens -> lens.getLabel().equals(label));
                    }
                }
                int total = 0;
                for (int i = 1; i <= 256; i++) {
                    var box = boxes.get(i - 1);
                    int slot = 1;
                    for (var lens : box) {
                        total += i * slot * lens.getLength();
                        slot++;
                    }
                }
                return total;
            }
        }
    }

    static int hash(String text) {
        var hash = 0;
        for (int i = 0; i < text.length(); i++) {
            hash += text.charAt(i);
            hash *= 17;
            hash = hash & 255;
        }
        return hash;
    }

    private static class Lens {
        private final String label;

        private int length;

        private Lens(String label, int length) {
            this.label = label;
            this.length = length;
        }

        public String getLabel() {
            return label;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        @Override
        public String toString() {
            return "[" + label + " " + length + "]";
        }

        @Override
        public boolean equals(Object obj) {
            var o = (Lens) obj;
            return this.label.equals(o.label);
        }

        @Override
        public int hashCode() {
            return this.label.hashCode();
        }
    }

    private static final Pattern COMMA = Pattern.compile(",");

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input).flatMap(COMMA::splitAsStream);
    }
}
