package net.wrlt.aoc2023.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day12 {
    public static class Part1 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var records = lines.map(line -> {
                    var parts = line.split(" ");
                    var springs = parts[0].toCharArray();
                    var groups = Strings.numbers(parts[1]).map(Integer::valueOf).toList();
                    return new Record(springs, groups);
                }).toList();
                return records.stream().parallel()
                        .mapToLong(Day12::arrangements).sum();
            }
        }

    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            try (var lines = parse(input)) {
                var records = lines.map(line -> {
                    var parts = line.split(" ");
                    var springs = String.join("?",
                            parts[0], parts[0], parts[0], parts[0], parts[0])
                            .toCharArray();
                    var groups = Strings.numbers(parts[1]).map(Integer::valueOf).toList();
                    var list = new ArrayList<Integer>();
                    for (int i = 0; i < 5; i++) {
                        list.addAll(groups);
                    }
                    return new Record(springs, list);
                }).toList();
                return records.stream().parallel()
                        .mapToLong(Day12::arrangements).sum();
            }
        }
    }

    private static long arrangements(Record record) {
        return dp(record, 0, 0, 0, new Long[128][128][128]);
    }

    private static long dp(Record record, int count, int i, int j, Long[][][] memo) {
        var groups = record.groups();
        if (count > 0 && j >= groups.size()) {
            return 0;
        } else if (i >= record.springs().length) {
            if ((count == 0 && j == groups.size())) {
                return 1;
            } else if (count > 0 && j == groups.size() - 1 && groups.get(j) == count) {
                return 1;
            } else {
                return 0;
            }
        }
        if (memo[i][j][count] != null) {
            return memo[i][j][count];
        }
        var res = 0L;
        var c = record.springs()[i];
        if (c == '.' || c == '?') {
            if (count == 0) {
                res += dp(record, count, i + 1, j, memo);
            } else if (groups.get(j) == count) {
                res += dp(record, 0, i + 1, j + 1, memo);
            }
        }
        if (c == '#' || c == '?') {
            res += dp(record, count + 1, i + 1, j, memo);
        }
        return memo[i][j][count] = res;
    }

    record Record(char[] springs, List<Integer> groups) {

    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
