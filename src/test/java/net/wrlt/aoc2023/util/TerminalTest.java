package net.wrlt.aoc2023.util;

import java.time.Duration;

class TerminalTest {
    public static void main(String[] args) throws Exception {
        var matrix = new Integer[5][5];
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                matrix[r][c] = r * matrix.length + c;
            }
        }

        var terminal = Terminal.get();
        terminal.alternateScreen();

        for (int r = 0; r < matrix.length; r++) {
            int selected = r;

            terminal.cursorHome()
                    .printf("Array: column=%d\n", selected)
                    .print(matrix[0], col -> col == selected)
                    .lineSeparator()
                    .printf("Matrix: row=%d\n", selected)
                    .print(matrix, row -> row == selected)
                    .lineSeparator()
                    .printf("Matrix: row=%d col=%d\n", selected, selected)
                    .print(matrix, row -> row == selected, col -> col == selected);

            Thread.sleep(Duration.ofSeconds(1));
        }

        Thread.sleep(Duration.ofSeconds(5));
    }
}
