package agh.ics.oop.presenter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainPresenter {

    @FXML
    private TextField movesTextField;

    public void onStartClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(
                    getClass().getClassLoader().getResource("simulation.fxml")
            );

            BorderPane viewRoot = loader.load();
            SimulationPresenter presenter = loader.getController();

            presenter.startSimulation();

            Stage stage = new Stage();
            stage.setTitle("Simulation");
            stage.setScene(new Scene(viewRoot));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
