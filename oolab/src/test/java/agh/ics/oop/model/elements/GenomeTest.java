package agh.ics.oop.model.elements;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {

    @Test
    void generateLen() {
        assertEquals(0, Genome.generate(0).size());
        assertEquals(5, Genome.generate(5).size());
        assertEquals(100, Genome.generate(100).size());
    }

    @Test
    void generateRange() {
        List<Integer> g = Genome.generate(200);
        assertTrue(g.stream().allMatch(x -> x >= 0 && x <= 7));
    }

    @Test
    void resistanceScore() {
        List<Integer> genome = List.of(0, 1, 2, 3);
        List<Integer> pattern = List.of(0, 9, 2, 9); // 2/4= 50%
        assertEquals(50, Genome.calculateResistanceScore(genome, pattern));
    }

    @Test
    void fullResistance() {
        List<Integer> genome = List.of(1, 2, 3);
        assertEquals(100, Genome.calculateResistanceScore(genome, List.of(1, 2, 3)));
        assertEquals(0, Genome.calculateResistanceScore(genome, List.of(7, 7, 7)));
    }

    @Test
    void mutateSkipMax0() {
        List<Integer> child = new ArrayList<>(List.of(1, 1, 1, 1));
        Genome.mutate(child, 0, 0);
        assertEquals(List.of(1, 1, 1, 1), child);
    }

    @Test
    void mutateSkipMinGtMax() {
        List<Integer> child = new ArrayList<>(List.of(1, 1, 1, 1));
        Genome.mutate(child, 3, 2);
        assertEquals(List.of(1, 1, 1, 1), child);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 8",
            "3, 8",
            "7, 10"
    })
    void mutateK(int k, int n) {
        List<Integer> before = new ArrayList<>();
        for (int i = 0; i < n; i++) before.add(0);

        List<Integer> child = new ArrayList<>(before);
        Genome.mutate(child, k, k);

        long changed = 0;
        for (int i = 0; i < n; i++) {
            if (!before.get(i).equals(child.get(i))) changed++;
            assertTrue(child.get(i) >= 0 && child.get(i) <= 7);
        }
        assertEquals(k, changed);
    }

    @Test
    void combineLen() {
        List<Integer> a = List.of(1, 1, 1, 1, 1, 1);
        List<Integer> b = List.of(2, 2, 2, 2, 2, 2);

        List<Integer> child = Genome.combine(a, b, 10, 10, 0, 0);

        assertEquals(6, child.size());
    }

    @Test
    void combineUnmod() {
        List<Integer> a = List.of(1, 1, 1, 1);
        List<Integer> b = List.of(2, 2, 2, 2);

        List<Integer> child = Genome.combine(a, b, 10, 10, 0, 0);

        assertThrows(UnsupportedOperationException.class, () -> child.set(0, 7));
    }

    @Test
    void combineCountsEq() {
        List<Integer> a = List.of(1, 1, 1, 1, 1, 1);
        List<Integer> b = List.of(2, 2, 2, 2, 2, 2);

        List<Integer> child = Genome.combine(a, b, 10, 10, 0, 0);

        long ones = child.stream().filter(x -> x == 1).count();
        long twos = child.stream().filter(x -> x == 2).count();

        assertEquals(3, ones);
        assertEquals(3, twos);
    }

    @Test
    void combineCountsStrong() {
        List<Integer> strong = List.of(1, 1, 1, 1, 1, 1);
        List<Integer> weak = List.of(2, 2, 2, 2, 2, 2);

        List<Integer> child = Genome.combine(strong, weak, 80, 20, 0, 0);

        long ones = child.stream().filter(x -> x == 1).count();
        long twos = child.stream().filter(x -> x == 2).count();

        assertEquals(5, ones);
        assertEquals(1, twos);
    }

    @Test
    void combineRange() {
        List<Integer> a = Genome.generate(50);
        List<Integer> b = Genome.generate(50);

        List<Integer> child = Genome.combine(a, b, 40, 60, 5, 10);

        assertEquals(50, child.size());
        assertTrue(child.stream().allMatch(x -> x >= 0 && x <= 7));
    }
}

