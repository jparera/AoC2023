package net.wrlt.aoc2023.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BinarySearchTest {
    @Test
    void searchesIndexOfValueInArrayWithoutDuplicates() {        
        var array = new int[] { 1, 2, 3, 4, 5 };
        assertEquals(-1, BinarySearch.indexOf(array, 0));
        assertEquals(0, BinarySearch.indexOf(array, 1));
        assertEquals(1, BinarySearch.indexOf(array, 2));
        assertEquals(2, BinarySearch.indexOf(array, 3));
        assertEquals(3, BinarySearch.indexOf(array, 4));
        assertEquals(4, BinarySearch.indexOf(array, 5));
        assertEquals(-array.length - 1, BinarySearch.indexOf(array, 6));
    }

    @Test
    void searchesIndexOfValueInArrayWithDuplicates() {
        var array = new int[] { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 };
        assertEquals(-1, BinarySearch.indexOf(array, 0));
        assertEquals(0, BinarySearch.indexOf(array, 1));
        assertEquals(2, BinarySearch.indexOf(array, 2));
        assertEquals(4, BinarySearch.indexOf(array, 3));
        assertEquals(6, BinarySearch.indexOf(array, 4));
        assertEquals(-array.length - 1, BinarySearch.indexOf(array, 6));
    }

    @Test
    void findsInsertionIndexOfValueInArrayWithoutDuplicates() {
        var array = new int[] { 1, 2, 3, 4, 5 };
        assertEquals(0, BinarySearch.insertionIndexOf(array, 0));
        assertEquals(0, BinarySearch.insertionIndexOf(array, 1));
        assertEquals(1, BinarySearch.insertionIndexOf(array, 2));
        assertEquals(2, BinarySearch.insertionIndexOf(array, 3));
        assertEquals(3, BinarySearch.insertionIndexOf(array, 4));
        assertEquals(4, BinarySearch.insertionIndexOf(array, 5));
        assertEquals(array.length, BinarySearch.insertionIndexOf(array, 6));
    }

    @Test
    void findsInsertionIndexOfValueInArrayWithDuplicates() {
        var array = new int[] { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 };
        assertEquals(0, BinarySearch.insertionIndexOf(array, 0));
        assertEquals(0, BinarySearch.insertionIndexOf(array, 1));
        assertEquals(2, BinarySearch.insertionIndexOf(array, 2));
        assertEquals(4, BinarySearch.insertionIndexOf(array, 3));
        assertEquals(6, BinarySearch.insertionIndexOf(array, 4));
        assertEquals(array.length, BinarySearch.insertionIndexOf(array, 6));
    }
}
