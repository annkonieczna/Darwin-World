package agh.ics.oop.model.util;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PresetManager {
    private final Gson gson = new Gson();
    private final Path directory = Path.of("presets");

    public PresetManager() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder",e);
        }
    }

    public void savePreset(File file, SimulationConfig config) throws IOException {
        Path path = file.toPath();
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(config, writer);
        }
    }

    public SimulationConfig loadPreset(File file) throws IOException {
        Path path = file.toPath();
        try (Reader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, SimulationConfig.class);
        }
    }

    public SimulationConfig loadFromPresets(String file) throws IOException {
        Path path = directory.resolve(file + ".json");
        try (Reader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, SimulationConfig.class);
        }
    }

    public List<String> getAvailablePresets() throws IOException {
        if (!Files.exists(directory)) return List.of();
        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().endsWith(".json"))
                    .map(file -> file.getFileName().toString().replace(".json", ""))
                    .collect(Collectors.toList());
        }
    }
}
