package agh.ics.oop.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import agh.ics.oop.model.util.Boundary;

/**
 * The interface responsible for interacting with the map of the world.
 *
 */
public interface WorldMap extends MoveValidator {

    void move(Animal animal);

    void placeAnimal(Animal animal);

    void placeGrass(Grass grass);

    void removeAnimal(Animal animal);

    void removeGrass(Grass grass);

    boolean inBounds(Vector2d position);

    Map<Vector2d, List<Animal>> getAnimals();

    Map<Vector2d, Grass> getGrasses();

    Vector2d randomPositionFromMap();

    Boundary getBounds();

    Boundary getJungleBoundary();

    UUID getId();
}
