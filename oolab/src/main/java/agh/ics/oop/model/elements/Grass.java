package agh.ics.oop.model.elements;

import agh.ics.oop.model.movement.Vector2d;

public class Grass implements WorldElement {
    private final Vector2d position;
    private final boolean toxic;

    public Grass(Vector2d position, boolean toxic) {
        this.position = position;
        this.toxic = toxic;
    }

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public boolean isToxic() {
        return toxic;
    }
}
