package solvers.aoc2018.day16;

import java.util.function.BiFunction;

final class Instructions {
    private static void abstraction(Registers registers, Instruction instruction, Type i1, Type i2, BiFunction<Integer, Integer, Integer> function) {
        int a, b;
        if (i1 == Type.VALUE) a = instruction.i1();
        else a = registers.dereference(instruction.i1());
        if (i2 == Type.VALUE) b = instruction.i2();
        else b = registers.dereference(instruction.i2());
        var result = function.apply(a, b);
        registers.write(instruction.o(), result);
    }

    protected static void addr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.POINTER, Integer::sum);
    }

    protected static void addi(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE, Integer::sum);
    }

    protected static void mulr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.POINTER, (a, b) -> a * b);
    }

    protected static void muli(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE, (a, b) -> a * b);
    }

    protected static void banr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.POINTER, (a, b) -> a & b);
    }

    protected static void bani(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE, (a, b) -> a & b);
    }

    protected static void borr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.POINTER, (a, b) -> a | b);
    }

    protected static void bori(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE, (a, b) -> a | b);
    }

    protected static void setr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE, (a, b) -> a);
    }

    protected static void seti(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.VALUE, Type.VALUE, (a, b) -> a);
    }

    protected static void gtir(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.VALUE, Type.POINTER,
                (a, b) -> a > b ? 1 : 0);
    }

    protected static void gtri(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE,
                (a, b) -> a > b ? 1 : 0);
    }

    protected static void gtrr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.POINTER,
                (a, b) -> a > b ? 1 : 0);
    }

    protected static void eqir(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.VALUE, Type.POINTER,
                (a, b) -> a.equals(b) ? 1 : 0);
    }

    protected static void eqri(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.VALUE,
                (a, b) -> a.equals(b) ? 1 : 0);
    }

    protected static void eqrr(Registers registers, Instruction instruction) {
        abstraction(registers, instruction, Type.POINTER, Type.POINTER,
                (a, b) -> a.equals(b) ? 1 : 0);
    }

    private enum Type {
        VALUE,
        POINTER
    }
}
