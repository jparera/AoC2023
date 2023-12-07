package net.wrlt.aoc2023.day07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Day07 {
    public static class Part1 {
        private static char[] CARD_SET = { '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A' };

        public static long execute(Path input) throws IOException {
            return winnings(input, Scale.ofCards(CARD_SET, Part1::cardFrequencies));
        }

        private static int[] cardFrequencies(String cards, int[] map) {
            var f = new int[CARD_SET.length];
            for (int i = 0; i < cards.length(); i++) {
                var card = map[cards.charAt(i)];
                f[card]++;
            }
            return f;
        }
    }

    public static class Part2 {
        private static final char[] CARD_SET = { 'J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A' };

        private static final char JOKER = 'J';

        public static int execute(Path input) throws IOException {
            return winnings(input, Scale.ofCards(CARD_SET, Part2::cardFrequencies));
        }

        private static int[] cardFrequencies(String cards, int[] map) {
            var f = new int[CARD_SET.length];
            int max = Integer.MIN_VALUE;
            int maxIndex = 0;
            var jokers = 0;
            for (int i = 0; i < cards.length(); i++) {
                if (cards.charAt(i) == JOKER) {
                    jokers++;
                    continue;
                }
                var card = map[cards.charAt(i)];
                f[card]++;
                if (f[card] > max) {
                    max = f[card];
                    maxIndex = card;
                }
            }
            f[maxIndex] += jokers;
            return f;
        }
    }

    private static int winnings(Path input, Scale scale) throws IOException {
        try (var lines = parse(input)) {
            var sorted = lines.map(line -> Hand.valueOf(line, scale)).sorted();
            var it = sorted.iterator();
            int sum = 0;
            for (int i = 1; it.hasNext(); i++) {
                var hand = it.next();
                sum += i * hand.bid();
            }
            return sum;
        }
    }

    private static class Hand implements Comparable<Hand> {
        private String cards;

        private int bid;

        private int weight;

        public Hand(String cards, int bid, int weight) {
            this.cards = cards;
            this.weight = weight;
            this.bid = bid;
        }

        public int bid() {
            return bid;
        }

        @Override
        public String toString() {
            return String.format("%s %s", cards, bid);
        }

        @Override
        public int compareTo(Hand o) {
            return Integer.compareUnsigned(weight, o.weight);
        }

        public static Hand valueOf(String line, Scale scale) {
            var parts = line.split("\\s");
            var cards = parts[0];
            var bid = Integer.parseInt(parts[1]);
            var weight = scale.weight(cards);
            return new Hand(parts[0], bid, weight);
        }
    }

    private static class Scale {
        private final int[] map;

        private final CardFrequencies cardFrequencies;

        public Scale(int[] map, CardFrequencies cardFrequencies) {
            this.map = map;
            this.cardFrequencies = cardFrequencies;
        }

        public static Scale ofCards(char[] cardSet, CardFrequencies frequencies) {
            // 4 bits = 16 -> 4 * 1 type + 4 * 5 cards = 24 bits < 32
            if (cardSet.length > 16) {
                throw new IllegalArgumentException();
            }
            var map = new int[256];
            for (int i = 0; i < cardSet.length; i++) {
                map[cardSet[i]] = i;
            }
            return new Scale(map, frequencies);
        }

        public int weight(String cards) {
            // 4 bits = 16 -> 4 * 1 type + 4 * 5 cards = 24 bits < 32
            if (cards.length() > 5) {
                throw new IllegalArgumentException();
            }
            int weight = typeWeight(cards);
            for (int i = 0; i < cards.length(); i++) {
                weight <<= 4;
                weight += map[cards.charAt(i)];
            }
            return weight;
        }

        private int typeWeight(String cards) {
            var f = cardFrequencies.count(cards, map);
            var ff = new int[cards.length() + 1];
            for (int i = 0; i < f.length; i++) {
                ff[f[i]]++;
            }
            if (ff[1] == 5) {
                // High card: { 1, 1, 1, 1, 1 }
                return 0;
            } else if (ff[1] == 3) {
                // One pair: { 1, 1, 1, 2 }
                return 1;
            } else if (ff[1] == 1 && ff[2] == 2) {
                // Two pair: { 1, 2, 2 }
                return 2;
            } else if (ff[1] == 2 && ff[3] == 1) {
                // Three of a kind: { 1, 1, 3 }
                return 3;
            } else if (ff[2] == 1 && ff[3] == 1) {
                // Full house: { 2, 3 }
                return 4;
            } else if (ff[4] == 1) {
                // Four of a kind: { 1, 4 }
                return 5;
            } else {
                // Five of a kind: { 5 }
                return 6;
            }
        }

        @FunctionalInterface
        private interface CardFrequencies {
            int[] count(String cards, int[] map);
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
