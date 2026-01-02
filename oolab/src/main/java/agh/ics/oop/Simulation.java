package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.GrassPositionGenerator;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.*;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final List<MapChangeListener> listeners = new ArrayList<>();
    private final WorldMap map;
    private final GrassPositionGenerator randomPG;
    private final Random random = new Random();

    private final SimulationConfig config;
    private int avgChildAmount;
    private int avgEnergy;
    private int avgLifeTime;
    private int avgLifeTimeCount;
    private int freeFields;
    private int day = 0;

    public Simulation(SimulationConfig config) {
        this.config = config;
        this.map = new EarthMap(config.width(), config.height());
        this.randomPG = new GrassPositionGenerator(config.width(), config.height());
        for (int i = 0; i < config.startAnimalAmount(); i++) {
            Animal animal = new Animal(
                    map.randomPositionFromMap(),
                    config.startEnergy(),
                    config.genomeLength()
            );
            animals.add(animal);
            map.placeAnimal(animal);
        }
        spawnGrasses(config.startGrassAmount());
    }

    public Simulation() {
        this(new SimulationConfig(
                10,
                10,
                0,
                5,
                555,
                10,
                3,
                100,
                10,
                1,
                2,
                2,
                1,
                3,
                10
        ));
    }

    public void registerListener(MapChangeListener listener){
        listeners.add(listener);
    }
    public void removeListener(MapChangeListener listener){
        listeners.remove(listener);
    }
    public void notifyListeners(){
        for(MapChangeListener listener : listeners){
            //!!! do dodania jeszcze przesył statystyk
            listener.mapChanged(map);
        }
    }

    @Override
    public void run() {
        notifyListeners();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        while (!Thread.currentThread().isInterrupted()) {
            removeDeadAnimals();
            moveAnimals();
            dinnerAnimals();
            reproduceAnimals();
            spawnGrasses(config.growingGrassAmount());
            updateStats();
            notifyListeners();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void removeDeadAnimals() {
        Iterator<Animal> iter = animals.iterator();
        while(iter.hasNext()){
            Animal animal = iter.next();
            if(animal.isDead()){
                animal.setDeathDay(day);
                avgLifeTimeCount += animal.getAge();
                deadAnimals.add(animal);
                map.removeAnimal(animal);
                iter.remove();
            }
        }

    }

    private void moveAnimals() {
        for (Animal animal : animals) {
            map.move(animal);
            animal.loseEnergy(config.moveEnergy());
        }
    }

    private void dinnerAnimals() {
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        Map<Vector2d, Grass> placedGrasses = map.getGrasses();
        for (Map.Entry<Vector2d, List<Animal>> field : placedAnimals.entrySet()) {
            Grass grass = placedGrasses.get(field.getKey());
            if (grass != null) {
                chooseBestAnimals(field.getValue(), 1).get(0).eatGrass(config.energyFromGrass(), grass.isToxic());
                map.removeGrass(grass);
                randomPG.makePositionFree(grass);
            }
        }
    }

    private void reproduceAnimals() {
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        List<Animal> newAnimals = new ArrayList<>();
        for (Map.Entry<Vector2d, List<Animal>> field : placedAnimals.entrySet()) {
            if (field.getValue().size() > 1) {
                List<Animal> toReproduce = chooseBestAnimals(field.getValue(), 2);
                if (toReproduce.get(1).getEnergy() >= config.minimumEnergyForReproduction()) {
                    Animal child = toReproduce.get(0).reproduceWith(
                            toReproduce.get(1),
                            config.minMutation(),
                            config.maxMutation(),
                            config.reproductionEnergyCost()
                    );
                    newAnimals.add(child);
                    map.placeAnimal(child);
                }
            }
        }
        animals.addAll(newAnimals);
    }

    private List<Animal> chooseBestAnimals(List<Animal> allAnimals, int count) {
        if (allAnimals.isEmpty()) {
            return Collections.emptyList();
        }

        List<Animal> result = new ArrayList<>(allAnimals);
        Collections.shuffle(result);
        Collections.sort(result, (a,b) ->{
            if (a.getEnergy() > b.getEnergy()) return -1;
            if (a.getEnergy() < b.getEnergy()) return 1;
            if (a.getAge() > b.getAge()) return -1;
            if (a.getAge() < b.getAge()) return 1;
            if (a.getChildrenCount() > b.getChildrenCount()) return -1;
            if (a.getChildrenCount() < b.getChildrenCount()) return 1;
            return 0;
        });

        return result.subList(0, Math.min(count, result.size()));
    }

    private void spawnGrasses(int amount) {
        for (int i = 0; i < amount; i++) {
            Vector2d position = randomPG.generateRandomPosition();
            if (position != null) {
                Grass grass = new Grass(
                        position,
                        random.nextInt(100) < config.toxicGrassChance()
                );
                map.placeGrass(grass);
            }
        }
    }

    private void updateStats() {
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
        if (!deadAnimals.isEmpty()) {
            avgLifeTime = avgLifeTimeCount / deadAnimals.size();
        }

        freeFields = countFreeFields();

        day++;
    }

    private int countFreeFields() {
        int result = 0;
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        for(Vector2d position : randomPG.getAllFreePositions()){
            if(!placedAnimals.containsKey(position)){
                result++;
            }
        }
        return result;
    }

    public WorldMap getWorldMap() {
        return map;
    }
}
