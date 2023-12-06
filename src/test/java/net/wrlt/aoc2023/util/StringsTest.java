package net.wrlt.aoc2023.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StringsTest {
    @Test
    void generateForwardIndexes() {
        var input = "abcde";
        var indices = Strings.fw(input).toArray();
        for (int i = 0; i < input.length(); i++) {
            assertEquals(i, indices[i]);
        }
    }

    @Test
    void iterateForwardUppercasing() {
        var input = "abcde";
        var uppercase = Strings
                .fwMapIndexed(input, (str, i) -> Character.toUpperCase(str.charAt(i)))
                .toArray();
        assertArrayEquals(input.toUpperCase().chars().toArray(), uppercase);
    }

    @Test
    void generateBackwardIndexes() {
        var input = "abcde";
        var indices = Strings.bw(input).toArray();
        for (int i = input.length() - 1, j = 0; i >= 0; i--) {
            assertEquals(i, indices[j++]);
        }
    }

    @Test
    void iterateBackwardUppercasing() {
        var input = "abcde";
        var uppercase = Strings
                .bwMapIndexed(input, (str, i) -> Character.toUpperCase(str.charAt(i)))
                .toArray();
        assertArrayEquals(new StringBuilder(input.toUpperCase()).reverse().chars().toArray(), uppercase);
    }

    @Test
    void findNumbers() {
        var input = "1, 2, 3";
        var actual = Strings.numbers(input).toList();
        assertEquals(List.of("1", "2", "3"), actual);
    }

    @Test
    void findWords() {
        var input = "abc, efg, hij";
        var actual = Strings.words(input).toList();
        assertEquals(List.of("abc", "efg", "hij"), actual);
    }

    @Nested
    class CharsTest {
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
