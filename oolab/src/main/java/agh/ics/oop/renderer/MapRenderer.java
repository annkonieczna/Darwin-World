package agh.ics.oop.renderer;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapRenderer {
    private final Canvas mapCanvas;
    private double cellSize = 50;
    private double cellMargin = 1;

    private final Image grassNormalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/grass_normal.png")));
    private final Image grassToxicImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/grass_toxic.png")));
    private final Image animalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/pepu.png")));

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
                (windowHeight - 100) / rows
        );

        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();

        if (cellSize > 15) {
            mapCanvas.setWidth(cols * cellSize + cellMargin);
            mapCanvas.setHeight(rows * cellSize + cellMargin);

            clearCanvas(graphics, Color.WHITE);
            drawBackground(graphics, boundary, Color.web("#85A947"), false);
            drawBackground(graphics, reverseBoundary(map.getJungleBoundary(), false, mapRows), Color.web("#3E7B27"), false);
            //!!! for tests
//            drawGrid(graphics, cols, rows);
            drawAxes(graphics, boundary, mapCols, mapRows);
            drawElements(graphics, map, boundary, false);
        } else {
            mapCanvas.setWidth(mapCols * cellSize);
            mapCanvas.setHeight(mapRows * cellSize);

            drawBackground(graphics, boundary, Color.web("#85A947"), true);
            drawBackground(graphics, reverseBoundary(map.getJungleBoundary(), false, mapRows), Color.web("#3E7B27"), true);
            drawElements(graphics, map, boundary, true);
        }
    }

    private void drawGrid(GraphicsContext graphics, int cols, int rows) {
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(cellMargin);

        for (int x = 0; x <= cols; x++) {
            graphics.strokeLine(
                    x * cellSize + cellMargin /2,
                    0,
                    x * cellSize + cellMargin /2,
                    rows * cellSize + cellMargin /2
            );
        }

        for (int y = 0; y <= rows; y++) {
            graphics.strokeLine(
                    0,
                    y * cellSize + cellMargin /2,
                    cols * cellSize + cellMargin /2,
                    y * cellSize + cellMargin /2
            );
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

    private void drawElements(GraphicsContext graphics, WorldMap map, Boundary boundary, boolean minimal) {
        int offset = minimal ? 0 : 1;
        graphics.setImageSmoothing(false);

        for (Map.Entry<Vector2d, Grass> entity : map.getGrasses().entrySet()) {
            Vector2d position = entity.getKey();
            Grass grass = entity.getValue();

            double x = (position.getX() - boundary.lowerLeft().getX() + offset) * cellSize + cellMargin /2;
            double y = (boundary.upperRight().getY() - position.getY() + offset) * cellSize + cellMargin /2;

            if (minimal) {
                if (grass.isToxic()) graphics.setFill(Color.RED);
                else graphics.setFill(Color.LIGHTGREEN);
                graphics.fillOval(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2);
            } else {
                if (grass.isToxic()) graphics.drawImage(grassToxicImage, x, y, cellSize, cellSize);
                else graphics.drawImage(grassNormalImage, x, y, cellSize, cellSize);
            }
        }

        for (Map.Entry<Vector2d, List<Animal>> entity : map.getAnimals().entrySet()) {
            Vector2d position = entity.getKey();

            double x = (position.getX() - boundary.lowerLeft().getX() + offset) * cellSize + cellMargin /2;
            double y = (boundary.upperRight().getY() - position.getY() + offset) * cellSize + cellMargin /2;

            drawAnimals(graphics, entity.getValue(), x, y, minimal);
        }
        graphics.setImageSmoothing(true);
    }

    private void drawAnimals(GraphicsContext graphics, List<Animal> animals, double x, double y, boolean minimal) {
        if (animals.size() == 1) {
            if (minimal) {
                graphics.setFill(Color.BROWN);
                graphics.fillOval(x + cellSize * (0.5/3), y + cellSize * (0.5/3), cellSize / 1.5, cellSize / 1.5);
            } else {
                graphics.drawImage(animalImage, x, y, cellSize, cellSize);
            }
        } else {
            if (minimal) {
                graphics.setFill(Color.BROWN);
                for (int i = 0; i < animals.size(); i++) {
                    graphics.fillOval(x + cellSize * (0.5/3), y + cellSize * (0.5/3), cellSize / 1.5, cellSize / 1.5);
                }
            } else {
                graphics.drawImage(animalImage, x, y, cellSize, cellSize);
            }
        }
    }

    private void drawBackground(GraphicsContext graphics, Boundary boundary, Color color, boolean minimal) {
        int offset = minimal ? 0 : 1;

        double w = (boundary.upperRight().getX() - boundary.lowerLeft().getX()+1) * cellSize;
        double h = (boundary.upperRight().getY() - boundary.lowerLeft().getY()+1) * cellSize;

        double x = (boundary.lowerLeft().getX()+offset) * cellSize;
        double y = (boundary.lowerLeft().getY()+offset) * cellSize;

        graphics.setFill(color);
        graphics.fillRect(x, y, w, h);
    }

    private void clearCanvas(GraphicsContext graphics, Color color) {
        graphics.setFill(color);
        graphics.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    private void configureFont(GraphicsContext graphics, int size, String fontWeight, Color color) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font(fontWeight, size));
        graphics.setFill(color);
    }

    private Boundary reverseBoundary(Boundary boundary, boolean horizontal, int value) {
        Vector2d lowerLeft = boundary.lowerLeft();
        Vector2d upperRight = boundary.upperRight();

        if (horizontal) {
            int newX1 = (value - 1) - upperRight.getX();
            int newX2 = (value - 1) - lowerLeft.getX();

            return new Boundary(
                    new Vector2d(newX1, lowerLeft.getY()),
                    new Vector2d(newX2, upperRight.getY())
            );
        } else {
            int newY1 = (value - 1) - upperRight.getY();
            int newY2 = (value - 1) - lowerLeft.getY();

            return new Boundary(
                    new Vector2d(lowerLeft.getX(), newY1),
                    new Vector2d(upperRight.getX(), newY2)
            );
        }
    }
}
