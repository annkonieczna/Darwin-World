package agh.ics.oop.model.listeners;

import agh.ics.oop.model.stats.SimulationStats;

public interface StatsChangeListener {
    void statsChanged(SimulationStats stats);
}
