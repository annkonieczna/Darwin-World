package agh.ics.oop.model.map;

import java.util.List;
import java.util.Map;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.movement.MoveValidator;
import agh.ics.oop.model.movement.Vector2d;

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

    void registerGrowth(Vector2d position, boolean toxic);

    Map<Vector2d, List<Animal>> getAnimals();

    Map<Vector2d, Grass> getGrasses();

    Vector2d randomPositionFromMap();

    Boundary getBounds();

    Boundary getJungleBoundary();

    Map<Vector2d,Integer> getGoodGrassSpawnMap();

    Map<Vector2d,Integer> getToxicGrassSpawnMap();

}
