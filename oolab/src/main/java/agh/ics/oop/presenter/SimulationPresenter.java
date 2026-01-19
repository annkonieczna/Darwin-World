package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.SimulationStats;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import agh.ics.oop.model.util.TrackedStats;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;


import java.util.*;
import java.util.stream.Collectors;

public class SimulationPresenter implements MapChangeListener {

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
    private VBox chartsBox;
    @FXML
    private LineChart<Number, Number> statsChart;
    @FXML
    private VBox mapContainer;
    @FXML
    private ScrollPane mapScrollPane;



    private final Map<TrackedStats, XYChart.Series<Number, Number>> chartSeries = new HashMap<>();
    private final Map<TrackedStats, List<XYChart.Data<Number, Number>>> history = new EnumMap<>(TrackedStats.class);

    private double windowWidth;
    private double windowHeight;

    private Simulation sim;
    private Thread simulationThread;

    private MapRenderer renderer;

    public void setupPresenter(Simulation sim) {
        this.sim = sim;
        this.renderer = new MapRenderer(mapCanvas);

        setListeners();
        setupStatCheckboxes();
        for (TrackedStats stat : TrackedStats.values()) {
            history.put(stat, new ArrayList<>());
        }

    }


    private void setListeners() {
        simSpeedScroll.valueProperty().addListener((observable, oldValue, newValue) -> {
            int speed = ((int) (1000 - newValue.intValue()) / 10) * 10;
            simSpeedLabel.setText(String.valueOf(speed));
            this.sim.setRunningSpeed(speed);
        });
    }

    public void setWindowSize(double width, double height) {
        refreshMap();
    }
    private void refreshMap() {
        if (sim != null && mapCanvas != null) {
            WorldMap map = sim.getWorldMap();
            double areaWidth = mapContainer.getWidth();
            double areaHeight = mapContainer.getHeight();

            if (areaWidth <= 0) areaWidth = 600;
            if (areaHeight <= 0) areaHeight = 600;

            renderer.drawMap(map, areaWidth, areaHeight);
        }
    }

    @Override
    public void mapChanged(WorldMap worldMap) {
        Platform.runLater(this::refreshMap);
    }

    //stats

    @Override
    public void statsChanged(SimulationStats stats) {
        Platform.runLater(() -> {
            dayLabel.setText(String.valueOf(stats.day()));
            animalCountLabel.setText(String.valueOf(stats.animalCount()));
            grassCountLabel.setText(String.valueOf(stats.grassCount()));
            freeFieldsLabel.setText(String.valueOf(stats.freeFields()));
            avgEnergyLabel.setText(String.valueOf(stats.avgEnergy()));
            avgLifeTimeLabel.setText(String.valueOf(stats.avgLifeTime()));
            avgChildAmountLabel.setText(String.valueOf(stats.avgChildAmount()));
            renderer.setDominantGenotypes(stats.dominantGenotypes());

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
            for (TrackedStats stat : TrackedStats.values()) {
                XYChart.Data<Number, Number> newData = new XYChart.Data<>(stats.day(), stats.getStatValue(stat));

                history.get(stat).add(newData);
                if (history.get(stat).size() > 300) {
                    history.get(stat).remove(0);
                }

                XYChart.Series<Number, Number> series = chartSeries.get(stat);
                if (series != null) {

                    series.getData().add(newData);


                    if (series.getData().size() > 300) {
                        series.getData().remove(0);
                    }
                }
            }

        });
    }

    //Starting/Pausing/Resuming a simulation

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
    private void setupStatCheckboxes() {
        for (TrackedStats stat : TrackedStats.values()) {
            CheckBox cb = new CheckBox(stat.getLabel());

            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    addSeries(stat);
                } else {
                    removeSeries(stat);
                }
            });

            statsCheckboxBox.getChildren().add(cb);
        }
    }

    private void addSeries(TrackedStats stat) {
        if (!chartSeries.containsKey(stat)) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(stat.getLabel());

            List<XYChart.Data<Number, Number>> currentHistory = history.get(stat);
            for(XYChart.Data<Number, Number> data : currentHistory) {
                series.getData().add(new XYChart.Data<>(data.getXValue(), data.getYValue()));
            }

            chartSeries.put(stat, series);
            statsChart.getData().add(series);
        }
    }


    private void removeSeries(TrackedStats stat) {
        XYChart.Series<Number, Number> series = chartSeries.remove(stat);
        if (series != null) {
            statsChart.getData().remove(series);
        }
    }

}
