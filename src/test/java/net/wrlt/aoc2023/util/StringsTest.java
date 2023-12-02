package net.wrlt.aoc2023.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.IntUnaryOperator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StringsTest {
    @Nested
    class Chars {
        @Test
        void iterateForwardUppercasing() {
            var input = "abcde";
            IntUnaryOperator op = i -> Character.toUpperCase(input.charAt(i));
            var uppercase = Strings.fwIndex(input, op).toArray();
            assertArrayEquals(input.toUpperCase().chars().toArray(), uppercase);
        }

        @Test
        void iterateBackwardUppercasing() {
            var input = "abcde";
            IntUnaryOperator op = i -> Character.toUpperCase(input.charAt(i));
            var uppercase = Strings.bwIndex(input, op).toArray();
            assertArrayEquals(new StringBuilder(input.toUpperCase()).reverse().chars().toArray(), uppercase);
        }

        @Test
        void generateForwardIndexes() {
            var input = "abcde";
            var indices = Strings.fw(input).toArray();
            for (int i = 0; i < input.length(); i++) {
                assertEquals(i, indices[i]);
            }
        }

        @Test
        void generateBackwardIndexes() {
            var input = "abcde";
            var indices = Strings.bw(input).toArray();
            for (int i = input.length() - 1, j = 0; i >= 0 ; i--) {
                assertEquals(i, indices[j++]);
            }
        }

        @Test
        void findFirstCharacterMatchesPredicate() {
            assertEquals('1', Strings.Chars.first("a1b2c", Character::isDigit).orElseThrow());
        }

        @Test
        void findLastCharacterMatchesPredicate() {
            assertEquals('2', Strings.Chars.last("a1b2c", Character::isDigit).orElseThrow());
        }
    }
}
