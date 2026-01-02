package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainPresenter {

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }

    @FXML
    public void onStartClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(
                    getClass().getClassLoader().getResource("simulation.fxml")
            );

            BorderPane viewRoot = loader.load();
            SimulationPresenter presenter = loader.getController();


            Stage stage = new Stage();
            configureStage(stage, viewRoot);
            stage.show();

            Simulation sim = new Simulation();
            sim.registerListener(presenter);
            presenter.setupPresenter(sim);

            presenter.startSimulation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
