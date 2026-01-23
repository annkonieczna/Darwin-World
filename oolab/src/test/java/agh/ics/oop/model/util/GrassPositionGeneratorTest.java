package agh.ics.oop.model.util;

import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.map.Boundary;
import agh.ics.oop.model.movement.Vector2d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GrassPositionGeneratorTest {
    @ParameterizedTest
    @CsvSource({
            "5, 4, true",
            "5, 5, true",
            "5, 6, false",
            "5, 3, false"
    })
    void isInJungleTest(int x, int y, boolean expected) {
        GrassPositionGenerator generator = new GrassPositionGenerator(10, 10);
        assertEquals(expected, generator.isInJungle(new Vector2d(x, y)));
    }

    @Test
    void getJungleReturnsCorrectBoundary() {
        GrassPositionGenerator generator = new GrassPositionGenerator(10, 10);
        Boundary expected = new Boundary(new Vector2d(0, 4), new Vector2d(9, 5));

        assertEquals(expected.lowerLeft(), generator.getJungle().lowerLeft());
        assertEquals(expected.upperRight(), generator.getJungle().upperRight());
    }

    @Test
    void generatePositionRemovesItFromFreePositions() {
        GrassPositionGenerator generator = new GrassPositionGenerator(2, 10);
        int initialSize = generator.getAllFreePositions().size();

        Vector2d pos = generator.generateRandomPosition();

        assertNotNull(pos);
        assertEquals(initialSize - 1, generator.getAllFreePositions().size());
        assertFalse(generator.getAllFreePositions().contains(pos));
    }

    @Test
    void makePositionFreeAddsItBack() {
        GrassPositionGenerator generator = new GrassPositionGenerator(10, 10);
        Vector2d pos = generator.generateRandomPosition();
        int sizeAfterGeneration = generator.getAllFreePositions().size();

        generator.makePositionFree(new Grass(pos, false));

        assertEquals(sizeAfterGeneration + 1, generator.getAllFreePositions().size());
        assertTrue(generator.getAllFreePositions().contains(pos));
    }

    @Test
    void shouldHandleEmptyLists() {
        GrassPositionGenerator generator = new GrassPositionGenerator(1, 1);

        Vector2d pos1 = generator.generateRandomPosition();
        Vector2d pos2 = generator.generateRandomPosition();

        assertNotNull(pos1);
        assertNull(pos2, "Should return null when no free positions are left");
    }
}