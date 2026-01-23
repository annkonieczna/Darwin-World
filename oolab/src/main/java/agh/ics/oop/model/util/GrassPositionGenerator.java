package agh.ics.oop.model.util;

import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.movement.Vector2d;
import agh.ics.oop.model.map.Boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrassPositionGenerator implements ElementPositionGenerator {
    private final Random random = new Random();
    private final List<Vector2d> freeSteppePositions = new ArrayList<>();
    private final List<Vector2d> freeJunglePositions = new ArrayList<>();
    private final int jungleStart;
    private final int jungleEnd;
    private final int width;

    public GrassPositionGenerator(int width, int height) {
        this.width = width;
        this.jungleStart = (int) (height * 0.4);
        this.jungleEnd = (int) (height * 0.6);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Vector2d position = new Vector2d(x, y);
                if(isInJungle(position)){
                    freeJunglePositions.add(position);
                } else {
                    freeSteppePositions.add(position);
                }
            }
        }
    }

    //Returns random free from grass position with rule 80 - 20
    public Vector2d generateRandomPosition() {
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

    //Returns random position from list of Vector2d-s
    public Vector2d randomPositionFromList(List<Vector2d> list) {
        Vector2d position = list.get(random.nextInt(list.size()));
        list.remove(position);

        return position;
    }

    //Adds position back to free positions
    public void makePositionFree(Grass grass) {
        Vector2d position = grass.getPosition();
        if (isInJungle(position)) {
            freeJunglePositions.add(position);
        } else {
            freeSteppePositions.add(position);
        }
    }

    public boolean isInJungle(Vector2d position){
        return position.getY() >= this.jungleStart && position.getY() < this.jungleEnd;
    }

    //Getters
    public List<Vector2d> getAllFreePositions() {
        List<Vector2d> freePositions = new ArrayList<>(freeSteppePositions);
        freePositions.addAll(freeJunglePositions);
        return freePositions;
    }

    public Boundary getJungle() {
        return new Boundary(new Vector2d(0, jungleStart), new Vector2d(width-1, jungleEnd-1));
    }
}
