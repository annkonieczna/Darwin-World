package agh.ics.oop.model.util;

import javafx.scene.paint.Color;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public enum TrackedStats {

    ANIMALS("Animals", Color.RED, SimulationStats::animalCount),
    GRASS("Grass", Color.BLUE, SimulationStats::grassCount),
    AVG_LIFETIME("Avg lifetime", Color.PURPLE, SimulationStats::avgLifeTime),
    AVG_ENERGY("Avg energy", Color.ORANGE, SimulationStats::avgEnergy);

    private final String label;
    private final Color color;
    private final ToDoubleFunction<SimulationStats> extractor;

    TrackedStats(String label, Color color,  ToDoubleFunction<SimulationStats> extractor) {
        this.label = label;
        this.color = color;
        this.extractor = extractor;
    }

    public String getLabel() { return label; }
    public Color getColor() { return color; }

    public int extract(SimulationStats stats) {
        return (int)extractor.applyAsDouble(stats);
    }
}
