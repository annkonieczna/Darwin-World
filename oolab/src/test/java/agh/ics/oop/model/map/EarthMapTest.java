package agh.ics.oop.model.map;

import agh.ics.oop.model.movement.MapDirection;
import agh.ics.oop.model.movement.Movement;
import agh.ics.oop.model.movement.Vector2d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class EarthMapTest {
    @Test
    void inBoundsTest() {
        Boundary jungle = new Boundary(new Vector2d(2, 2), new Vector2d(3, 3));
        EarthMap map = new EarthMap(10, 10, jungle);

        assertTrue(map.inBounds(new Vector2d(0, 0)));
        assertTrue(map.inBounds(new Vector2d(9, 9)));
        assertFalse(map.inBounds(new Vector2d(10, 5)));
        assertFalse(map.inBounds(new Vector2d(5, -1)));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5, NW, 9, 6",
            "9, 5, NE, 0, 6",
            "5, 9, N,  5, 9",

            "5, 9, NE, 6, 9",
            "5, 9, NW, 4, 9",

            "5, 0, SE, 6, 0",
            "5, 0, SW, 4, 0",

            "9, 9, NE, 0, 9",
            "0, 9, NW, 9, 9",
            "9, 0, SE, 0, 0",
            "0, 0, SW, 9, 0"
    })
    void correctPositionTest(int startX, int startY, MapDirection dir, int expX, int expY) {
        Boundary jungle = new Boundary(new Vector2d(2, 2), new Vector2d(3, 3));
        EarthMap map = new EarthMap(10, 10, jungle);
        Vector2d current = new Vector2d(startX, startY);

        Movement result = map.correctPosition(current, dir);

        assertEquals(new Vector2d(expX, expY), result.position());
        if (startY == 9 && dir == MapDirection.N) {
            assertEquals(MapDirection.S, result.direction(), "Should rotate 180 deg on pole");
        }

        if (startY == 9 && dir == MapDirection.S) {
            assertEquals(MapDirection.S, result.direction(), "Should rotate 180 deg on pole");
        }
    }
}