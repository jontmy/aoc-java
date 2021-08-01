package solvers.aoc2018.day15;

import utils.Coordinates;

import java.util.List;

final class Goblin extends Unit {
    protected Goblin(Cavern cavern, Coordinates coordinates) {
        super(cavern, coordinates, 3);
    }

    @Override
    protected List<? extends Unit> enemies() {
        return super.cavern.elves();
    }

    @Override
    public String toString() {
        return "Goblin" + super.toString();
    }
}
