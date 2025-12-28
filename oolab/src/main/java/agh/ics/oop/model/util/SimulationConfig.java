package agh.ics.oop.model.util;

public record SimulationConfig(
        int width,
        int height,
        int startGrassAmount,   //<=width*height
        int startAnimalAmount,
        int growingGrassAmount,
        int toxicGrassChance,
        int energyFromGrass,
        int energyFromToxicGrass,
        int startEnergy,
        int loosingEnergy,
        int reproductionEnergyCost,
        int minMutation,    //<=maxMutation
        int maxMutation,    //<=genomeLength
        int genomeLength
) {
}
