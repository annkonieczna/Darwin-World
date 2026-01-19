package agh.ics.oop.model;

import agh.ics.oop.model.util.Movement;

import java.util.List;
import java.util.Random;

public class Animal implements WorldElement {
    private Vector2d position;
    private MapDirection direction;
    private int energy;
    private int maxEnergy;
    private final List<Integer> genome;
    private final int resistanceScore;
    private int activeGeneIndex;

    private int plantsEaten = 0;
    private int age = 0;
    private int childrenCount = 0;
    private Integer deathDay = null;

    private static final Random random = new Random();

    public Animal(Vector2d position, int startEnergy, List<Integer> genome, List<Integer> pattern, int maxEnergy) {
        this.position = position;
        this.energy = startEnergy;
        this.genome = genome;
        this.direction = MapDirection.values()[random.nextInt(8)];
        this.activeGeneIndex = random.nextInt(genome.size());
        this.resistanceScore = Genome.calculateResistanceScore(genome, pattern);
        this.maxEnergy = maxEnergy;
    }

    public Animal(Vector2d position, int startEnergy, int genomeLength, List<Integer> pattern, int maxEnergy) {
        this.position = position;
        this.energy = startEnergy;
        this.genome = Genome.generate(genomeLength);
        this.direction = MapDirection.values()[random.nextInt(8)];
        this.activeGeneIndex = random.nextInt(genome.size());
        this.resistanceScore = Genome.calculateResistanceScore(genome, pattern);
        this.maxEnergy = maxEnergy;
    }

    @Override
    public String toString() {
        return String.valueOf(energy);
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public void loseEnergy(int amount) {
        energy -= amount;
    }

    public void eatGrass(int amount, boolean isToxic) {
        if (isToxic) {
            int damage = amount * (100 - resistanceScore) / 100;
            energy -= damage;

        } else {
            energy += amount;
            energy = Math.min(energy, maxEnergy);
        }
        plantsEaten += 1;
    }

    public boolean isDead() {
        return energy <= 0;
    }

    public void setDeathDay(int day) {
        if (deathDay == null) {
            deathDay = day;
        }
    }

    public void move(MoveValidator validator) {
        int activeGene = genome.get(activeGeneIndex);
        direction = direction.rotate(activeGene);

        Movement movement = validator.correctPosition(position, direction);
        position = movement.position();
        direction = movement.direction();

        activeGeneIndex = (activeGeneIndex + 1) % genome.size();
        age++;

    }

    public Animal reproduceWith(Animal partner, int min, int max, int energyCost, List<Integer> pattern) {

        List<Integer> childGenome = Genome.combine(this.genome, partner.genome, this.energy, partner.energy, min, max);

        this.energy -= energyCost;
        partner.energy -= energyCost;

        this.childrenCount++;
        partner.childrenCount++;
        return new Animal(this.position, energyCost * 2, childGenome, pattern, this.maxEnergy);
    }

    //getters
    @Override
    public Vector2d getPosition() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public List<Integer> getGenome() {
        return genome;
    }

    public int getActiveGeneIndex() {
        return activeGeneIndex;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public Integer getDeathDay() {
        return deathDay;
    }

    public int getMaxEnergy() { return maxEnergy; }

    public int getPlantsEaten() {
        return plantsEaten;
    }

    public void setPlantsEaten(int plantsEaten) {
        this.plantsEaten = plantsEaten;
    }
}


