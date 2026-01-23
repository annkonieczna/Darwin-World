package agh.ics.oop.model.map;

import agh.ics.oop.model.movement.MapDirection;
import agh.ics.oop.model.movement.Movement;
import agh.ics.oop.model.movement.Vector2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EarthMap extends AbstractWorldMap {
    private final Boundary bounds;
    private final Random random = new Random();
    private final Boundary jungleBoundary;
    private final Map<Vector2d, Integer> goodGrassSpawnMap = new HashMap<>();
    private final Map<Vector2d, Integer> toxicGrassSpawnMap = new HashMap<>();

    public EarthMap(int width, int height, Boundary jungleBoundary) {
        this.bounds = new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
        this.jungleBoundary = jungleBoundary;
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

        return new Movement(new Vector2d(x, y), direction);
    }

    @Override
    public Vector2d randomPositionFromMap() {
        return new Vector2d(random.nextInt(bounds.lowerLeft().getX(), bounds.upperRight().getX() + 1),
                random.nextInt(bounds.lowerLeft().getY(), bounds.upperRight().getY()) + 1);
    }

    @Override
    public boolean inBounds(Vector2d position) {
        return (position.follows(bounds.lowerLeft()) && position.precedes(bounds.upperRight()));
    }

    @Override
    public void registerGrowth(Vector2d position, boolean toxic) {
        if (toxic) {
            toxicGrassSpawnMap.merge(position, 1, Integer::sum);
        } else {
            goodGrassSpawnMap.merge(position, 1, Integer::sum);
        }
    }


    @Override
    public Boundary getBounds() {
        return bounds;
    }

    public Boundary getJungleBoundary() {
        return jungleBoundary;
    }

    @Override
    public Map<Vector2d, Integer> getGoodGrassSpawnMap() {
        return Collections.unmodifiableMap(goodGrassSpawnMap);
    }

    @Override
    public Map<Vector2d, Integer> getToxicGrassSpawnMap() {
        return Collections.unmodifiableMap(toxicGrassSpawnMap);
    }
}
