package solvers.aoc2018.day16;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.List;
import java.util.function.BiConsumer;

record Simulator(Registers before, Instruction instruction, Registers after) {
    protected static final Logger LOGGER = (Logger) LogManager.getLogger(Simulator.class);
    private static final List<BiConsumer<Registers, Instruction>> INSTRUCTIONS =
            List.of(Instructions::addr, Instructions::addi,
                    Instructions::mulr, Instructions::muli, Instructions::banr, Instructions::bani,
                    Instructions::borr, Instructions::bori, Instructions::setr, Instructions::seti,
                    Instructions::gtir, Instructions::gtri, Instructions::gtrr,
                    Instructions::eqir, Instructions::eqri, Instructions::eqrr
            );

    protected int matching() {
        var matching = 0;
        for (BiConsumer<Registers, Instruction> consumer : INSTRUCTIONS) {
            var simulable = Registers.from(before);
            consumer.accept(simulable, instruction);
            if (simulable.equals(after)) matching++;
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
