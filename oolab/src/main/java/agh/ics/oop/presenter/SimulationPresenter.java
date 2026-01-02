package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private Simulation sim;
    private WorldMap map;
    private Thread simulationThread;

    private static final int CELL_SIZE = 30;

    public void setPresenter(Simulation sim) {
        this.sim = sim;
        this.map = sim.getWorldMap();
    }

    @Override
    public void mapChanged(WorldMap worldMap) {
        Platform.runLater(() -> {
            drawMap();
        });
    }

    //Drawing

    private void clearCanvas(GraphicsContext graphics) {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    private void configureFont(GraphicsContext graphics, int size) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font("Arial", size));
        graphics.setFill(Color.BLACK);
    }

    private void drawGrid(GraphicsContext graphics, int cols, int rows) {
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(1);

        for (int x = 0; x <= cols; x++) {
            graphics.strokeLine(
                    x * CELL_SIZE,
                    0,
                    x * CELL_SIZE,
                    rows * CELL_SIZE
            );
        }

        for (int y = 0; y <= rows; y++) {
            graphics.strokeLine(
                    0,
                    y * CELL_SIZE,
                    cols * CELL_SIZE,
                    y * CELL_SIZE
            );
        }
    }

    private void drawAxes(GraphicsContext graphics, Boundary boundary, int mapCols, int mapRows) {
        configureFont(graphics, 12);

        // left upper corner
        graphics.fillText("y/x", CELL_SIZE / 2.0, CELL_SIZE / 2.0);

        //  X-axis (upper row)
        for (int x = 0; x < mapCols; x++) {
            int value = boundary.lowerLeft().x() + x;
            double cx = (x + 1) * CELL_SIZE + CELL_SIZE / 2.0;
            graphics.fillText(String.valueOf(value), cx, CELL_SIZE / 2.0);
        }

        // Y-axis (left column)
        for (int y = 0; y < mapRows; y++) {
            int value = boundary.upperRight().y() - y;
            double cy = (y + 1) * CELL_SIZE + CELL_SIZE / 2.0;
            graphics.fillText(String.valueOf(value), CELL_SIZE / 2.0, cy);
        }
    }

    public void drawMap() {
        if (map == null) return;

        Boundary boundary = map.getBounds();

        int mapCols = boundary.upperRight().x() - boundary.lowerLeft().x() + 1;
        int mapRows = boundary.upperRight().y() - boundary.lowerLeft().y() + 1;

        int cols = mapCols + 1;
        int rows = mapRows + 1;

        mapCanvas.setWidth(cols * CELL_SIZE);
        mapCanvas.setHeight(rows * CELL_SIZE);

        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();

        clearCanvas(graphics);
        drawGrid(graphics, cols, rows);
        drawAxes(graphics, boundary, mapCols, mapRows);
        configureFont(graphics, 16);

        //drawElements
        drawElements();
    }
    // to add
    private void  drawElements() {
        Boundary boundary = map.getBounds();
        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();
        for (Map.Entry<Vector2d, Grass> entity : map.getGrasses().entrySet()) {
            Vector2d position = entity.getKey();
            WorldElement element = entity.getValue();

            int x = (position.getX() - boundary.lowerLeft().getX() +1) * CELL_SIZE + 1;
            int y = (boundary.upperRight().getY() - position.getY() +1) * CELL_SIZE + 1;

            graphics.fillText(element.toString(), x + CELL_SIZE / 2, y + CELL_SIZE / 2);
        }

        for (Map.Entry<Vector2d, List<Animal>> entity : map.getAnimals().entrySet()) {
            Vector2d position = entity.getKey();
            for (Animal element: entity.getValue()) {
                int x = (position.getX() - boundary.lowerLeft().getX() +1) * CELL_SIZE + 1;
                int y = (boundary.upperRight().getY() - position.getY() +1) * CELL_SIZE + 1;

                graphics.fillText(element.toString(), x + CELL_SIZE / 2, y + CELL_SIZE / 2);
            }
        }
    }


    //Starting/Pausing/Resuming a simulation

    public void startSimulation() {
        simulationThread = new Thread(this.sim);

        simulationThread.setDaemon(true);

        simulationThread.start();
    }


    @FXML
    public void onPlayClicked(){
        if (playButton.getText().equals("Play")) {
            resumeSimulation();
        } else {
            pauseSimulation();
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

    //stats

    private void updateStats() {

    }


}
