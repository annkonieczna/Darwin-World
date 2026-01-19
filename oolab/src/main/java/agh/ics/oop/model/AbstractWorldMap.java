package agh.ics.oop.model;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    protected Map<Vector2d, List<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, Grass> grasses = new HashMap<>();

    @Override
    public void placeAnimal(Animal animal) {
        if (animals.containsKey(animal.getPosition())) {
            animals.get(animal.getPosition()).add(animal);
        } else {
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            animals.put(animal.getPosition(), list);
        }
    }

    @Override
    public void placeGrass(Grass grass) {
        grasses.put(grass.getPosition(), grass);
    }

    @Override
    public void removeAnimal(Animal animal) {
        if (animals.containsKey(animal.getPosition())) {
            animals.get(animal.getPosition()).remove(animal);
            if (animals.get(animal.getPosition()).isEmpty()) {
                animals.remove(animal.getPosition());
            }
        }
    }

    @Override
    public void removeGrass(Grass grass) {
        grasses.remove(grass.getPosition());
    }

    @Override
    public void move(Animal animal) {
        if (animals.get(animal.getPosition()) != null) {
            animals.get(animal.getPosition()).remove(animal);
            if (animals.get(animal.getPosition()).isEmpty()) {
                animals.remove(animal.getPosition());
            }
            animal.move(this);
            placeAnimal(animal);
        }
    }

    @Override
    public Map<Vector2d, List<Animal>> getAnimals() {
        // !!! zmianiłem z Map.copyof (zwiększa zdecydowanie wydajność, może zmniejsza bezpieczeństko idk, idc XD, nie no, do obgadania)
        return this.animals;
    }

    @Override
    public Map<Vector2d, Grass> getGrasses() {
        return this.grasses;
    }
}
