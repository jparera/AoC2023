package net.wrlt.aoc2023.day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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
            try (var cards = parse(input)) {
                var list = cards.toList();
                var n = list.size();

                var instances = new int[n];
                Arrays.fill(instances, 1);

                int sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += instances[i];
                    var card = list.get(i);
                    var quantity = instances[i];
                    for (int j = 1; j <= card.matchingNumbers(); j++) {
                        if (i + j < n) {
                            instances[i + j] += quantity;
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

        public static Card valueOf(String text) {
            var parts = text.split(":\\s+");
            var id = Integer.parseInt(parts[0].split("\\s+")[1]);
            var numberParts = parts[1].split("\\s+\\|\\s+");
            var winningNumbers = new HashSet<Integer>(
                    Arrays.stream(numberParts[0].split("\\s+"))
                            .map(Integer::valueOf).toList());
            var myNumbers = new HashSet<Integer>(
                    Arrays.stream(numberParts[1].split("\\s+"))
                            .map(Integer::valueOf)
                            .toList());
            return new Card(id - 1, winningNumbers, myNumbers);
        }
    }

    private static Stream<Card> parse(Path input) throws IOException {
        return Files.lines(input).map(Card::valueOf);
    }
}
