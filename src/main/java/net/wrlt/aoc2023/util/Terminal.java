package net.wrlt.aoc2023.util;

import java.io.Console;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class Terminal {
    private Console console;

    public Terminal(Console console) {
        this.console = console;
    }

    public static Terminal get() {
        return new Terminal(System.console());
    }

    public Terminal alternateScreen() {
        return execute(Job.ALTERNATE_SCREEN);
    }

    public Terminal cursorHome() {
        return execute(Job.CURSOR_HOME);
    }

    public Terminal print(int[][] matrix) {
        return print(matrix, row -> false, col -> false);
    }

    public Terminal print(int[][] matrix, IntPredicate highlightRow) {
        return print(matrix, highlightRow, col -> false);
    }

    public Terminal print(int[][] matrix, IntPredicate highlightRow, IntPredicate highlightCol) {
        var maxDigits = Arrays.stream(matrix)
                .flatMapToInt(row -> Arrays.stream(row))
                .map(Terminal::digits)
                .reduce(0, Integer::max);
        var max = digits(matrix.length - 1);
        for (int i = 0; i < matrix.length; i++) {
            printf("%s: ", leftPad(Integer.toString(i), max));
            print(matrix[i], maxDigits, highlightRow.test(i), highlightCol);
        }
        return this;
    }

    public Terminal print(int[] array) {
        return print(array, i -> false);
    }

    public Terminal print(int[] array, IntPredicate highlightCol) {
        var max = Arrays.stream(array).map(Terminal::digits).reduce(0, Integer::max);
        return print(array, max, false, highlightCol);
    }

    private Terminal print(
            int[] array,
            int leftPad,
            boolean highlightRow,
            IntPredicate highlightCol) {
        return iterate(
                0, array.length,
                i -> leftPad(Long.toString(array[i]), leftPad),
                highlightRow, highlightCol);
    }

    public Terminal print(long[][] matrix) {
        return print(matrix, row -> false, col -> false);
    }

    public Terminal print(long[][] matrix, IntPredicate highlightRow) {
        return print(matrix, highlightRow, col -> false);
    }

    public Terminal print(long[][] matrix, IntPredicate highlightRow, IntPredicate highlightCol) {
        var maxDigits = Arrays.stream(matrix)
                .flatMapToLong(row -> Arrays.stream(row))
                .mapToInt(Terminal::digits)
                .reduce(0, Integer::max);
        var max = digits(matrix.length - 1);
        for (int i = 0; i < matrix.length; i++) {
            printf("%s: ", leftPad(Integer.toString(i), max));
            print(matrix[i], maxDigits, highlightRow.test(i), highlightCol);
        }
        return this;
    }

    public Terminal print(long[] array) {
        return print(array, i -> false);
    }

    public Terminal print(long[] array, IntPredicate highlightCol) {
        var max = Arrays.stream(array).mapToInt(Terminal::digits).reduce(0, Integer::max);
        return print(array, max, false, highlightCol);
    }

    private Terminal print(
            long[] array,
            int leftPad,
            boolean highlightBackground,
            IntPredicate highlightCol) {
        return iterate(
                0, array.length,
                i -> leftPad(Long.toString(array[i]), leftPad),
                highlightBackground, highlightCol);
    }

    public <T> Terminal print(T[][] matrix) {
        return print(matrix, row -> false, col -> false);
    }

    public <T> Terminal print(T[][] matrix, IntPredicate highlightRow) {
        return print(matrix, highlightRow, col -> false);
    }

    public <T> Terminal print(T[][] matrix, IntPredicate highlightRow, IntPredicate highlightCol) {
        var max = Arrays.stream(matrix)
                .flatMap(row -> Arrays.stream(row))
                .map(value -> Objects.toString(value))
                .mapToInt(String::length)
                .reduce(0, Integer::max);
        var maxRowDigits = digits(matrix.length - 1);
        for (int i = 0; i < matrix.length; i++) {
            printf("%s: ", leftPad(Integer.toString(i), maxRowDigits));
            print(matrix[i], max, highlightRow.test(i), highlightCol);
        }
        return this;
    }

    public <T> Terminal print(T[] array) {
        return print(array, col -> false);
    }

    public <T> Terminal print(T[] array, IntPredicate highlightCol) {
        var max = Arrays.stream(array)
                .map(value -> Objects.toString(value))
                .mapToInt(String::length)
                .reduce(0, Integer::max);
        return print(array, max, false, highlightCol);
    }

    private <T> Terminal print(
            T[] array,
            int leftPad,
            boolean highlightBackground,
            IntPredicate highlightCol) {
        return iterate(
                0, array.length,
                i -> leftPad(Objects.toString(array[i]), leftPad),
                highlightBackground, highlightCol);
    }

    private Terminal iterate(
            int start,
            int end,
            IntFunction<String> fn,
            boolean highlightRow,
            IntPredicate highlightCol) {
        var builder = Job.builder();
        builder.openParenthesis();
        for (int i = start; i < end; i++) {
            if (highlightRow || highlightCol.test(i)) {
                builder.highlightBackground().highlightColor();
            } else {
                builder.defaultColor();
            }
            builder.append(fn.apply(i));
            if (highlightRow || highlightCol.test(i)) {
                builder.turnOffAttributes();
                if (highlightRow) {
                    builder.highlightBackground().highlightColor();
                }
            }
            if (i < end - 1) {
                builder.comma();
            }
        }
        if (highlightRow) {
            builder.turnOffAttributes();
        }
        builder.closeParenthesis().lineSeparator();
        execute(builder.build());
        return this;
    }

    public Terminal lineSeparator() {
        return printf(System.lineSeparator());
    }

    public Terminal printf(String format, Object... args) {
        console.printf(String.format(format, args));
        return this;
    }

    private Terminal execute(Job job) {
        console.printf(job.toString());
        if (job.isTurnOffAttributes()) {
            console.printf(Job.TURNOFF_ATTRIBUTES.toString());
        }
        return this;
    }

    private static int digits(long value) {
        int count = value <= 0 ? 1 : 0;
        value = Math.abs(value);
        while (value > 0) {
            value = value / 10;
            count++;
        }
        return count;
    }

    private static String leftPad(String value, int length) {
        if (value.length() >= length) {
            return value;
        }
        var buffer = new StringBuilder();
        int padding = length - value.length();
        buffer.repeat(' ', padding);
        buffer.append(value);
        return buffer.toString();
    }

    private static class Job {
        private static final String ESC = "\u001B";

        static Job ALTERNATE_SCREEN = new Job(ESC + "[?1049h");

        static Job CURSOR_HOME = new Job(ESC + "[H");

        static Job TURNOFF_ATTRIBUTES = new Job(ESC + "[m");

        static Job HIGHLIGHT_COLOR = color(0x99, 0xFF, 0x99);

        static Job ACCENT_COLOR = color(0xE6, 0x41, 0x0B);

        static Job DEFAULT_COLOR = color(0x00, 0x99, 0x00);

        static Job HIGHLIGHT_BACKGROUND = background(0x05, 0x05, 0x05);

        static Job OPEN_PARENTHESIS = Job
                .builder().accentColor().append('[').build();

        static Job CLOSE_PARENTHESIS = Job
                .builder().accentColor().append(']').build();

        static Job COMMA = Job
                .builder().accentColor().append(',').build();

        private final String command;

        private final boolean turnOffAttributes;

        Job(String command) {
            this(command, false);
        }

        @Override
        public String toString() {
            return command;
        }

        Job(String command, boolean turnOffAttributes) {
            this.command = command;
            this.turnOffAttributes = turnOffAttributes;
        }

        public boolean isTurnOffAttributes() {
            return turnOffAttributes;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Job color(int r, int g, int b) {
            return new Job(ESC + "[38;2;" + r + ";" + g + ";" + b + "m", true);
        }

        public static Job background(int r, int g, int b) {
            return new Job(ESC + "[48;2;" + r + ";" + g + ";" + b + "m", true);
        }

        static class Builder {
            private boolean turnOffAttributes;

            private final StringBuilder buffer = new StringBuilder();

            public Job build() {
                return new Job(buffer.toString(), turnOffAttributes);
            }

            public Builder turnOffAttributes() {
                turnOffAttributes = false;
                return append(TURNOFF_ATTRIBUTES);
            }

            public Builder highlightColor() {
                return append(HIGHLIGHT_COLOR);
            }

            public Builder accentColor() {
                return append(ACCENT_COLOR);
            }

            public Builder defaultColor() {
                return append(DEFAULT_COLOR);
            }

            public Builder highlightBackground() {
                return append(HIGHLIGHT_BACKGROUND);
            }

            public Builder openParenthesis() {
                return append(OPEN_PARENTHESIS);
            }

            public Builder closeParenthesis() {
                return append(CLOSE_PARENTHESIS);
            }

            public Builder comma() {
                return append(COMMA);
            }

            public Builder lineSeparator() {
                return append(System.lineSeparator());
            }

            public Builder append(Job job) {
                turnOffAttributes |= job.isTurnOffAttributes();
                return append(job.toString());
            }

            public Builder append(char c) {
                buffer.append(c);
                return this;
            }

            public Builder append(String text) {
                buffer.append(text);
                return this;
            }
        }
    }
}