package agh.ics.oop.model;

import java.util.Random;

public class Animal implements WorldElement {
    private Vector2d position;
    private MapDirection direction;
    private int energy;
    private final int[] genome;
    private int activeGeneIndex;

    private int plantsEaten = 0;
    private int age = 0;

    public Animal(Vector2d position, int startEnergy, int[] genome) {
        this.position = position;
        this.energy = startEnergy;
        this.genome = genome;
        this.direction = MapDirection.values()[new Random().nextInt(8)];
        this.activeGeneIndex = new Random().nextInt(genome.length);
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

    public void loseEnergy(int amount) {
        energy -= amount;
    }

    public void eatGrass(int amount) {
        energy += amount;
        plantsEaten += 1;
    }

    public int getEnergy() {
        return energy;
    }

    public void move(WorldMap map) {
        int activeGene = genome[activeGeneIndex];
        direction = direction.rotate(activeGene);

        Vector2d moveVector = direction.toUnitVector();
//      position =

        activeGeneIndex = (activeGeneIndex + 1) % genome.length;
        age++;

    }

}
