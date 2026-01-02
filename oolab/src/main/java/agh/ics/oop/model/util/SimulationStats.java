package agh.ics.oop.model.util;

public record SimulationStats(
        int avgChildAmount,
        int avgEnergy,
        int avgLifeTime,
        int freeFields,
        int day,
        int animalCount,
        int grassCount
) {
}