package com.sentinel.sentinel_pm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class AppInitializer extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

        @Override
    public void start(@SuppressWarnings("exports") Stage stage) {
        try {
            // Leer la ruta desde el archivo JSON
            String jsonFilePath = "config.json";
            File jsonFile = new File(jsonFilePath);
            FXMLLoader fxmlLoader;

            // si el archivo existe, lee la ruta y redirige a inicio, si no a configuracion para crear el archivo JSON
            if (jsonFile.exists()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonFile);
                String ruta = jsonNode.get("ruta").asText();

                // Verificar que la ruta no sea nula o vacía
                if (ruta != null && !ruta.isEmpty()) {

                    fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Inicio.fxml"));

                } else {
                    fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Configuracion.fxml"));
                }
            } else {
                fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Configuracion.fxml"));
            }

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

    public void changeScene(@SuppressWarnings("exports") Stage stage, String fxml) {
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

    public void makeWindowDraggable(@SuppressWarnings("exports") Parent root, @SuppressWarnings("exports") Stage stage) {
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
        launch(args);
    }
}