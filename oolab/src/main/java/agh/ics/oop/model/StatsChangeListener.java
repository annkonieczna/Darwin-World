package agh.ics.oop.model;

import agh.ics.oop.model.util.SimulationStats;

public interface StatsChangeListener {
    void statsChanged(SimulationStats stats);
}
