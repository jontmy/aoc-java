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

    protected static Simulation of(List<String> input, int elfAtk) {
        return new Simulation(Cavern.parse(input, elfAtk));
    }

    // Returns true if the round completed with all units having acted, otherwise false.
    protected boolean simulateRound() {
        var queue = new PriorityQueue<Unit>();
        queue.addAll(cavern.goblins());
        queue.addAll(cavern.elves());
        assert queue.size() == cavern.goblins().size() + cavern.elves().size();

        while (!queue.isEmpty()) {
            if (cavern.goblins().isEmpty() || cavern.elves().isEmpty()) return false;
            var unit = queue.remove();
            if (unit.hp() > 0) unit.act();
        }
        return true;
    }

    protected int simulateToCompletion() {
        var rounds = 0;
        boolean roundCompleted;
        do {
            roundCompleted = simulateRound();
            if (roundCompleted) rounds++;
        } while (!cavern.goblins().isEmpty() && !cavern.elves().isEmpty());
        LOGGER.info("Simulation ends after {} rounds.", rounds);
        var remainingHP = (cavern.goblins().isEmpty() ? cavern.elves() : cavern.goblins())
                .stream()
                .map(Unit::hp)
                .reduce(Integer::sum)
                .orElseThrow();
        return rounds * remainingHP;
    }
}
