package net.wrlt.aoc2023.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.wrlt.aoc2023.util.Ranges.OfInt;
import net.wrlt.aoc2023.util.Ranges.OfLong;

public class RangesTest {
    @Nested
    class OfIntTest {
        @Test
        public void intersectEmpty() {
            var r1 = new OfInt.Range(1, 2);
            assertNull(r1.intersect(null));
        }

        @Test
        public void intersectDisjointRanges() {
            var r1 = new OfInt.Range(1, 2);
            var r2 = new OfInt.Range(3, 4);
            assertNull(r1.intersect(r2));
        }

        @Test
        public void intersectJointRanges() {
            var r1 = new OfInt.Range(1, 3);
            var r2 = new OfInt.Range(2, 4);
            assertEquals(new OfInt.Range(2, 3), r1.intersect(r2));
        }

        @Test
        public void addEmpty() {
            var r1 = new OfInt.Range(1, 2);
            assertEquals(List.of(r1), r1.add(null));
        }

        @Test
        public void addDisjointRangesGap0() {
            var r1 = new OfInt.Range(2, 3);
            var r2 = new OfInt.Range(1, 2);
            assertEquals(List.of(new OfInt.Range(1, 3)), r1.add(r2));
        }

        @Test
        public void addDisjointRangesGap1() {
            var r1 = new OfInt.Range(3, 4);
            var r2 = new OfInt.Range(1, 2);
            assertEquals(List.of(r1, r2), r1.add(r2));
        }

        @Test
        public void addJointRanges() {
            var r1 = new OfInt.Range(2, 4);
            var r2 = new OfInt.Range(1, 3);
            assertEquals(List.of(new OfInt.Range(1, 4)), r1.add(r2));
        }
    }

    @Nested
    class OfLongTest {
        @Test
        public void intersectEmpty() {
            var r1 = new OfLong.Range(1, 2);
            assertNull(r1.intersect(null));
        }

        @Test
        public void intersectDisjointRanges() {
            var r1 = new OfLong.Range(1, 2);
            var r2 = new OfLong.Range(3, 4);
            assertNull(r1.intersect(r2));
        }

        @Test
        public void intersectJointRanges() {
            var r1 = new OfLong.Range(1, 3);
            var r2 = new OfLong.Range(2, 4);
            assertEquals(new OfLong.Range(2, 3), r1.intersect(r2));
        }

        @Test
        public void addEmpty() {
            var r1 = new OfLong.Range(1, 2);
            assertEquals(List.of(r1), r1.add(null));
        }

        @Test
        public void addDisjointRangesGap0() {
            var r1 = new OfLong.Range(2, 3);
            var r2 = new OfLong.Range(1, 2);
            assertEquals(List.of(new OfLong.Range(1, 3)), r1.add(r2));
        }

        @Test
        public void addDisjointRangesGap1() {
            var r1 = new OfLong.Range(3, 4);
            var r2 = new OfLong.Range(1, 2);
            assertEquals(List.of(r1, r2), r1.add(r2));
        }

        @Test
        public void addJointRanges() {
            var r1 = new OfLong.Range(2, 4);
            var r2 = new OfLong.Range(1, 3);
            assertEquals(List.of(new OfLong.Range(1, 4)), r1.add(r2));
        }
    }
}
