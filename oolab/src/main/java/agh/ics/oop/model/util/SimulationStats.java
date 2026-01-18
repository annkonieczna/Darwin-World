package agh.ics.oop.model.util;

import java.util.List;
import java.util.Set;

public record SimulationStats(
        int avgChildAmount,
        int avgEnergy,
        int avgLifeTime,
        int freeFields,
        int day,
        int animalCount,
        int grassCount,
        Set<List<Integer>> dominantGenotypes
) {
}