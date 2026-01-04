package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;

public class MapRenderer {
    private final Canvas mapCanvas;
    private double cellSize = 50;
    private double cellMargin = 1;

    public MapRenderer(Canvas canvas) {
        mapCanvas = canvas;
    }

    public void drawMap(WorldMap map, double windowWidth, double windowHeight) {
        if (map == null) return;

        Boundary boundary = map.getBounds();

        int mapCols = boundary.upperRight().x() - boundary.lowerLeft().x() + 1;
        int mapRows = boundary.upperRight().y() - boundary.lowerLeft().y() + 1;

        int cols = mapCols + 1;
        int rows = mapRows + 1;

        cellSize = Math.min(
                (windowWidth - 300) / cols,
                windowHeight / rows
        );

        mapCanvas.setWidth(cols * cellSize + cellMargin);
        mapCanvas.setHeight(rows * cellSize + cellMargin);

        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();

        clearCanvas(graphics);

        if (cellSize > 15) {
            drawGrid(graphics, cols, rows, false);
            drawAxes(graphics, boundary, mapCols, mapRows);
            drawElements(graphics, map, boundary);
        } else {
            drawGrid(graphics, cols, rows, true);
            drawAxes(graphics, boundary, mapCols, mapRows);
            drawElements(graphics, map, boundary);
        }
    }

    private void drawGrid(GraphicsContext graphics, int cols, int rows, boolean limit) {
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(cellMargin);

        for (int x = 0; x <= cols; x++) {
            if (limit && x > 1) {
                graphics.strokeLine(
                        x * cellSize + cellMargin /2,
                        0,
                        x * cellSize + cellMargin /2,
                        cellSize + cellMargin /2
                );
            } else {
                graphics.strokeLine(
                        x * cellSize + cellMargin /2,
                        0,
                        x * cellSize + cellMargin /2,
                        rows * cellSize + cellMargin /2
                );
            }
        }

        for (int y = 0; y <= rows; y++) {
            if (limit && y > 1) {
                graphics.strokeLine(
                        0,
                        y * cellSize + cellMargin /2,
                        cellSize + cellMargin /2,
                        y * cellSize + cellMargin /2
                );
            }else {
                graphics.strokeLine(
                        0,
                        y * cellSize + cellMargin /2,
                        cols * cellSize + cellMargin /2,
                        y * cellSize + cellMargin /2
                );
            }
        }
    }

    private void drawAxes(GraphicsContext graphics, Boundary boundary, int mapCols, int mapRows) {
        configureFont(graphics, (int) cellSize/2, "Poppins Medium", Color.BLACK);

        graphics.fillText("y/x", cellSize / 2.0 + cellMargin /2, cellSize / 2.0 + cellMargin /2);

        for (int x = 0; x < mapCols; x++) {
            int value = boundary.lowerLeft().x() + x;
            double cx = (x + 1) * cellSize + cellSize / 2.0 + cellMargin /2;
            graphics.fillText(String.valueOf(value), cx, cellSize / 2.0 + cellMargin /2);
        }

        for (int y = 0; y < mapRows; y++) {
            int value = boundary.upperRight().y() - y;
            double cy = (y + 1) * cellSize + cellSize / 2.0 + cellMargin /2;
            graphics.fillText(String.valueOf(value), cellSize / 2.0 + cellMargin /2, cy);
        }
    }

    private void drawElements(GraphicsContext graphics, WorldMap map, Boundary boundary) {
        configureFont(graphics, (int) cellSize/2, "Poppins Regular", Color.GREEN);

        for (Map.Entry<Vector2d, Grass> entity : map.getGrasses().entrySet()) {
            Vector2d position = entity.getKey();
            WorldElement element = entity.getValue();

            double x = (position.getX() - boundary.lowerLeft().getX() +1) * cellSize + cellMargin /2;
            double y = (boundary.upperRight().getY() - position.getY() +1) * cellSize + cellMargin /2;

            graphics.fillText(element.toString(), x + cellSize / 2, y + cellSize / 2);
        }

        configureFont(graphics, (int) cellSize/2, "Poppins Regular", Color.RED);
        for (Map.Entry<Vector2d, List<Animal>> entity : map.getAnimals().entrySet()) {
            Vector2d position = entity.getKey();
            for (Animal element: entity.getValue()) {
                double x = (position.getX() - boundary.lowerLeft().getX() +1) * cellSize + 1;
                double y = (boundary.upperRight().getY() - position.getY() +1) * cellSize + 1;

                graphics.fillText(element.toString(), x + cellSize / 2, y + cellSize / 2);
            }
        }
    }

    private void clearCanvas(GraphicsContext graphics) {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    private void configureFont(GraphicsContext graphics, int size, String fontWeight, Color color) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font(fontWeight, size));
        graphics.setFill(color);
    }
}
