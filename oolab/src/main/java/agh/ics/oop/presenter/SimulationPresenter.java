package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.listeners.MapChangeListener;
import agh.ics.oop.model.listeners.StatsChangeListener;
import agh.ics.oop.model.stats.SimulationStats;
import agh.ics.oop.renderer.MapRenderer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.chart.LineChart;
import javafx.util.Duration;


import java.util.*;
import java.util.stream.Collectors;

public class SimulationPresenter implements MapChangeListener, StatsChangeListener {

    @FXML
    private Canvas mapCanvas;
    @FXML
    private Button playButton;
    @FXML
    private Label
            simSpeedLabel,
            dayLabel,
            animalCountLabel,
            grassCountLabel,
            freeFieldsLabel,
            avgEnergyLabel,
            avgLifeTimeLabel,
            avgChildAmountLabel,
            dominantGenotypesLabel;
    @FXML
    private Slider simSpeedScroll;
    @FXML
    private VBox dominantGenotypesBox;
    @FXML
    private VBox statsCheckboxBox;
    @FXML
    private LineChart<Number, Number> statsChart;
    @FXML
    private VBox mapContainer;
    @FXML
    private HBox statsLegendBox;
    @FXML
    private NumberAxis dayAxis;


    private static final int WINDOW = 500;
    private Simulation sim;
    private Thread simulationThread;
    private final PauseTransition resizeThrottle = new PauseTransition(Duration.millis(50));

    private MapRenderer renderer;
    private SimulationChart statsChartController;

    public void setupPresenter(Simulation sim) {
        this.sim = sim;
        this.renderer = new MapRenderer(mapCanvas);

        setListeners();
        dayAxis.setAutoRanging(false);
        dayAxis.setLowerBound(1);
        dayAxis.setUpperBound(50);
        dayAxis.setForceZeroInRange(false);
        statsChartController = new SimulationChart(
                statsChart,
                statsCheckboxBox,
                statsLegendBox,
                WINDOW
        );
    }

    private void setListeners() {
        simSpeedScroll.valueProperty().addListener((observable, oldValue, newValue) -> {
            int speed = ((int) (1000 - newValue.intValue()) / 10) * 10;
            if (speed == 0) speed = 10;
            simSpeedLabel.setText(String.valueOf(speed));
            this.sim.setRunningSpeed(speed);
        });
    }

    public void setWindowSize() {
        resizeThrottle.setOnFinished(event -> refreshMap());
        resizeThrottle.playFromStart();
    }

    private void refreshMap() {
        if (sim != null && mapCanvas != null) {
            WorldMap map = sim.getWorldMap();
            double areaWidth = mapContainer.getWidth();
            double areaHeight = mapContainer.getHeight();

            if (areaWidth <= 0) areaWidth = 100;
            if (areaHeight <= 0) areaHeight = 100;

            synchronized (map){
                renderer.drawMap(map, areaWidth, areaHeight);
            }
        }
    }

    @Override
    public void mapChanged() {
        Platform.runLater(this::refreshMap);
    }

    @Override
    public void statsChanged(SimulationStats stats) {
        Platform.runLater(() -> {
            dayLabel.setText(String.valueOf(stats.day()));
            animalCountLabel.setText(String.valueOf(stats.animalCount()));
            grassCountLabel.setText(String.valueOf(stats.grassCount()));
            freeFieldsLabel.setText(String.valueOf(stats.freeFields()));
            avgEnergyLabel.setText(String.format("%.2f",stats.avgEnergy()));
            avgLifeTimeLabel.setText(String.format("%.2f",stats.avgLifeTime()));
            avgChildAmountLabel.setText(String.format("%.2f",stats.avgChildAmount()));
            renderer.setDominantGenotypes(stats.dominantGenotypes());
            dominantGenotypesLabel.setText(
                    String.format("Dominant Genotypes with this many animals each: %d",
                            stats.dominantAmount()));

            dominantGenotypesBox.getChildren().clear();
            for (List<Integer> genotype : stats.dominantGenotypes()) {
                Label label = new Label(
                        genotype.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(""))
                );
                label.getStyleClass().add("fontSmall");
                dominantGenotypesBox.getChildren().add(label);
            }
            int d = stats.day();
            int lower = Math.max(1, d - WINDOW + 1);

            dayAxis.setLowerBound(lower);
            dayAxis.setUpperBound(d);
            dayAxis.setForceZeroInRange(false);
            int range = d - lower + 1;

            double tick = Math.max(1, Math.pow(10, Math.floor(Math.log10(range / 10.0))));

            if (range / tick > 20) tick *= 5;
            else if (range / tick > 15) tick *= 2;

            dayAxis.setTickUnit(tick);
            statsChartController.updateStats(stats.day(), stats);
        });
    }

    public void startSimulation() {
        simulationThread = new Thread(this.sim);

        simulationThread.setDaemon(true);

        simulationThread.start();
    }

    @FXML
    public void onPlayClicked() {
        if (sim.getRunning()) {
            pauseSimulation();
        } else {
            resumeSimulation();
        }
    }

    public void pauseSimulation() {
        playButton.setText("Play");
        sim.setRunning(false);
    }

    public void resumeSimulation() {
        playButton.setText("Pause");
        sim.setRunning(true);
    }

    public void terminateSimulation() {
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }


}
