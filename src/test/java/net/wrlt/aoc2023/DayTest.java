package net.wrlt.aoc2023;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.wrlt.aoc2023.util.Tests;

class DayTest {
    Path input() {
        return Tests.toPath(this.getClass(), "input.txt");
    }

    Path example1() {
        return Tests.toPath(this.getClass(), "example1.txt");
    }

    @Nested
    class Part1Test {
        @Test
        @Disabled
        void passesExample() throws Exception {
            var output = Day.Part1.execute(example1());
            assertEquals(-1, output);
        }

        @Test
        @Disabled
        void passesInput() throws Exception {
            var output = Day.Part1.execute(input());
            assertEquals(-1, output);
        }
    }

    @Nested
    class Part2Test {
        @Test
        @Disabled
        void passesExample() throws Exception {
            var output = Day.Part2.execute(example1());
            assertEquals(-1, output);
        }

        @Test
        @Disabled
        void passesInput() throws Exception {
            var output = Day.Part2.execute(input());
            assertEquals(-1, output);
        }
    }
}
