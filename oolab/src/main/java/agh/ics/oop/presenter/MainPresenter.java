package agh.ics.oop.presenter;

import agh.ics.oop.CSVGenerator;
import agh.ics.oop.Simulation;
import agh.ics.oop.model.util.PresetManager;
import agh.ics.oop.model.stats.SimulationConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainPresenter {
    @FXML
    private CheckBox isToxicCheckBox;
    @FXML
    private TextField
            widthInput,
            heightInput,
            startGrassInput,
            startAnimalInput,
            growingGrassInput,
            toxicChanceInput,
            energyFromGrassInput,
            energyFromToxicInput,
            startEnergyInput,
            moveEnergyCostInput,
            reproductionEnergyCostInput,
            minimumEnergyForReproductionInput,
            minMutationInput,
            maxMutationInput,
            genomeLengthInput,
            maxAnimalEnergy;

    @FXML
    ComboBox<String> presetsComboBox;

    PresetManager presetManager = new PresetManager();;

    @FXML
    public void initialize() {
        setupIntegerValidation(widthInput, 5, 200);
        setupIntegerValidation(heightInput, 5, 200);
        setupIntegerValidation(startGrassInput, 0, 40000);
        setupIntegerValidation(startAnimalInput, 0, 40000);
        setupIntegerValidation(growingGrassInput, 0, 40000);
        setupIntegerValidation(toxicChanceInput, 0, 100);
        setupIntegerValidation(energyFromGrassInput, 0, 1000);
        setupIntegerValidation(energyFromToxicInput, 0, 1000);
        setupIntegerValidation(moveEnergyCostInput, 0, 1000);
        setupIntegerValidation(reproductionEnergyCostInput, 1, 1000);
        setupIntegerValidation(minimumEnergyForReproductionInput, 1, 1000);
        setupIntegerValidation(genomeLengthInput, 1, 100);
        setupIntegerValidation(minMutationInput, 0, 100);
        setupIntegerValidation(maxMutationInput, 0, 100);
        setupDependentIntegerValidation(minMutationInput,maxMutationInput);
        setupDependentIntegerValidation(minMutationInput,genomeLengthInput);
        setupDependentIntegerValidation(maxMutationInput,genomeLengthInput);
        setupIntegerValidation(startEnergyInput, 1, 1000);
        setupIntegerValidation(maxAnimalEnergy, 1, 1000);
        setupDependentIntegerValidation(startEnergyInput,maxAnimalEnergy);

        refreshPresets();
    }

    private void setupDependentIntegerValidation(TextField lower, TextField upper) {
        lower.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                int low = Integer.parseInt(lower.getText());
                int high = Integer.parseInt(upper.getText());
                if (high < low) {upper.setText(Integer.toString(low));}
            }
        });
        upper.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                int low = Integer.parseInt(lower.getText());
                int high = Integer.parseInt(upper.getText());
                if (high < low) {lower.setText(Integer.toString(high));}
            }
        });
    }

    private void setupIntegerValidation(TextField textField, int min, int max) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            if (newText.matches("\\d*") && newText.length() < 10) {
                return change;
            }
            return null;
        }));

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().isEmpty()) {
                    textField.setText(String.valueOf(min));
                }
                int value = Integer.parseInt(textField.getText());
                if (value < min) {
                    textField.setText(String.valueOf(min));
                } else if (value > max) {
                    textField.setText(String.valueOf(max));
                }
            }
        });
    }

    private void refreshPresets() {
        try {
            List<String> presets = presetManager.getAvailablePresets();
            presetsComboBox.getItems().clear();
            presetsComboBox.getItems().setAll(presets);

            if (presets.isEmpty()) presetsComboBox.setDisable(true);
            else presetsComboBox.setDisable(false);
        } catch (IOException e) {
            System.err.println("Could not load presets: " + e.getMessage());
        }
    }

    @FXML
    public void onSaveClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save preset");
        fileChooser.setInitialDirectory(new File("presets"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName("preset.json");

        File file = fileChooser.showSaveDialog(presetsComboBox.getScene().getWindow());
        if (file == null) return;

        try {
            presetManager.savePreset(file, makeConfig());
            refreshPresets();
        } catch (IOException e) {
            System.err.println("Could not save presets: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open preset");
        fileChooser.setInitialDirectory(new File("presets"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(presetsComboBox.getScene().getWindow());
        if (file == null) return;

        try {
            SimulationConfig result = presetManager.loadPreset(file);
            setConfig(result);
            refreshPresets();
        } catch (IOException e) {
            System.err.println("Could not open given preset: " + e.getMessage());
        }
    }

    @FXML
    public void onPresetSelected() {
        String selected = presetsComboBox.getValue();
        if (selected != null && !selected.isEmpty()) {
            try {
                SimulationConfig result = presetManager.loadFromPresets(selected);
                setConfig(result);
            } catch (IOException e) {
                System.err.println("Could not open given preset: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onToxicCheckBox() {
        if (isToxicCheckBox.isSelected()) {
            toxicChanceInput.setDisable(false);
            energyFromToxicInput.setDisable(false);
        } else {
            toxicChanceInput.setDisable(true);
            energyFromToxicInput.setDisable(true);
        }
    }

    @FXML
    public void onStartClicked() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(
                    getClass().getClassLoader().getResource("fxml/simulation.fxml")
            );

            BorderPane viewRoot = loader.load();
            SimulationPresenter presenter = loader.getController();

            Simulation sim = new Simulation(makeConfig());
            sim.registerMapListener(presenter);
            sim.registerStatsListener(presenter);
            presenter.setupPresenter(sim);

            CSVGenerator csvGenerator = new CSVGenerator(String.valueOf(sim.getSimID()));
            sim.registerStatsListener(csvGenerator);

            Stage stage = new Stage();
            configureStage(stage, viewRoot, presenter);

            presenter.startSimulation();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot, SimulationPresenter presenter) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");

        primaryStage.show();

        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());

        presenter.setWindowSize();
        scene.widthProperty().addListener((obs, oldW, newW) -> {
            presenter.setWindowSize();
        });
        scene.heightProperty().addListener((obs, oldH, newH) -> {
            presenter.setWindowSize();
        });
        primaryStage.setOnCloseRequest(event -> {
            presenter.terminateSimulation();
        });
    }

    private SimulationConfig makeConfig() {
        return new SimulationConfig(
                Integer.parseInt(widthInput.getText()),
                Integer.parseInt(heightInput.getText()),
                Integer.parseInt(startGrassInput.getText()),
                Integer.parseInt(startAnimalInput.getText()),
                Integer.parseInt(growingGrassInput.getText()),
                Integer.parseInt(toxicChanceInput.getText()),
                Integer.parseInt(energyFromGrassInput.getText()),
                Integer.parseInt(energyFromToxicInput.getText()),
                Integer.parseInt(startEnergyInput.getText()),
                Integer.parseInt(moveEnergyCostInput.getText()),
                Integer.parseInt(reproductionEnergyCostInput.getText()),
                Integer.parseInt(minimumEnergyForReproductionInput.getText()),
                Integer.parseInt(minMutationInput.getText()),
                Integer.parseInt(maxMutationInput.getText()),
                Integer.parseInt(genomeLengthInput.getText()),
                Integer.parseInt(maxAnimalEnergy.getText()),
                isToxicCheckBox.isSelected()
        );
    }

    private void setConfig(SimulationConfig config) {
        widthInput.setText(String.valueOf(config.width()));
        heightInput.setText(String.valueOf(config.height()));
        startGrassInput.setText(String.valueOf(config.startGrassAmount()));
        startAnimalInput.setText(String.valueOf(config.startAnimalAmount()));
        growingGrassInput.setText(String.valueOf(config.growingGrassAmount()));
        toxicChanceInput.setText(String.valueOf(config.toxicGrassChance()));
        energyFromGrassInput.setText(String.valueOf(config.energyFromGrass()));
        energyFromToxicInput.setText(String.valueOf(config.energyFromToxicGrass()));
        startEnergyInput.setText(String.valueOf(config.startEnergy()));
        moveEnergyCostInput.setText(String.valueOf(config.moveEnergyCost()));
        reproductionEnergyCostInput.setText(String.valueOf(config.reproductionEnergyCost()));
        minimumEnergyForReproductionInput.setText(String.valueOf(config.minimumEnergyForReproduction()));
        minMutationInput.setText(String.valueOf(config.minMutation()));
        maxMutationInput.setText(String.valueOf(config.maxMutation()));
        genomeLengthInput.setText(String.valueOf(config.genomeLength()));
        maxAnimalEnergy.setText(String.valueOf(config.maxAnimalEnergy()));
        isToxicCheckBox.setSelected(config.toxicOn());
        onToxicCheckBox();
    }
}
