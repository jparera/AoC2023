package net.wrlt.aoc2023.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StreamsTest {
    private Path input() {
        return Tests.toPath(getClass(), "streams.txt");
    }

    @Test
    void enumerateSizedStream() {
        var list = List.of(0, 1, 2, 3);
        var indexes = Streams.enumerate(list.stream()).map(e -> e.index()).toList();
        assertEquals(list, indexes);
    }

    @Test
    void enumerateFileLines() throws IOException {
        try (var lines = Streams.enumerate(Files.lines(input()))) {
            assertEquals(List.of(0, 1, 2), lines.map(e -> e.index()).toList());
        }
    }
}
