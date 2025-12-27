package agh.ics.oop.model;

public record Grass(Vector2d position) implements WorldElement {

    @Override
    public String toString() {
        return "*";

    }

}