package agh.ics.oop.model.util;

import agh.ics.oop.model.Grass;
import agh.ics.oop.model.Vector2d;

import java.util.List;

public interface ElementPositionGenerator {
    Vector2d generateRandomPosition();
    void makePositionFree(Grass grass);
    List<Vector2d> getAllFreePositions();
}
