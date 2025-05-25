package com.sentinel.sentinel_pm;

import com.sentinel.sentinel_pm.config.ConfigManager;
import com.sentinel.sentinel_pm.entidadesJson.passRuta;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class AppInitializer extends Application {

    private double xOffset = 0;
    private double yOffset = 0;    @Override
    public void start(@SuppressWarnings("exports") Stage stage) {
        try {
            // Inicializar el sistema de configuración (limpia archivos corruptos si es necesario)
            ConfigManager.inicializar();
            
            // Comprobar si existe config.json en alguna ubicación conocida
            boolean configExists = false;
            
            // 1. Comprobar en el directorio actual
            File currentDirConfig = new File("config.json");
            if (currentDirConfig.exists()) {
                configExists = true;
                System.out.println("Archivo de configuración encontrado en: " + currentDirConfig.getAbsolutePath());
            } else {
                // 2. Comprobar en la ruta específica si la conocemos
                File specificConfig = new File("c:\\Users\\Sergio\\Desktop\\fdsf\\sentinel\\config.json");
                if (specificConfig.exists()) {
                    configExists = true;
                    System.out.println("Archivo de configuración encontrado en: " + specificConfig.getAbsolutePath());
                    // Establecer esta ruta como la carpeta de configuración
                    ConfigManager.setConfigDirectory("c:\\Users\\Sergio\\Desktop\\fdsf");
                }
            }
            
            FXMLLoader fxmlLoader;
            
            if (configExists) {
                // Si existe el archivo de configuración, ir a la pantalla de inicio de sesión
                fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Inicio.fxml"));
                System.out.println("Configuración existente encontrada, mostrando pantalla de inicio de sesión");
            } else {
                // No hay configuración existente, mostrar la pantalla de configuración inicial
                fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Configuracion.fxml"));
                System.out.println("No se encontró configuración, mostrando pantalla de configuración inicial");
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
            mostrarAlerta(AlertType.ERROR, "Error", "Error al iniciar la aplicación: " + e.getMessage());
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
            mostrarAlerta(AlertType.ERROR, "Error", "Error al cambiar de pantalla: " + e.getMessage());
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
    
    private static void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        try {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al mostrar alerta: " + e.getMessage());
            System.err.println(titulo + ": " + mensaje);
        }
    }

    public static void main(String[] args) {
        // Aseguramos que cualquier excepción en el hilo de JavaFX se registre
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Error no capturado en thread: " + thread.getName());
            throwable.printStackTrace();
        });
        
        // Configuramos el manejo de errores en el hilo de la UI de JavaFX
        Platform.startup(() -> {});
        Platform.setImplicitExit(true);
        
        launch(args);
    }
}