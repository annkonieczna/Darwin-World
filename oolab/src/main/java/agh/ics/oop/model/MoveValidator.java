package agh.ics.oop.model;

import agh.ics.oop.model.util.Movement;

public interface MoveValidator {
    Movement correctPosition(Vector2d current, MapDirection direction);
}
