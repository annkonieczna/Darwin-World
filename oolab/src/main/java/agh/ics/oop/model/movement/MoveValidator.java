package agh.ics.oop.model.movement;

public interface MoveValidator {
    Movement correctPosition(Vector2d current, MapDirection direction);
}
