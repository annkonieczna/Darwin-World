package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.GrassPositionGenerator;
import agh.ics.oop.model.util.SimulationConfig;
import agh.ics.oop.model.util.SimulationStats;

import java.util.*;
import java.util.stream.Collectors;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final List<MapChangeListener> listeners = new ArrayList<>();
    private final Map<List<Integer>, Integer> genomeCount = new HashMap<>();
    private Set<List<Integer>> currDominantGenomes = new HashSet<>();
    private final WorldMap map;
    private final GrassPositionGenerator randomPG;
    private final Random random = new Random();

    private final SimulationConfig config;
    private boolean running = true;
    private int runningSpeed = 500;

    private int avgChildAmount;
    private int avgEnergy;
    private int avgLifeTime;
    private int avgLifeTimeCount;
    private int freeFields;
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
            registerGenome(animal);
            updateDominantGenomes();
            animalCount++;
        }
        spawnGrasses(config.startGrassAmount());
    }

    //for tests
    public Simulation() {
        this(new SimulationConfig(
                10,
                10,
                0,
                4,
                5,
                10,
                3,
                100,
                10,
                1,
                2,
                2,
                1,
                3,
                10,
                100
        ));
    }

    public void registerListener(MapChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MapChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners() {
        for (MapChangeListener listener : listeners) {
            listener.statsChanged(new SimulationStats(
                    avgChildAmount,
                    avgEnergy,
                    avgLifeTime,
                    freeFields,
                    day,
                    animalCount,
                    grassCount,
                    currDominantGenomes));
            listener.mapChanged(map);
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
                synchronized (map) {
                    removeDeadAnimals();
                    moveAnimals();
                    dinnerAnimals();
                    reproduceAnimals();
                    spawnGrasses(config.growingGrassAmount());
                    updateStats();
                }
                notifyListeners();
            }
        }
    }

    private void removeDeadAnimals() {
        Iterator<Animal> iter = animals.iterator();
        while (iter.hasNext()) {
            Animal animal = iter.next();
            if (animal.isDead()) {
                animal.setDeathDay(day);
                avgLifeTimeCount += animal.getAge();
                deadAnimals.add(animal);
                map.removeAnimal(animal);
                unregisterGenome(animal);
                animalCount--;
                iter.remove();
            }
        }

    }

    private void moveAnimals() {
        for (Animal animal : animals) {
            map.move(animal);
            animal.loseEnergy(config.moveEnergyCost());
        }
    }

    private void dinnerAnimals() {
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        Map<Vector2d, Grass> placedGrasses = map.getGrasses();
        for (Map.Entry<Vector2d, List<Animal>> field : placedAnimals.entrySet()) {
            Grass grass = placedGrasses.get(field.getKey());
            if (grass != null) {
                int amount = grass.isToxic() ? config.energyFromToxicGrass() : config.energyFromGrass();
                chooseBestAnimals(field.getValue(), 1).get(0).eatGrass(amount, grass.isToxic());
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
                    registerGenome(child);
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
                Grass grass = new Grass(
                        position,
                        random.nextInt(100) < config.toxicGrassChance()
                );
                map.placeGrass(grass);
                grassCount++;
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
        updateDominantGenomes();

        day++;
    }

    private int countFreeFields() {
        int result = 0;
        Map<Vector2d, List<Animal>> placedAnimals = map.getAnimals();
        for (Vector2d position : randomPG.getAllFreePositions()) {
            if (!placedAnimals.containsKey(position)) {
                result++;
            }
        }
        return result;
    }

    private void registerGenome(Animal animal) {
        List<Integer> key = animal.getGenome();
        genomeCount.put(key, genomeCount.getOrDefault(key, 0) + 1);
    }

    private void unregisterGenome(Animal animal) {
        List<Integer> key = animal.getGenome();
        int count = genomeCount.getOrDefault(key, 0);
        if (count <= 1) {
            genomeCount.remove(key);
        } else {
            genomeCount.put(key, count - 1);
        }

    }

    private void updateDominantGenomes() {
        if (genomeCount.isEmpty()) {
            currDominantGenomes = Collections.emptySet();
            return;
        }
        int maxCount = Collections.max(genomeCount.values());
        currDominantGenomes = genomeCount.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .limit(5)
                .collect(Collectors.toSet());

    }

    public WorldMap getWorldMap() {
        return map;
    }

    public boolean getRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setRunningSpeed(int speed) {
        runningSpeed = speed;
    }
}
