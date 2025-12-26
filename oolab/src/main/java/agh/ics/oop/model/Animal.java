package agh.ics.oop.model;

import java.util.Random;

public class Animal implements WorldElement {
    private Vector2d position;
    private MapDirection direction;
    private int energy;
    private final int[] genome;
    private int activeGene;


    public Animal(Vector2d position,int startEnergy, int[] genome) {
        this.position = position;
        this.energy = startEnergy;
        this.genome = genome;
        this.direction = MapDirection.values()[new Random().nextInt(8)];
        this.activeGene = genome[new Random().nextInt(genome.length)];
    }


    @Override
    public String toString() {
        return direction.toString();
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }


    public MapDirection getDirection() {
        return direction;
    }

    public Vector2d getPosition() {
        return position;
    }
    public void looseEnergy(int amount) {
        energy-= amount;
    }

    


}
