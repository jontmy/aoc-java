package solvers.aoc2018.day15;

import java.util.List;

final class Goblin extends Unit {
    protected Goblin(Cavern cavern, Coordinates coordinates) {
        super(cavern, coordinates);
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
