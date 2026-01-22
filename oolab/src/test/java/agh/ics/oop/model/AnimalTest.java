package agh.ics.oop.model;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.movement.MoveValidator;
import agh.ics.oop.model.movement.Vector2d;
import agh.ics.oop.model.movement.Movement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void toStringReturnsEnergy() {
        Animal a = new Animal(new Vector2d(0, 0), 42, List.of(0, 1), List.of(0, 0), 100);
        assertEquals("42", a.toString());
    }

    @Test
    void isAtUsesPositionEquals() {
        Animal a = new Animal(new Vector2d(2, 3), 10, List.of(0, 1), List.of(0, 0), 100);
        assertTrue(a.isAt(new Vector2d(2, 3)));
        assertFalse(a.isAt(new Vector2d(2, 4)));
    }

    @ParameterizedTest
    @CsvSource({
            "20, 5, 15",
            "0,  1, -1",
            "10, 10, 0"
    })
    void loseEnergyDecreasesEnergy(int start, int amount, int expected) {
        Animal a = new Animal(new Vector2d(0, 0), start, List.of(0, 1), List.of(0, 0), 100);
        a.loseEnergy(amount);
        assertEquals(expected, a.getEnergy());
    }

    @Test
    void eatGrassNonToxicTest() {
        Animal a = new Animal(new Vector2d(0, 0), 9, List.of(0, 1), List.of(0, 0), 10);

        a.eatGrass(5, false);

        assertEquals(10, a.getEnergy(), "Energy should be limited to maxEnergy");
        assertEquals(1, a.getPlantsEaten());
    }

    @Test
    void eatGrassToxicGrassTest() {
        // Resistance = 50%
        List<Integer> genome = List.of(0, 1);
        List<Integer> pattern = List.of(0, 0);

        Animal a = new Animal(new Vector2d(0, 0), 20, genome, pattern, 100);

        // damage- 5
        a.eatGrass(10, true);

        assertEquals(15, a.getEnergy());
        assertEquals(1, a.getPlantsEaten());
    }

    @ParameterizedTest
    @CsvSource({
            "1, false",
            "0, true",
            "-5, true"
    })
    void isDeadWhenEnergyBelow0(int energy, boolean expected) {
        Animal a = new Animal(new Vector2d(0, 0), energy, List.of(0, 1), List.of(0, 0), 100);
        assertEquals(expected, a.isDead());
    }

    @Test
    void setDeathDaySetsOnlyOnce() {
        Animal a = new Animal(new Vector2d(0, 0), 10, List.of(0, 1), List.of(0, 0), 100);

        a.setDeathDay(5);
        a.setDeathDay(99);

        assertEquals(5, a.getDeathDay());
    }

    @Test
    void moveChangesPositionTest() {
        // Genom len- 3
        Animal a = new Animal(new Vector2d(1, 1), 10, List.of(0, 0, 0), List.of(0, 0, 0), 100);

        int beforeAge = a.getAge();
        int beforeIdx = a.getActiveGeneIndex();

        MoveValidator validator = (pos, dir) -> new Movement(new Vector2d(5, 6), dir);

        a.move(validator);

        assertEquals(new Vector2d(5, 6), a.getPosition());
        assertEquals(beforeAge + 1, a.getAge());
        assertEquals((beforeIdx + 1) % a.getGenome().size(), a.getActiveGeneIndex());
    }

    @Test
    void reproduceWithTest() {
        List<Integer> pattern = List.of(0, 0, 0, 0);

        Animal a = new Animal(new Vector2d(2, 2), 50, List.of(1, 1, 1, 1), pattern, 100);
        Animal b = new Animal(new Vector2d(2, 2), 30, List.of(2, 2, 2, 2), pattern, 100);

        int energyCost = 10;

        Animal child = a.reproduceWith(b, 0, 0, energyCost, pattern);

        assertEquals(40, a.getEnergy());
        assertEquals(20, b.getEnergy());
        assertEquals(1, a.getChildrenCount());
        assertEquals(1, b.getChildrenCount());

        assertEquals(new Vector2d(2, 2), child.getPosition());
        assertEquals(energyCost * 2, child.getEnergy());
        assertEquals(100, child.getMaxEnergy(), "Child should get maxEnergy from parent 'this'");
        assertEquals(4, child.getGenome().size(), "Genome lengt should be same as its parents");
    }

    @Test
    void setPlantsEatenOverwritesCounter() {
        Animal a = new Animal(new Vector2d(0, 0), 10, List.of(0, 1), List.of(0, 0), 100);
        a.eatGrass(1, false);
        a.eatGrass(1, false);
        assertEquals(2, a.getPlantsEaten());

        a.setPlantsEaten(7);
        assertEquals(7, a.getPlantsEaten());
    }
}

