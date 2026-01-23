package agh.ics.oop.model.stats;

import java.util.List;
import java.util.Set;

public record SimulationStats(
        float avgChildAmount,
        float avgEnergy,
        float avgLifeTime,
        int freeFields,
        int day,
        int animalCount,
        int grassCount,
        int dominantAmount,
        Set<List<Integer>> dominantGenotypes
) {
}
