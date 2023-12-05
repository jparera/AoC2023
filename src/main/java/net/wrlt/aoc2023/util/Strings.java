package net.wrlt.aoc2023.util;

import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Strings {

    public static IntStream fwMapIndexed(String input, IntIndexedMapper mapper) {
        return fw(input).map(index -> mapper.apply(input, index));
    }

    public static IntStream fw(String input) {
        return IntStream
                .iterate(0, Math::incrementExact)
                .limit(input.length());
    }

    public static IntStream bwMapIndexed(String input, IntIndexedMapper mapper) {
        return bw(input).map(index -> mapper.apply(input, index));
    }

    public static IntStream bw(String input) {
        return IntStream
                .iterate(input.length() - 1, Math::decrementExact)
                .limit(input.length());
    }

    private static final Pattern NUMBER = Pattern.compile("\\d+");

    public static Stream<String> numbers(String line) {
        return NUMBER.matcher(line).results().map(MatchResult::group);
    }

    private static final Pattern WORDS = Pattern.compile("[A-Za-z]+");

    public static Stream<String> words(String line) {
        return WORDS.matcher(line).results().map(MatchResult::group);
    }

    /**
     * Maps <code>str</code> character at <code>index</code> to an integer.
     */
    @FunctionalInterface
    public interface IntIndexedMapper {
        int apply(String str, int index);
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
