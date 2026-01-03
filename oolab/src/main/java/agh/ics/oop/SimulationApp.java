package agh.ics.oop;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Parent;


public class SimulationApp extends Application {

    private void configureAndShowStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot, 600,640);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation launcher");
        //zmieniłem to żeby okienko faktycznie miało takie minimalne rozmiary

//        primaryStage.show();      //!!!turned off for tests

        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 32);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("main.fxml"));

        BorderPane viewRoot = loader.load();
        configureAndShowStage(primaryStage, viewRoot);
    }
}
