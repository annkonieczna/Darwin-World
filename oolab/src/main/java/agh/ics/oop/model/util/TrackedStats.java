package agh.ics.oop.model.util;

public enum TrackedStats {
    AVG_ENERGY("Avg Energy"),
    AVG_LIFETIME("Avg Life Time"),
    ANIMAL_COUNT("Animal Count"),
    GRASS_COUNT("Grass Count");

    private final String label;

    TrackedStats(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

