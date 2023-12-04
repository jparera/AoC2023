package net.wrlt.aoc2023.day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Strings;

public class Day04 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var cards = parse(input)) {
                return cards.mapToInt(Card::points).sum();
            }
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var stream = parse(input)) {
                var cards = stream.toList();
                var n = cards.size();

                var instances = new int[n];
                Arrays.fill(instances, 1);

                int sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += instances[i];
                    var card = cards.get(i);
                    for (int j = 1; j <= card.matchingNumbers(); j++) {
                        if (i + j < n) {
                            instances[i + j] += instances[i];
                        }
                    }
                }

                return sum;
            }
        }
    }

    private record Card(int id, Set<Integer> winningNumbers, Set<Integer> myNumbers) {
        public int matchingNumbers() {
            int count = 0;
            for (var number : winningNumbers) {
                if (myNumbers.contains(number)) {
                    count++;
                }
            }
            return count;
        }

        public int points() {
            int count = matchingNumbers();
            return count == 0 ? 0 : 1 << (count - 1);
        }

        public static Card valueOf(String line) {
            var id = Strings.numbers(line).findFirst().orElseThrow() - 1;
            var parts = line.split(":")[1].split("\\|");
            var winningNumbers = Strings.numbers(parts[0]).collect(Collectors.toSet());
            var myNumbers = Strings.numbers(parts[1]).collect(Collectors.toSet());
            return new Card(id - 1, winningNumbers, myNumbers);
        }
    }

    private static Stream<Card> parse(Path input) throws IOException {
        return Files.lines(input).map(Card::valueOf);
    }
}
