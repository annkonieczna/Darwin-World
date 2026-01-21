package agh.ics.oop;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class SimulationApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 32);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("fxml/main.fxml"));

        BorderPane viewRoot = loader.load();
        configureStage(primaryStage, viewRoot);
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation launcher");

        primaryStage.show();
//      tests!!!

        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}
