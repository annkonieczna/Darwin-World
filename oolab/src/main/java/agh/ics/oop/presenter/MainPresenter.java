package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.util.SimulationConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
            genomeLengthInput;

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
        setupIntegerValidation(startEnergyInput, 1, 1000);
        setupIntegerValidation(moveEnergyCostInput, 0, 1000);
        setupIntegerValidation(reproductionEnergyCostInput, 1, 1000);
        setupIntegerValidation(minimumEnergyForReproductionInput, 1, 1000);
        setupIntegerValidation(genomeLengthInput, 1, 100);
        setupIntegerValidation(minMutationInput, 0, 100);
        setupIntegerValidation(maxMutationInput, 0, 100);

//        !!!for tests
        onStartClicked();
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
                }
                if (value > max) {
                    textField.setText(String.valueOf(max));
                }
                if (textField.getId().equals("minMutationInput")) {
                    if (value > Integer.parseInt(genomeLengthInput.getText())) {
                        minMutationInput.setText(genomeLengthInput.getText());
                        value = Integer.parseInt(genomeLengthInput.getText());
                    }
                    if(value > Integer.parseInt(maxMutationInput.getText())) {
                        maxMutationInput.setText(String.valueOf(value));
                    }
                }
                if (textField.getId().equals("maxMutationInput")) {
                    if (value > Integer.parseInt(genomeLengthInput.getText())) {
                        maxMutationInput.setText(genomeLengthInput.getText());
                    }
                    if(value < Integer.parseInt(minMutationInput.getText())) {
                        minMutationInput.setText(String.valueOf(value));
                    }
                }
            }
        });
    }

    @FXML
    public void onToxicCheckBox() {
        if (isToxicCheckBox.isSelected()) {
            toxicChanceInput.setDisable(false);
            energyFromToxicInput.setDisable(false);
        } else {
            toxicChanceInput.setText("0");
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
            sim.registerListener(presenter);
            presenter.setupPresenter(sim);

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

        presenter.setWindowSize(scene.getWidth(), scene.getHeight());
        scene.widthProperty().addListener((obs, oldW, newW) -> {
            presenter.setWindowSize(scene.getWidth(), scene.getHeight());
        });
        scene.heightProperty().addListener((obs, oldH, newH) -> {
            presenter.setWindowSize(scene.getWidth(), scene.getHeight());
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
                Integer.parseInt(genomeLengthInput.getText())
        );
    }
}
