package solvers.aoc2018.day15;

import utils.Coordinates;

import java.util.List;

final class Elf extends Unit {
    protected Elf(Cavern cavern, Coordinates coordinates, int atk) {
        super(cavern, coordinates, atk);
    }

    @Override
    protected List<Goblin> enemies() {
        return super.cavern.goblins();
    }

    @Override
    public String toString() {
        return "Elf" + super.toString();
    }
}
