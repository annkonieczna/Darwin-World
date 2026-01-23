package agh.ics.oop.renderer;

import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.elements.Grass;
import agh.ics.oop.model.map.Boundary;
import agh.ics.oop.model.movement.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;

import java.util.*;

public class MapRenderer {
    private final Canvas mapCanvas;
    private double cellSize = 50;
    private double cellMargin = 1;
    private Set<List<Integer>> dominantGenotypes = new HashSet<>();

    private final Image grassNormalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/grass_normal.png")));
    private final Image grassToxicImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/grass_toxic.png")));
    private final Image animalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/pepu.png")));
    private final Image animalSpecialImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/pepu_special.png")));
    private final Image animalsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/pepus.png")));
    private final Image animalsSpecialImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/pepus_special.png")));

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
                (windowWidth - 50) / cols,
                (windowHeight - 50) / rows
        );

        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();

        if (cellSize > 20) {
            mapCanvas.setWidth(cols * cellSize + cellMargin);
            mapCanvas.setHeight(rows * cellSize + cellMargin);

            clearCanvas(graphics, Color.WHITE);
            drawBackground(graphics, boundary, Color.web("#85A947"), false);
            drawBackground(graphics, reverseBoundary(map.getJungleBoundary(), false, mapRows), Color.web("#3E7B27"), false);
            drawGrid(graphics, cols, rows);
            drawAxes(graphics, boundary, mapCols, mapRows);
            drawElements(graphics, map, boundary, false);
        } else {
            mapCanvas.setWidth(mapCols * cellSize);
            mapCanvas.setHeight(mapRows * cellSize);

            clearCanvas(graphics, Color.web("#85A947"));
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
        configureFont(graphics, (int) cellSize/2, "Poppins Medium", Color.WHITE);

        for (Map.Entry<Vector2d, Grass> entity : map.getGrasses().entrySet()) {
            Vector2d position = entity.getKey();
            Grass grass = entity.getValue();

            double x = (position.getX() - boundary.lowerLeft().getX() + offset) * cellSize + cellMargin*offset /2;
            double y = (boundary.upperRight().getY() - position.getY() + offset) * cellSize + cellMargin*offset /2;

            if (minimal) {
                if (grass.isToxic()) graphics.setFill(Color.web("#FF4646"));
                else graphics.setFill(Color.web("#A8DF8E"));
                graphics.fillOval(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2);
            } else {
                if (grass.isToxic()) graphics.drawImage(grassToxicImage, x, y, cellSize, cellSize);
                else graphics.drawImage(grassNormalImage, x, y, cellSize, cellSize);
            }
        }

        for (Map.Entry<Vector2d, List<Animal>> entity : map.getAnimals().entrySet()) {
            Vector2d position = entity.getKey();

            double x = (position.getX() - boundary.lowerLeft().getX() + offset) * cellSize + cellMargin*offset / 2.0;
            double y = (boundary.upperRight().getY() - position.getY() + offset) * cellSize + cellMargin*offset / 2.0;

            if (minimal) drawMinimalAnimals(graphics, entity.getValue(), x, y);
            else drawAnimals(graphics, entity.getValue(), x, y);
        }
        graphics.setImageSmoothing(true);
    }

    private void drawMinimalAnimals(GraphicsContext graphics, List<Animal> animals, double x, double y) {
        graphics.setFill(Color.web("#6E5034"));
        if (animals.size() == 1) {
            if (dominantGenotypes.contains(animals.getFirst().getGenome())) {
                graphics.setFill(Color.web("#6bc1f2"));
            }
            graphics.fillOval(x + cellSize * (0.5/3.0), y + cellSize * (0.5/3.0), cellSize / 1.5, cellSize / 1.5);
        } else {
            double cX = x + cellSize / 2.0;
            double cY = y + cellSize / 2.0;
            int dominantCount = 0;
            for (Animal animal : animals) {
                if (dominantGenotypes.contains(animal.getGenome())){
                    dominantCount++;
                }
            }
            for (int i = 0; i < 3; i++) {
                if (dominantCount > 0) {
                    graphics.setFill(Color.web("#6bc1f2"));
                    dominantCount --;
                } else {
                    graphics.setFill(Color.web("#6E5034"));
                }

                double angle = 2 * Math.PI * i / 3.0 - Math.PI/2.0;

                double pX = cX + cellSize/4.0 * Math.cos(angle);
                double pY = cY + cellSize/4.0  * Math.sin(angle);

                graphics.fillOval(pX - cellSize/5.0, pY - cellSize/5.0, cellSize/2.5, cellSize/2.5);
            }
        }
    }

    private void drawAnimals(GraphicsContext graphics, List<Animal> animals, double x, double y) {


        if (animals.size() == 1) {
            graphics.save();

            double cX = x + (cellSize - cellMargin) / 2.0;
            double cY = y + (cellSize - cellMargin) / 2.0;
            int angle = animals.getFirst().getDirection().toAngle() + 90;
            graphics.translate(cX, cY);
            graphics.rotate(angle);

            if (dominantGenotypes.contains(animals.getFirst().getGenome())) {
                graphics.drawImage(
                        animalSpecialImage,
                        -(cellSize - cellMargin) / 2.0,
                        -(cellSize - cellMargin) / 2.0,
                        (cellSize - cellMargin),
                        (cellSize - cellMargin)
                );
            } else {
                graphics.drawImage(
                        animalImage,
                        -(cellSize - cellMargin) / 2.0,
                        -(cellSize - cellMargin) / 2.0,
                        (cellSize - cellMargin),
                        (cellSize - cellMargin)
                );
            }

            graphics.restore();
            drawHealthBar(graphics, animals.getFirst(), cX, cY);
        } else {
            boolean dominant = false;
            for (Animal animal : animals) {
                if (dominantGenotypes.contains(animal.getGenome())) {
                    dominant = true;
                    break;
                }
            }

            if (dominant) {
                graphics.drawImage(
                        animalsSpecialImage,
                        x,
                        y,
                        (cellSize - cellMargin),
                        (cellSize - cellMargin)
                );
            } else {
                graphics.drawImage(
                        animalsImage,
                        x,
                        y,
                        (cellSize - cellMargin),
                        (cellSize - cellMargin)
                );
            }
        }
    }

    private void drawHealthBar(GraphicsContext graphics, Animal animal, double cX, double cY) {
        double w = cellSize / 1.5;
        double h = cellSize / 8.0;

        graphics.setFill(Color.WHITE);
        graphics.fillRoundRect(cX - w/2, cY-cellSize/2.5, w, h, h, h);

        double w2 = cellSize / 1.6;
        double h2 = cellSize / 12.0;

        double w3 = w2 * ((double) animal.getEnergy() /animal.getMaxEnergy());

        graphics.setFill(Color.GREEN);
        graphics.fillRoundRect(cX - w2/2, cY-cellSize/2.5 + (h-h2)/2.0, w3, h2, h2, h2);
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

    public void setDominantGenotypes(Set<List<Integer>> genotypes) {
        this.dominantGenotypes = genotypes;

    }
}
