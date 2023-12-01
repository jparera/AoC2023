package net.wrlt.aoc2023.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Piles {
    public static Map<Integer, ArrayDeque<String>> parse(Stream<String> lines, int step, int length) {
        record Pair(int index, String value) {
        }
        return lines
                .flatMap(line -> {                    
                    var pairs = new ArrayList<Pair>();
                    for (int i = 0; i < line.length(); i += step) {
                        var c = line.charAt(i);
                        if (c == '[') {
                            var column = line.substring(i + 1, i + 1 + length);
                            pairs.add(new Pair((i / step) + 1, column));
                        }
                    }
                    return pairs.stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Pair::index,
                                Collectors.mapping(
                                        Pair::value,
                                        Collectors.toCollection(ArrayDeque::new))));

    }
}
