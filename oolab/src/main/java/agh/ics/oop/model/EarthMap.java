package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.Movement;

import java.util.UUID;

public class EarthMap extends AbstractWorldMap{
    private final Boundary bounds;
    private final String mapID = UUID.randomUUID().toString();

    public EarthMap(int width, int height) {
        this.bounds = new Boundary(new Vector2d(0,0), new Vector2d(width-1,height-1));
    }

    @Override
    public Movement correctPosition(Vector2d current, MapDirection direction) {
        Vector2d newPosition = current.add(direction.toUnitVector());
        int x = newPosition.getX();
        int y = newPosition.getY();

        if (inBounds(newPosition)) {
            return new Movement(newPosition, direction);
        }

        if (y < bounds.lowerLeft().getY() || y > bounds.upperRight().getY()) {
            direction = direction.rotate(4);
            y = current.getY();
        }

        if (x < bounds.lowerLeft().getX()) {
            x = bounds.upperRight().getX();
        } else if (x > bounds.upperRight().getX()) {
            x = bounds.lowerLeft().getX();
        }

        return new Movement(new Vector2d(x,y), direction);
    }

    @Override
    public boolean inBounds(Vector2d position) {
        return (position.follows(bounds.lowerLeft()) && position.precedes(bounds.upperRight()));
    }

    @Override
    public Boundary getBounds() {
        return bounds;
    }

    @Override
    public UUID getId() {
        return null;
    }
}
