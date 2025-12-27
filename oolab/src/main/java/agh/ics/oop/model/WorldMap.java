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

    /**
     * Place a new animal on the map.
     *
     * @param animal The animal to be placed on the map.
     */
    void place(Animal animal) throws IncorrectPositionException;


    /**
     * Return true if given position on the map is occupied. Should not be
     * confused with canMoveTo since there might be empty positions where the animal
     * cannot move.
     *
     * @param position Position to check.
     * @return True if the position is occupied.
     */
    boolean isOccupied(Vector2d position);

    Vector2d correctPosition(Vector2d current,Vector2d moveVector);

    Boundary getBounds();

    UUID getId();
}
