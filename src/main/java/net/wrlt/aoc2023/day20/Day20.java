package net.wrlt.aoc2023.day20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day20 {
    public static class Part1 {
        public static long execute(Path input) throws IOException {
            var network = parse(input);
            int steps = 0;
            var counters = new int[2];
            while (steps < 1000) {
                network.pushButton(pulse -> {
                    switch (pulse.level()) {
                        case HIGH:
                            counters[1]++;
                            break;
                        case LOW:
                            counters[0]++;
                            break;
                    }
                });
                steps++;
            }
            return counters[0] * counters[1];
        }
    }

    public static class Part2 {
        public static long execute(Path input) throws IOException {
            // &kh -> rx
            // All the inputs of kh must be HIGH to send a LOW to rx.
            // Outputs of [pv, qh, xm, hz] are connected to kh.
            // HIGH pulses from [pv, qh, xm, hz] to kh must be monitorized.
            var network = parse(input);
            var origins = Set.of("pv", "qh", "xm", "hz");
            var highs = new HashMap<String, Integer>();
            var periods = new HashMap<String, Integer>();
            var step = new int[1];
            while (true) {
                network.pushButton(pulse -> {
                    var to = pulse.to();
                    var from = pulse.from();
                    if (pulse.level() == Level.HIGH && "kh".equals(to) && origins.contains(from)) {
                        var last = highs.get(from);
                        if (last == null) {
                            highs.put(from, step[0]);
                        } else {
                            periods.put(from, step[0] - last);
                        }
                    }
                });
                if (periods.size() == origins.size()) {
                    break;
                }
                step[0]++;
            }
            // lcm(a,b) * gcd(a, b) = |a * b| -> lcm(a,b) = |a * b| / gcd(a, b)
            // lcm(a, b, c) = lcm(lcm(a, b), c)
            return periods.values().stream()
                    .mapToLong(Integer::intValue)
                    .reduce(1, Day20::lcm);
        }
    }

    private static long lcm(long a, long b) {
        return (a * b) / gcd(a, b);
    }

    private static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    private static class Network {
        private final Map<String, Module> modules;

        private final Map<String, Set<String>> inputs;

        private final Map<String, Set<String>> outputs;

        private final Queue<Pulse> queue = new ArrayDeque<>();

        private static final Pulse BUTTON_SIGNAL = new Pulse("button", "broadcaster", Level.LOW);

        public Network(Map<String, Module> modules,
                Map<String, Set<String>> inputs,
                Map<String, Set<String>> outputs) {
            this.modules = modules;
            this.inputs = inputs;
            this.outputs = outputs;
        }

        public void pushButton(PulseMonitor monitor) {
            queue.add(BUTTON_SIGNAL);
            while (!queue.isEmpty()) {
                var pulse = queue.poll();
                monitor.process(pulse);
                var module = modules.get(pulse.to());
                if (module != null) {
                    module.process(pulse);
                }
            }
        }

        class Broadcaster implements Module {
            private final String name = "broadcaster";

            @Override
            public String name() {
                return name;
            }

            @Override
            public void process(Pulse pulse) {
                var outs = outputs.get(name);
                for (var output : outs) {
                    queue.add(new Pulse(name, output, pulse.level()));
                }
            }
        }

        class FlipFlop implements Module {
            private final String name;

            private boolean status;

            FlipFlop(String name) {
                this.name = name;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public void process(Pulse pulse) {
                if (pulse.level() == Level.LOW) {
                    status = !status;
                    var level = status ? Level.HIGH : Level.LOW;
                    var outs = outputs.get(name);
                    for (var output : outs) {
                        queue.add(new Pulse(name, output, level));
                    }
                }
            }
        }

        class Conjunction implements Module {
            private final String name;

            private final Set<String> memory = new HashSet<>();

            Conjunction(String name) {
                this.name = name;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public void process(Pulse pulse) {
                switch (pulse.level()) {
                    case HIGH:
                        memory.add(pulse.from());
                        break;
                    case LOW:
                        memory.remove(pulse.from());
                        break;
                }
                var ins = inputs.get(name);
                var level = ins.size() == memory.size() ? Level.LOW : Level.HIGH;
                var outs = outputs.get(name);
                for (var output : outs) {
                    queue.add(new Pulse(name, output, level));
                }
            }
        }
    }

    interface PulseMonitor {
        void process(Pulse pulse);
    }

    interface Module {
        void process(Pulse pulse);

        String name();
    }

    record Pulse(String from, String to, Level level) {

    }

    enum Level {
        LOW,
        HIGH,
    }

    private static Network parse(Path input) throws IOException {
        var devices = new HashMap<String, Module>();
        var inputs = new HashMap<String, Set<String>>();
        var outputs = new HashMap<String, Set<String>>();

        var network = new Network(devices, inputs, outputs);

        for (var line : Files.readAllLines(input)) {
            var ids = aphanumerics(line).toList();
            var name = ids.get(0);
            var type = name.charAt(0);
            var device = switch (type) {
                case '%' -> network.new FlipFlop(name.substring(1));
                case '&' -> network.new Conjunction(name.substring(1));
                default -> network.new Broadcaster();
            };
            devices.put(device.name(), device);
            for (int i = 1; i < ids.size(); i++) {
                var id = ids.get(i);
                inputs.computeIfAbsent(id, k -> new LinkedHashSet<>()).add(device.name());
                outputs.computeIfAbsent(device.name(), k -> new LinkedHashSet<>()).add(id);
            }
        }
        return network;
    }

    private static final Pattern ALPHANUMERICS = Pattern.compile("[A-Za-z0-9%&]+");

    public static Stream<String> aphanumerics(String line) {
        return ALPHANUMERICS.matcher(line).results().map(MatchResult::group);
    }
}
