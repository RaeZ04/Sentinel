package org.example.pruebaexe;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

import static javafx.application.Application.launch;

public class AppInitializer extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("Inicio.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);

            makeWindowDraggable(root, stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeScene(@SuppressWarnings("exports")Stage stage, String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            makeWindowDraggable(root, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeWindowDraggable(Parent root, Stage stage) {
        // Cuando se presiona el ratón, almacenamos la posición actual del ratón
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // Cuando se arrastra el ratón, movemos la ventana a la nueva posición del ratón
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}


