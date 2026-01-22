package agh.ics.oop.model.map;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.movement.MapDirection;
import agh.ics.oop.model.movement.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractWorldMapTest {
    private final Boundary jungle = new Boundary(new Vector2d(0, 0), new Vector2d(0, 0));

    @Test
    void placeAndRemoveAnimalTest() {
        EarthMap map = new EarthMap(10, 10, jungle);
        Animal a1 = new Animal(new Vector2d(2, 2), 20, List.of(0), List.of(0), 100);
        Animal a2 = new Animal(new Vector2d(2, 2), 30, List.of(0), List.of(0), 100);

        map.placeAnimal(a1);
        map.placeAnimal(a2);

        assertEquals(2, map.getAnimals().get(new Vector2d(2, 2)).size(), "Should have 2 animals on same field");

        map.removeAnimal(a1);
        assertEquals(1, map.getAnimals().get(new Vector2d(2, 2)).size());

        map.removeAnimal(a2);
        assertNull(map.getAnimals().get(new Vector2d(2, 2)), "Entry should be removed from map when empty");
    }

    @Test
    void grassManagementTest() {
        EarthMap map = new EarthMap(10, 10, jungle);
        Grass g = new Grass(new Vector2d(5, 5), false);

        map.placeGrass(g);
        assertTrue(map.getGrasses().containsKey(new Vector2d(5, 5)));

        map.removeGrass(g);
        assertFalse(map.getGrasses().containsKey(new Vector2d(5, 5)));
    }

    @Test
    void moveUpdatesAnimalPositionInMapTest() {
        EarthMap map = new EarthMap(10, 10, jungle);
        Vector2d startPos = new Vector2d(1, 1);
        Animal a = new Animal(startPos, 20, List.of(0, 0, 0), List.of(0, 0, 0), 100);

        MapDirection startDir = a.getDirection();
        Vector2d endPos = startPos.add(startDir.toUnitVector());

        map.placeAnimal(a);
        map.move(a);

        assertNull(map.getAnimals().get(startPos), "Old position should be empty");
        assertNotNull(map.getAnimals().get(endPos), "New position should contain animal");
        assertEquals(endPos, a.getPosition());
    }
}