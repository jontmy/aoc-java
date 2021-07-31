package solvers.aoc2018.day16;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

record Simulator(Registers before, Instruction instruction, Registers after) {
    protected static final Set<BiConsumer<Registers, Instruction>> INSTRUCTIONS =
            Set.of(Instructions::addr, Instructions::addi,
                    Instructions::mulr, Instructions::muli, Instructions::banr, Instructions::bani,
                    Instructions::borr, Instructions::bori, Instructions::setr, Instructions::seti,
                    Instructions::gtir, Instructions::gtri, Instructions::gtrr,
                    Instructions::eqir, Instructions::eqri, Instructions::eqrr
            );

    protected Set<BiConsumer<Registers, Instruction>> operations() {
        var matching = new HashSet<BiConsumer<Registers, Instruction>>();
        for (BiConsumer<Registers, Instruction> operation : INSTRUCTIONS) {
            var simulable = Registers.from(before);
            operation.accept(simulable, instruction);
            if (simulable.equals(after)) matching.add(operation);
        }
        return matching;
    }

    @Override
    public String toString() {
        return "\nBefore: %s\nInstruction: %s\nAfter: %s\n".formatted(
                before.toString(),
                instruction.toString(),
                after.toString()
        );
    }
}
