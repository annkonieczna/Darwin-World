package agh.ics.oop;

import agh.ics.oop.model.listeners.StatsChangeListener;
import agh.ics.oop.model.stats.SimulationStats;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CSVGenerator implements StatsChangeListener {
    private final String fileName;

    public CSVGenerator(String fileName) {
        String directoryName = "csvFiles";
        try {
            Files.createDirectories(Path.of(directoryName));
            this.fileName = String.format("%s/sim_%s.csv", directoryName, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder",e);
        }
        initialize();
    }

    private void initialize() {
        String header = String.join(";",
                "avgChildAmount",
                "avgEnergy",
                "avgLifeTime",
                "freeFields",
                "day",
                "animalCount",
                "grassCount",
                "dominantAmount",
                "dominantGenotypes"
        );
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println(header);
        } catch (IOException e) {
            System.err.println("Could not save head: " + e.getMessage());
        }
    }

    @Override
    public void statsChanged(SimulationStats stats) {

        String data = String.format("%f;%f;%f;%d;%d;%d;%d;%d",
                stats.avgChildAmount(),
                stats.avgEnergy(),
                stats.avgLifeTime(),
                stats.freeFields(),
                stats.day(),
                stats.animalCount(),
                stats.grassCount(),
                stats.dominantAmount()
                );

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(data);
        } catch (IOException e) {
            System.err.println("Could not save data: " + e.getMessage());
        }
    }

}
