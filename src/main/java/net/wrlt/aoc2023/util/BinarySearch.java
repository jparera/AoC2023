package net.wrlt.aoc2023.util;

public class BinarySearch {
    public static int indexOf(int[] values, int value) {        
        int index = insertionIndexOf(values, value);
        return index < values.length && values[index] == value ? index : -index - 1;
    }

    public static int insertionIndexOf(int[] values, int value) {
        int left = 0;
        int right = values.length;
        while (left < right) {
            int mid = (left + right) >>> 1;
            if (values[mid] < value) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
