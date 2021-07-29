package solvers.aoc2018.day15;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

record Simulation(Cavern cavern) {
    protected static Simulation of(List<String> input) {
        return new Simulation(Cavern.parse(input));
    }

    protected void simulateRound() {
        var queue = new PriorityQueue<Unit>();
        queue.addAll(cavern.goblins());
        queue.addAll(cavern.elves());
        while (!queue.isEmpty()) {
            var unit = queue.remove();
            Optional<Path> path;
            if (unit instanceof Goblin goblin) path = goblin.pathfind(Cavern.ELF);
            else if (unit instanceof Elf elf) path = elf.pathfind(Cavern.GOBLIN);
            else throw new IllegalStateException(unit.toString());
            path.ifPresent(unit::move);
        }
    }
}
