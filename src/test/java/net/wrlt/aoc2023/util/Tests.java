package net.wrlt.aoc2023.util;

import java.nio.file.Path;
import java.util.Optional;

public class Tests {
    public static Path toPath(Class<?> clazz, String file) {
        try {
            var resource = Optional.ofNullable(clazz.getResource(file));
            return Path.of(resource.orElseThrow().toURI());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
