package agh.ics.oop.model;

import agh.ics.oop.model.util.RandomPositionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractWorldMap implements WorldMap {
    protected List<Vector2d> freeJunglePositions;
    protected List<Vector2d> freeSteppePositions;
    protected RandomPositionGenerator randomPG;

    protected Map<Vector2d, List<Animal>> animals;
    protected Map<Vector2d, Grass> grasses;

    @Override
    public void placeAnimal(Animal animal) {
        if (animals.containsKey(animal.getPosition())) {
            animals.get(animal.getPosition()).add(animal);
        } else {
            List<Animal> list = new ArrayList<Animal>();
            list.add(animal);
            animals.put(animal.getPosition(), list);
        }
    }

    @Override
    public void placeGrass(Grass grass) {
        grasses.put(grass.getPosition(), grass);
    }

    @Override
    public void move(Animal animal) {
        animals.get(animal.getPosition()).remove(animal);
        if (animals.get(animal.getPosition()).isEmpty()) {
            animals.remove(animal.getPosition());
        }
        animal.move(this);
        placeAnimal(animal);
    }
}
