package net.wrlt.aoc2023.util;

import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class Strings {
    public static IntStream fwIndex(String input, IntUnaryOperator mapper) {
        return fw(input).map(mapper);
    }

    public static IntStream fw(String input) {
        return IntStream
                .iterate(0, Math::incrementExact)
                .limit(input.length());
    }

    public static IntStream bwIndex(String input, IntUnaryOperator mapper) {
        return bw(input).map(mapper);
    }

    public static IntStream bw(String input) {
        return IntStream
                .iterate(input.length() - 1, Math::decrementExact)
                .limit(input.length());
    }

    public static class Chars {
        public static OptionalInt first(String input, IntPredicate predicate) {
            int len = input.length();
            for (int i = 0; i < len; i++) {
                var c = input.charAt(i);
                if (predicate.test(c)) {
                    return OptionalInt.of(c);
                }
            }
            return OptionalInt.empty();
        }

        public static OptionalInt last(String input, IntPredicate predicate) {
            for (int i = input.length() - 1; i >= 0; i--) {
                var c = input.charAt(i);
                if (predicate.test(c)) {
                    return OptionalInt.of(c);
                }
            }
            return OptionalInt.empty();
        }
    }
}
