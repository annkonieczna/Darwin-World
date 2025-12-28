package agh.ics.oop.model.util;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Grass;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.WorldMap;

import java.util.List;
import java.util.Random;

public class RandomPositionGenerator {
    private Random random = new Random();
    private List<Vector2d> freeSteppePositions;
    private List<Vector2d> freeJunglePositions;
    private int jungleStart;
    private int jungleEnd;

    public RandomPositionGenerator(int width, int height) {
        this.jungleStart = (int) (height * 0.4);
        this.jungleEnd = (int) (height * 0.6);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Vector2d position = new Vector2d(x, y);
                if(y >= this.jungleStart && y < this.jungleEnd){
                    freeJunglePositions.add(position);
                } else {
                    freeSteppePositions.add(position);
                }
            }
        }
    }

    public Vector2d randomPositionGrass() {
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

        return position;
    }

    public Vector2d randomPositionFromList(List<Vector2d> list) {
        Vector2d position = list.get(random.nextInt(list.size()));
        list.remove(position);

        return position;
    }

    public Vector2d randomPositionFromBounds(Boundary boundary) {
        Vector2d position = new Vector2d(random.nextInt(boundary.lowerLeft().getX(),boundary.upperRight().getX()+1),
                random.nextInt(boundary.lowerLeft().getY(),boundary.upperRight().getY())+1);
        return position;
    }

}
