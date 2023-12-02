package net.wrlt.aoc2023.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StringsTest {
    @Nested
    class Chars {
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
