package agh.ics.oop.presenter;

import agh.ics.oop.model.util.SimulationStats;
import agh.ics.oop.model.util.TrackedStats;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import java.util.*;

public class SimulationChart {

    private static final int MAX_POINTS = 300;

    private final LineChart<Number, Number> chart;
    private final VBox checkboxBox;
    private final VBox legendBox;

    private final Map<TrackedStats, XYChart.Series<Number, Number>> activeSeries = new EnumMap<>(TrackedStats.class);
    private final Map<TrackedStats, Deque<XYChart.Data<Number, Number>>> history = new EnumMap<>(TrackedStats.class);

    public SimulationChart(LineChart<Number, Number> chart,
                           VBox checkboxBox,
                           VBox legendBox) {

        this.chart = chart;
        this.checkboxBox = checkboxBox;
        this.legendBox = legendBox;

        for (TrackedStats stat : TrackedStats.values()) {
            history.put(stat, new ArrayDeque<>());
            createCheckbox(stat);
            createLegendEntry(stat);
        }
    }

    public void updateStats(int day, SimulationStats stats) {
        for (TrackedStats stat : TrackedStats.values()) {
            XYChart.Data<Number, Number> data =
                    new XYChart.Data<>(day, stat.extract(stats));

            Deque<XYChart.Data<Number, Number>> h = history.get(stat);
            h.addLast(data);
            if (h.size() > MAX_POINTS) {
                h.removeFirst();
            }

            XYChart.Series<Number, Number> series = activeSeries.get(stat);
            if (series != null) {
                series.getData().add(data);
                if (series.getData().size() > MAX_POINTS) {
                    series.getData().remove(0);
                }
            }
        }
    }


    private void createCheckbox(TrackedStats stat) {
        CheckBox cb = new CheckBox(stat.getLabel());

        cb.setOnAction(e -> {
            if (cb.isSelected()) {
                showSeries(stat);
            } else {
                hideSeries(stat);
            }
        });

        checkboxBox.getChildren().add(cb);
    }



    private void showSeries(TrackedStats stat) {
        if (activeSeries.containsKey(stat)) return;

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(stat.getLabel());

        for (XYChart.Data<Number, Number> d : history.get(stat)) {
            series.getData().add(
                    new XYChart.Data<>(d.getXValue(), d.getYValue())
            );
        }

        activeSeries.put(stat, series);
        chart.getData().add(series);

        applyColor(series, stat.getColor());
    }

    private void hideSeries(TrackedStats stat) {
        XYChart.Series<Number, Number> series = activeSeries.remove(stat);
        if (series != null) {
            chart.getData().remove(series);
        }
    }


    private void applyColor(XYChart.Series<Number, Number> series, Color color) {
        Platform.runLater(() -> {
            Node node = series.getNode();
            if (node != null) {
                node.lookup(".chart-series-line")
                        .setStyle("-fx-stroke: " + toRgb(color) + ";");
            }
        });
    }

    private String toRgb(Color c) {
        return String.format(
                "rgb(%d,%d,%d)",
                (int)(c.getRed()*255),
                (int)(c.getGreen()*255),
                (int)(c.getBlue()*255)
        );
    }

    private void createLegendEntry(TrackedStats stat) {
        Rectangle rect = new Rectangle(12, 12, stat.getColor());
        Label label = new Label(stat.getLabel());

        HBox row = new HBox(6, rect, label);
        legendBox.getChildren().add(row);
    }
}
