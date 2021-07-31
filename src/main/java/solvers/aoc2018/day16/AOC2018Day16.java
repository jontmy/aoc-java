package solvers.aoc2018.day16;

import solvers.AOCDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public class AOC2018Day16 extends AOCDay<Integer> {
    private final List<Simulator> simulators;
    private final List<Instruction> instructions;

    public AOC2018Day16() throws IOException, URISyntaxException {
        super(16, 2018);
        this.simulators = new ArrayList<>();
        this.instructions = new ArrayList<>();
        parse();
    }

    private void parse() {
        var split = IntStream.range(0, input.size())
                .filter(i -> input.get(i).startsWith("A"))
                .max()
                .orElseThrow();

        // Parse the input for the first part.
        var first = IntStream.rangeClosed(0, split + 1)
                .mapToObj(input::get)
                .filter(not(String::isBlank))
                .toList();
        for (int i = 0; i < first.size(); ) {
            var before = Registers.parse(first.get(i++));
            var instruction = Instruction.parse(first.get(i++));
            var after = Registers.parse(first.get(i++));
            simulators.add(new Simulator(before, instruction, after));
        }

        // Parse the input for the second part.
        IntStream.range(split + 1, input.size())
                .mapToObj(input::get)
                .filter(not(String::isBlank))
                .map(Instruction::parse)
                .forEach(instructions::add);
    }

    @Override
    protected Integer solvePartOne(List<String> input) {
        return (int) simulators.stream()
                .map(Simulator::operations)
                .filter(ops -> ops.size() >= 3)
                .count();
    }

    @Override
    protected Integer solvePartTwo(List<String> input) {
        // Map each opcode to its operation a.k.a. higher order instruction a.k.a. BiConsumer<Registers, Instruction>.
        var unconfirmed = simulators.stream()
                .map(Simulator::instruction)
                .map(Instruction::opcode)
                .distinct()
                .collect(Collectors.toMap(op -> op, op -> new HashSet<>(Simulator.INSTRUCTIONS)));
        var confirmed = new HashMap<Integer, BiConsumer<Registers, Instruction>>();

        // Reduce each opcode mapping to just the operations that all simulations agree with by set intersections.
        for (Simulator simulator : simulators) {
            var opcode = simulator.instruction().opcode();
            var operations = simulator.operations();
            assert unconfirmed.containsKey(opcode);
            unconfirmed.get(opcode).retainAll(operations);
        }

        // Work out the number of each opcode by elimination.
        while (!unconfirmed.isEmpty()) {
            for (Integer opcode : unconfirmed.keySet()) {
                assert unconfirmed.containsKey(opcode);
                assert unconfirmed.get(opcode).size() > 0;

                // Confirm the opcode mapping if the number of its possible operations is 1.
                if (unconfirmed.get(opcode).size() == 1) {
                    var operation = unconfirmed.get(opcode).iterator().next();
                    confirmed.put(opcode, operation);
                }
            }
            confirmed.keySet().forEach(unconfirmed::remove);
            unconfirmed.values().forEach(ops -> ops.removeAll(confirmed.values()));
        }

        // Run the program through the list of instructions.
        var registers = Registers.blank();
        instructions.forEach(ins -> confirmed.get(ins.opcode()).accept(registers, ins));
        return registers.dereference(0);
    }
}
