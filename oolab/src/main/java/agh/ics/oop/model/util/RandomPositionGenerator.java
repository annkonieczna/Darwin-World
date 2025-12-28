package agh.ics.oop.model.util;

import agh.ics.oop.model.Grass;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.WorldMap;

import java.util.List;
import java.util.Random;

public class RandomPositionGenerator {
    private Random random = new Random();
    private List<Vector2d> freeSteppePositions;
    private List<Vector2d> freeJunglePositions;
    private WorldMap map;

    public RandomPositionGenerator(List<Vector2d> freeSteppePositions, List<Vector2d> freeJunglePositions, WorldMap map) {
        this.freeSteppePositions = freeSteppePositions;
        this.freeJunglePositions = freeJunglePositions;
        this.map = map;
    }

    public void spawnGrass(int amount) {
        for (int i = 0; i < amount; i++) {
            Vector2d position = null;
            if (random.nextInt(100) < 20) {
                if (freeSteppePositions.isEmpty()) {
                    if (!freeJunglePositions.isEmpty()) {
                        position = randomPositionFromList(freeJunglePositions);
                    }
                } else {
                    position = randomPositionFromList(freeSteppePositions);
                }
            } else {
                if (freeJunglePositions.isEmpty()) {
                    if (!freeSteppePositions.isEmpty()) {
                        position = randomPositionFromList(freeSteppePositions);
                    }
                } else {
                    position = randomPositionFromList(freeJunglePositions);
                }
            }
            if (position != null) {
                map.place(new Grass(position, false));
            }
        }
    }

    public Vector2d randomPositionFromList(List<Vector2d> list) {
        Vector2d position = list.get(random.nextInt(list.size()));
        list.remove(position);

        return position;
    }

    public void removePositionFromList(List<Vector2d> list, Vector2d position) {

    }

}
