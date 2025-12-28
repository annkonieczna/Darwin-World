package agh.ics.oop;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.EarthMap;
import agh.ics.oop.model.Grass;
import agh.ics.oop.model.WorldMap;
import agh.ics.oop.model.util.RandomPositionGenerator;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private final RandomPositionGenerator randomPG;
    private final Random random = new Random();

    public Simulation(SimulationConfig config) {
        this.map = new EarthMap(config.width(), config.height());
        this.randomPG = new RandomPositionGenerator(config.width(), config.height());
        for (int i = 0; i < config.startAnimalAmount(); i++) {
            Animal animal = new Animal(
                    randomPG.randomPositionFromBounds(map.getBounds()),
                    config.startEnergy(),
                    config.genomeLength()
            );
            animals.add(animal);
            map.placeAnimal(animal);
        }
        for (int i = 0; i < config.startGrassAmount(); i++) {
            Grass grass = new Grass(
                    randomPG.randomPositionGrass(),
                    random.nextInt(100) < config.toxicGrassChance()
            );
            map.placeGrass(grass);
        }
    }

    @Override
    public void run() {
        //1.usuwanie martwych zwierzątek
        //2.skręt,ruch
        //3.obiad
        //4.sex
        //5.spawn roślinek
    }

    public void removeDeadAnimals() {
        animals.removeIf(animal -> animal.isDead());
    }
}
