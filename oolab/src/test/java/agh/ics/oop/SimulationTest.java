package agh.ics.oop;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.movement.MapDirection;
import agh.ics.oop.model.movement.Vector2d;
import agh.ics.oop.model.stats.SimulationConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {
    @Test
    void simulationIntegrationFullCycleTest() {
        SimulationConfig config = new SimulationConfig(
                10,
                10,
                0,
                1,
                0,
                0,
                20,
                0,
                100,
                5,
                5,
                10,
                0,
                0,
                5,
                200,
                false
        );
        Simulation simulation = new Simulation(config);

        Animal a = simulation.getWorldMap().getAnimals().values().stream()
                .flatMap(List::stream)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No starting animal found"));

        Vector2d startPos = a.getPosition();
        MapDirection startDir = a.getDirection();

        int activeGene = a.getGenome().get(a.getActiveGeneIndex());
        MapDirection expectedDir = startDir.rotate(activeGene);

        Vector2d expectedPos = simulation.getWorldMap().correctPosition(startPos, expectedDir).position();

        simulation.doDay();

        assertEquals(expectedPos, a.getPosition(),
                "Animal started at " + startPos + " facing " + startDir +
                        " with active gene " + activeGene + ". It should move to " + expectedPos);

        assertEquals(95, a.getEnergy(), "Energy should decrease after move");
    }
}