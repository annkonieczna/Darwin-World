package agh.ics.oop.model.stats;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.movement.Vector2d;
import agh.ics.oop.model.util.GrassPositionGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class StatsManager {
    private final Map<List<Integer>, Integer> genomeCount = new HashMap<>();
    private Set<List<Integer>> currDominantGenomes = new HashSet<>();
    private int dominantAmount;

    private float avgChildAmount;
    private float avgEnergy;
    private float avgLifeTime;
    private long sumLifeTime;
    private int deadAnimalsCount;
    private int freeFields;

    public void registerGenome(Animal animal) {
        List<Integer> key = animal.getGenome();
        genomeCount.put(key, genomeCount.getOrDefault(key, 0) + 1);
    }

    public void unregisterGenome(Animal animal) {
        List<Integer> key = animal.getGenome();
        int count = genomeCount.getOrDefault(key, 0);
        if (count <= 1) {
            genomeCount.remove(key);
        } else {
            genomeCount.put(key, count - 1);
        }
    }

    public void registerDeath(Animal animal) {
        sumLifeTime += animal.getAge();
        deadAnimalsCount++;
    }

    public void updateStats(List<Animal> animals, WorldMap map, GrassPositionGenerator randomPG) {
        avgEnergy = 0;
        avgChildAmount = 0;
        if (!animals.isEmpty()) {
            for (Animal animal : animals) {
                avgChildAmount += animal.getChildrenCount();
                avgEnergy += animal.getEnergy();
            }
            avgChildAmount = avgChildAmount / animals.size();
            avgEnergy = avgEnergy / animals.size();
        }

        avgLifeTime = 0;
        if (deadAnimalsCount > 0) {
            avgLifeTime = (float) sumLifeTime / deadAnimalsCount;
        }

        updateFreeFields(map, randomPG);

        updateDominantGenomes();
    }

    private void updateFreeFields(WorldMap map, GrassPositionGenerator randomPG) {
        int result = 0;
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        for (Vector2d position : randomPG.getAllFreePositions()) {
            if (!placedAnimals.containsKey(position)) {
                result++;
            }
        }
        this.freeFields = result;
    }

    private void updateDominantGenomes() {
        if (genomeCount.isEmpty() || Collections.max(genomeCount.values()) < 2) {
            currDominantGenomes = Collections.emptySet();
            dominantAmount = 0;
            return;
        }
        dominantAmount = Collections.max(genomeCount.values());
        currDominantGenomes = genomeCount.entrySet().stream()
                .filter(e -> e.getValue() == dominantAmount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public SimulationStats makeStats(int day, int animalCount, int grassCount) {
        return new SimulationStats(
                avgChildAmount,
                avgEnergy,
                avgLifeTime,
                freeFields,
                day,
                animalCount,
                grassCount,
                dominantAmount,
                new HashSet<>(currDominantGenomes)
        );
    }
}