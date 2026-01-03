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



import java.util.List;
import java.util.Map;

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
            avgChildAmountLabel;
    @FXML
    private Slider simSpeedScroll;

    private Simulation sim;
    private Thread simulationThread;

    private MapRenderer renderer;

    public void setupPresenter(Simulation sim) {
        this.sim = sim;
        this.renderer = new MapRenderer(mapCanvas);

        setListeners();
    }

    private void setListeners(){
        simSpeedScroll.valueProperty().addListener((observable, oldValue, newValue) -> {
            int speed = ((int) (1000 - newValue.intValue())/10) * 10;
            simSpeedLabel.setText("Speed: " + String.valueOf(speed));
            this.sim.setRunningSpeed(speed);
        });
    }

    @Override
    public void mapChanged(WorldMap worldMap) {
        Platform.runLater(() -> {
            synchronized (worldMap) {
                renderer.drawMap(worldMap);
            }
        });
    }

    //stats

    @Override
    public void statsChanged(SimulationStats stats) {
        Platform.runLater(() -> {
            dayLabel.setText("Day: " + String.valueOf(stats.day()));
            animalCountLabel.setText("Animals: " + String.valueOf(stats.animalCount()));
            grassCountLabel.setText("Grasses: " + String.valueOf(stats.grassCount()));
            freeFieldsLabel.setText("Free Fields: " + String.valueOf(stats.freeFields()));
            avgEnergyLabel.setText("Avg. Energy: " + String.valueOf(stats.avgEnergy()));
            avgLifeTimeLabel.setText("Avg. Life Time: " + String.valueOf(stats.avgLifeTime()));
            avgChildAmountLabel.setText("Avg. Child Count: " + String.valueOf(stats.avgChildAmount()));
        });
    }

    //Starting/Pausing/Resuming a simulation

    public void startSimulation() {
        simulationThread = new Thread(this.sim);

        simulationThread.setDaemon(true);

        simulationThread.start();
    }

    @FXML
    public void onPlayClicked(){
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

}
