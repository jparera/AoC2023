package net.wrlt.aoc2023.day07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import net.wrlt.aoc2023.util.Streams;

public class Day07 {
    public static class Part1 {
        private static char[] CARDS = { '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A' };

        private static Scale SCALE = Scale.ofCards(CARDS);

        public static long execute(Path input) throws IOException {
            return winnings(input, SCALE, Part1::handTypeResolver);
        }

        private static HandType handTypeResolver(String cards) {
            var f = new int[CARDS.length];
            for (int i = 0; i < cards.length(); i++) {
                var card = SCALE.weight(cards.charAt(i));
                f[card]++;
            }
            return HandType.of(f, cards.length());
        }
    }

    public static class Part2 {
        private static final char[] CARDS = { 'J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A' };

        private static final char JOKER = 'J';

        private static final Scale SCALE = Scale.ofCards(CARDS);

        public static int execute(Path input) throws IOException {
            return winnings(input, SCALE, Part2::handTypeResolver);
        }

        private static HandType handTypeResolver(String cards) {
            var f = new int[CARDS.length];
            int max = Integer.MIN_VALUE;
            int maxIndex = 0;
            var jokers = 0;
            for (int i = 0; i < cards.length(); i++) {
                if (cards.charAt(i) == JOKER) {
                    jokers++;
                    continue;
                }
                var card = SCALE.weight(cards.charAt(i));
                f[card]++;
                if (f[card] > max) {
                    max = f[card];
                    maxIndex = card;
                }
            }
            f[maxIndex] += jokers;
            return HandType.of(f, cards.length());
        }
    }

    private static int winnings(Path input, Scale scale, HandType.Resolver resolver) throws IOException {
        try (var lines = parse(input)) {
            return Streams
                    .enumerate(lines
                            .map(line -> Hand.valueOf(line, scale, resolver))
                            .sorted())
                    .mapToInt(e -> Math.multiplyExact(e.element().bid(), e.index() + 1))
                    .reduce(0, Math::addExact);
        }
    }

    private static class Hand implements Comparable<Hand> {
        private String cards;

        private int weight;

        private int bid;

        private HandType type;

        public Hand(String cards, int weight, int bid, HandType type) {
            this.cards = cards;
            this.weight = weight;
            this.bid = bid;
            this.type = type;
        }

        public int bid() {
            return bid;
        }

        @Override
        public String toString() {
            return String.format("Hand[cards=%s, type=%s]", cards, type);
        }

        @Override
        public int compareTo(Hand o) {
            int cmp = type.compareTo(o.type);
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compareUnsigned(weight, o.weight);
        }

        public static Hand valueOf(String line, Scale scale, HandType.Resolver resolver) {
            var parts = line.split("\\s");
            var cards = parts[0];
            var weight = scale.weight(cards);
            var bid = Integer.parseInt(parts[1]);
            var type = resolver.resolve(parts[0]);
            return new Hand(parts[0], weight, bid, type);
        }

    }

    enum HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        TREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND;

        public static HandType of(int[] frequencies, int handCards) {
            var f = new int[handCards + 1];
            for (int i = 0; i < frequencies.length; i++) {
                f[frequencies[i]]++;
            }
            if (f[1] == 5) {
                // High card: { 1, 1, 1, 1, 1 }
                return HIGH_CARD;
            } else if (f[1] == 3) {
                // One pair: { 1, 1, 1, 2 }
                return ONE_PAIR;
            } else if (f[1] == 1 && f[2] == 2) {
                // Two pair: { 1, 2, 2 }
                return TWO_PAIR;
            } else if (f[1] == 2 && f[3] == 1) {
                // Three of a kind: { 1, 1, 3 }
                return TREE_OF_A_KIND;
            } else if (f[2] == 1 && f[3] == 1) {
                // Full house: { 2, 3 }
                return FULL_HOUSE;
            } else if (f[4] == 1) {
                // Four of a kind: { 1, 4 }
                return FOUR_OF_A_KIND;
            } else {
                // Five of a kind: { 5 }
                return FIVE_OF_A_KIND;
            }
        }

        @FunctionalInterface
        interface Resolver {
            public HandType resolve(String cards);
        }
    }

    private static class Scale {
        private final int[] map;

        public Scale(int[] map) {
            this.map = map;
        }

        public int weight(char card) {
            return map[card];
        }

        public int weight(String cards) {
            // 4 bits = 16 cards -> 4 bits * 5 cards = 20 bits < 32 bits
            if (cards.length() > 5) {
                throw new IllegalArgumentException();
            }
            int weight = 0;
            for (int i = 0; i < cards.length(); i++) {
                weight <<= 4;
                weight += map[cards.charAt(i)];
            }
            return weight;
        }

        public static Scale ofCards(char[] cards) {
            // 4 bits = 16 cards -> 4 bits * 5 cards = 20 bits < 32 bits
            if (cards.length > 16) {
                throw new IllegalArgumentException();
            }
            var map = new int[256];
            for (int i = 0; i < cards.length; i++) {
                map[cards[i]] = i;
            }
            return new Scale(map);
        }
    }

    private static Stream<String> parse(Path input) throws IOException {
        return Files.lines(input);
    }
}
