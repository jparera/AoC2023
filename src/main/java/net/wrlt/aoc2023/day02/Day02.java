package net.wrlt.aoc2023.day02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.wrlt.aoc2023.day02.Day02.Game.Subset;

public class Day02 {
    public static class Part1 {
        public static int execute(Path input) throws IOException {
            try (var games = parse(input)) {
                return games
                        .filter(Part1::isPossible)
                        .mapToInt(Game::id).sum();
            }
        }

        private static boolean isPossible(Game game) {
            return game.subsets().stream()
                    .filter(subset -> subset.red() > 12 || subset.green() > 13 || subset.blue() > 14)
                    .findAny()
                    .isEmpty();
        }
    }

    public static class Part2 {
        public static int execute(Path input) throws IOException {
            try (var games = parse(input)) {
                return games.map(Part2::maxSubset)
                        .mapToInt(Subset::power)
                        .sum();
            }
        }

        private static Subset maxSubset(Game game) {
            int red = 0;
            int green = 0;
            int blue = 0;
            for (var subset : game.subsets()) {
                red = Math.max(red, subset.red());
                green = Math.max(green, subset.green());
                blue = Math.max(blue, subset.blue());
            }
            return new Subset(red, green, blue);
        }
    }

    private static Stream<Game> parse(Path input) throws IOException {
        return Files.lines(input).map(Game::valueOf);
    }

    record Game(int id, List<Subset> subsets) {

        public static Game valueOf(String game) {
            var parts = game.split(": ");
            var id = Integer.parseInt(parts[0].split(" ")[1]);
            var subsets = Arrays.stream(parts[1].split("; ")).map(Subset::valueOf).toList();
            return new Game(id, subsets);
        }

        record Subset(int red, int green, int blue) {
            public int power() {
                return Math.multiplyExact(Math.multiplyExact(this.red, this.green), this.blue);
            }

            public static Subset valueOf(String subset) {
                var cubes = subset.split(", ");
                int red = 0, green = 0, blue = 0;
                for (var cube : cubes) {
                    var parts = cube.split(" ");
                    var count = Integer.parseInt(parts[0]);
                    var color = parts[1];
                    switch (color) {
                        case "red":
                            red = count;
                            break;
                        case "green":
                            green = count;
                            break;
                        case "blue":
                            blue = count;
                            break;
                        default:
                            break;
                    }
                }
                return new Subset(red, green, blue);
            }
        }
    }
}
