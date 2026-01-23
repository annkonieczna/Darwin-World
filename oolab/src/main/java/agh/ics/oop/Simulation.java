package agh.ics.oop;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.elements.Genome;
import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.map.EarthMap;
import agh.ics.oop.model.movement.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.listeners.MapChangeListener;
import agh.ics.oop.model.listeners.StatsChangeListener;
import agh.ics.oop.model.stats.StatsManager;
import agh.ics.oop.model.util.GrassPositionGenerator;
import agh.ics.oop.model.stats.SimulationConfig;
import agh.ics.oop.model.stats.SimulationStats;

import java.util.*;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final List<MapChangeListener> mapListeners = new ArrayList<>();
    private final List<StatsChangeListener> statsListeners = new ArrayList<>();

    private final StatsManager statsManager = new StatsManager();

    private final WorldMap map;
    private final GrassPositionGenerator randomPG;
    private final Random random = new Random();
    private final UUID simID = UUID.randomUUID();

    private final SimulationConfig config;
    private boolean running = true;
    private int runningSpeed = 500;

    private int day = 0;
    private int animalCount;
    private int grassCount;
    private final List<Integer> resistancePattern;

    public Simulation(SimulationConfig config) {
        this.config = config;
        this.randomPG = new GrassPositionGenerator(config.width(), config.height());
        this.resistancePattern = Genome.generate(config.genomeLength());
        this.map = new EarthMap(config.width(), config.height(), this.randomPG.getJungle());

        for (int i = 0; i < config.startAnimalAmount(); i++) {
            Animal animal = new Animal(
                    map.randomPositionFromMap(),
                    config.startEnergy(),
                    config.genomeLength(),
                    resistancePattern,
                    config.maxAnimalEnergy()
            );
            animals.add(animal);
            map.placeAnimal(animal);

            statsManager.registerGenome(animal);
            animalCount++;
        }
        statsManager.updateStats(animals, map, randomPG);
        spawnGrasses(config.startGrassAmount());
    }


    public void registerMapListener(MapChangeListener mapListener) {
        mapListeners.add(mapListener);
    }

    public void registerStatsListener(StatsChangeListener statsListener) {
        statsListeners.add(statsListener);
    }

    public synchronized void notifyListeners() {
        SimulationStats stats = statsManager.makeStats(day, animalCount, grassCount);

        for (StatsChangeListener listener : statsListeners) {
            listener.statsChanged(stats);
        }
        for (MapChangeListener listener : mapListeners) {
            listener.mapChanged();
        }
    }

    @Override
    public void run() {
        notifyListeners();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(runningSpeed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (running) {
                doDay();
            }
        }
    }

    void doDay() {
        synchronized (map) {
            removeDeadAnimals();
            moveAnimals();
            dinnerAnimals();
            reproduceAnimals();
            loseEnergy();
            spawnGrasses(config.growingGrassAmount());
            statsManager.updateStats(animals, map, randomPG);
            day++;

            notifyListeners();
        }
    }

    private void removeDeadAnimals() {
        Iterator<Animal> iter = animals.iterator();
        while (iter.hasNext()) {
            Animal animal = iter.next();
            if (animal.isDead()) {
                animal.setDeathDay(day);

                statsManager.registerDeath(animal);
                statsManager.unregisterGenome(animal);

                map.removeAnimal(animal);
                animalCount--;
                iter.remove();
            }
        }
    }
    private void loseEnergy() {
        for(Animal animal: animals) {
            animal.loseEnergy(config.moveEnergyCost());
        }
    }

    private void moveAnimals() {
        for (Animal animal : animals) {
            map.move(animal);
        }
    }

    private void dinnerAnimals() {
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        Map<Vector2d, Grass> placedGrasses = map.getGrasses();
        for (Map.Entry<Vector2d, List<Animal>> field : placedAnimals.entrySet()) {
            Grass grass = placedGrasses.get(field.getKey());
            if (grass != null) {
                int amount = grass.isToxic() ? config.energyFromToxicGrass() : config.energyFromGrass();
                chooseBestAnimals(field.getValue(), 1).getFirst().eatGrass(amount, grass.isToxic());
                map.removeGrass(grass);
                randomPG.makePositionFree(grass);
                grassCount--;
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
                            config.reproductionEnergyCost(),
                            resistancePattern
                    );
                    newAnimals.add(child);
                    map.placeAnimal(child);

                    statsManager.registerGenome(child);

                    animalCount++;
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
        Collections.sort(result, (a, b) -> {
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
                boolean toxic = random.nextInt(100) < config.toxicGrassChance() && config.toxicOn();
                Grass grass = new Grass(position, toxic);
                map.placeGrass(grass);
                grassCount++;
                map.registerGrowth(position, toxic);
            }
        }
    }

    public WorldMap getWorldMap() { return map; }
    public boolean getRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    public void setRunningSpeed(int speed) { runningSpeed = speed; }
    public UUID getSimID() { return simID; }
}