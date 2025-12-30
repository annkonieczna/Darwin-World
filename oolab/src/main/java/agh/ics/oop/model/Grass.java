package agh.ics.oop.model;

public class Grass implements WorldElement {
    private final Vector2d position;
    private final boolean toxic;

    public Grass(Vector2d position, boolean toxic) {
        this.position = position;
        this.toxic = toxic;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public boolean isToxic() {
        return toxic;
    }
}
