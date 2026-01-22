package agh.ics.oop.model.movement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class Vector2dTest {

    @Test
    void toStringFormatsCorrectly() {
        assertEquals("(3,4)", new Vector2d(3, 4).toString());
        assertEquals("(-1,0)", new Vector2d(-1, 0).toString());
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,  2,2,  true",
            "2,1,  2,2,  true",
            "3,2,  2,2,  false",
            "2,3,  2,2,  false"
    })
    void precedesTest(int ax, int ay, int bx, int by, boolean expected) {
        Vector2d a = new Vector2d(ax, ay);
        Vector2d b = new Vector2d(bx, by);
        assertEquals(expected, a.precedes(b));
    }

    @ParameterizedTest
    @CsvSource({
            "2,2,  1,1,  true",
            "2,2,  2,1,  true",
            "2,2,  3,1,  false",
            "2,2,  1,3,  false"
    })
    void followsTest(int ax, int ay, int bx, int by, boolean expected) {
        Vector2d a = new Vector2d(ax, ay);
        Vector2d b = new Vector2d(bx, by);
        assertEquals(expected, a.follows(b));
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,  3,4,  4,6",
            "-1,2, 3,-5, 2,-3",
            "0,0,  0,0,  0,0"
    })
    void addTest(int ax, int ay, int bx, int by, int ex, int ey) {
        assertEquals(new Vector2d(ex, ey), new Vector2d(ax, ay).add(new Vector2d(bx, by)));
    }

    @ParameterizedTest
    @CsvSource({
            "5,7,  2,3,  3,4",
            "-1,2, 3,-5, -4,7",
            "0,0,  0,0,  0,0"
    })
    void subtractTest(int ax, int ay, int bx, int by, int ex, int ey) {
        assertEquals(new Vector2d(ex, ey), new Vector2d(ax, ay).subtract(new Vector2d(bx, by)));
    }

    @ParameterizedTest
    @CsvSource({
            "1,9,  3,4,  3,9",
            "-1,2, 3,-5, 3,2",
            "0,0,  0,0,  0,0"
    })
    void upperRightTest(int ax, int ay, int bx, int by, int ex, int ey) {
        assertEquals(new Vector2d(ex, ey), new Vector2d(ax, ay).upperRight(new Vector2d(bx, by)));
    }

    @ParameterizedTest
    @CsvSource({
            "1,9,  3,4,  1,4",
            "-1,2, 3,-5, -1,-5",
            "0,0,  0,0,  0,0"
    })
    void lowerLeftTest(int ax, int ay, int bx, int by, int ex, int ey) {
        assertEquals(new Vector2d(ex, ey), new Vector2d(ax, ay).lowerLeft(new Vector2d(bx, by)));
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,  -1,-2",
            "-3,0, 3,0",
            "0,0,  0,0"
    })
    void oppositeTest(int ax, int ay, int ex, int ey) {
        assertEquals(new Vector2d(ex, ey), new Vector2d(ax, ay).opposite());
    }

    @Test
    void gettersReturnRecordComponents() {
        Vector2d v = new Vector2d(7, -2);
        assertEquals(7, v.getX());
        assertEquals(-2, v.getY());
        assertEquals(7, v.x());
        assertEquals(-2, v.y());
    }
}

