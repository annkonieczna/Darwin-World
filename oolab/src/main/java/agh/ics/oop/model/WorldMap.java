package agh.ics.oop.model;

import java.util.List;
import java.util.UUID;

import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.IncorrectPositionException;

/**
 * The interface responsible for interacting with the map of the world.
 *
 */
public interface WorldMap extends MoveValidator {

    void move(Animal animal);

    void placeAnimal(Animal animal);

    void placeGrass(Grass grass);

    boolean inBounds(Vector2d position);

    Boundary getBounds();

    UUID getId();
}
