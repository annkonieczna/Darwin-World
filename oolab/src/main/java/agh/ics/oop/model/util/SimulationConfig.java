package agh.ics.oop.model.util;

public record SimulationConfig(
        int width,
        int height,
        int startGrassAmount,   //<=width*height
        int startAnimalAmount,
        int growingGrassAmount,
        int toxicGrassChance,   //between 0 and 100
        int energyFromGrass,
        int energyFromToxicGrass,
        int startEnergy,
        int moveEnergyCost,
        int reproductionEnergyCost,
        int minimumEnergyForReproduction,
        int minMutation,    //<=maxMutation
        int maxMutation,    //<=genomeLength
        int genomeLength
) {
}
