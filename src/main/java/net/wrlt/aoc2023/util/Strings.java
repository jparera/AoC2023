package net.wrlt.aoc2023.util;

import java.util.OptionalInt;
import java.util.function.IntPredicate;

public class Strings {
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
