package solvers.aoc2018.day15;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import solvers.AOCDay;

import java.nio.file.LinkOption;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

record Simulation(Cavern cavern) {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Simulation.class);

    protected static Simulation of(List<String> input) {
        return new Simulation(Cavern.parse(input));
    }

    // Returns true if any unit moved during this round, otherwise false.
    protected boolean simulateRound() {
        var queue = new PriorityQueue<Unit>();
        queue.addAll(cavern.goblins());
        queue.addAll(cavern.elves());

        var moved = false;
        while (!queue.isEmpty()) {
            var unit = queue.remove();
            var before = Coordinates.from(unit.coordinates());
            unit.move();
            moved = moved || !before.equals(unit.coordinates());
        }
        return moved;
    }
}
