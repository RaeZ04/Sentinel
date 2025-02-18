package com.sentinel.sentinel_pm;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class ConfigurationController {

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private TextField PasswdTextBox;

    @FXML
    private TextField rutaArchivoTextBox;

    @FXML
    private Button botonBuscarCarpeta;

    //para cambiar de escena
    private AppInitializer appInitializer = new AppInitializer();

    @FXML
    private void initialize() {

        if (PasswdTextBox != null && rutaArchivoTextBox != null){
            //primero crear carpeta
            //guardar contrasena y ruta

            //coger ruta
            File rutaRecogida = new File(rutaArchivoTextBox.getText());

            //comprobar si existe y si no crearla
            if (rutaRecogida.exists()){
                System.out.println("la ruta existe");

                //recoger passwd y almacenar junto a la ruta
                String passwdRecogida = PasswdTextBox.getText();




            }else{
                System.out.println("la ruta no existe creando...");
                rutaRecogida.mkdirs();
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("No deje ningun campo en blanco");
            alert.showAndWait();
        }
















        /////////////////////////////////////////////////////////////////////// BARRA DE
        /////////////////////////////////////////////////////////////////////// ARRIBA////////////////////////

        // boton de salir X en la esquina superior derecha
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0)));
            timeline.setOnFinished(e -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    Platform.runLater(() -> System.exit(0));
                }).start();
            });
            timeline.play();
        });

        // Configuración del botón de minimizar
        Platform.runLater(() -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();
            if (stage != null) {
                stage.iconifiedProperty().addListener((obs, wasMinimized, isNowMinimized) -> {
                    if (!isNowMinimized) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 0.0)),
                                new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 1.0)));
                        timeline.play();
                    }
                });

                minimizeButton.setOnAction(event -> {
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                            new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0)));
                    timeline.setOnFinished(e -> stage.setIconified(true));
                    timeline.play();
                });
            }
        });

    }

    /// //////////////////////////////////////////////////////////METODOS//////////////////////////////////////////////////////////////////////////////////////////////////////
    public void obtenerPasswd(){
        String nuevaPasswd = PasswdTextBox.getText();
        System.out.println(nuevaPasswd);
    }

    public void obtenerRuta(){
        String nuevaRuta = rutaArchivoTextBox.getText();
        System.out.println(nuevaRuta);
    }



}
