package com.sentinel.sentinel_pm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel_pm.entidadesJson.passRuta;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class ConfigurationController {

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private static TextField PasswdTextBox;

    @FXML
    private TextField rutaArchivoTextBox;

    @FXML
    private Button botonGuardar;

    @FXML
    private Button botonBuscarCarpeta;

    //para cambiar de escena
    private AppInitializer appInitializer = new AppInitializer();

    //passwd para pasar de clave
    public static String passwdKey = PasswdTextBox.getText();

    @FXML
    private void initialize() {

        //variable para salir del bucle y comprobar si esta creado
        File jsonFile = new File("test.json");

        botonGuardar.setOnAction(event -> {
            if (PasswdTextBox != null && rutaArchivoTextBox != null){
                //primero crear carpeta
                //guardar contrasena y ruta

                //coger ruta
                File rutaRecogida = new File(rutaArchivoTextBox.getText());

                while(!rutaRecogida.exists() && jsonFile.exists()){
                    //comprobar si existe y si no crearla
                    if (rutaRecogida.exists()){
                        System.out.println("la ruta existe");

                        //recoger passwd y almacenar junto a la ruta
                        String passwdRecogida = PasswdTextBox.getText();
                        String rutaGuardar = rutaArchivoTextBox.getText();

                        passRuta passRuta = new passRuta(passwdRecogida, rutaGuardar);

                        ObjectMapper objectMapper = new ObjectMapper();
                        try{
                            objectMapper.writeValue(new File(String.valueOf(jsonFile)), passRuta);

                            if (jsonFile.exists()){
                                System.out.println("json creado");
                            }else{
                                System.out.println("json no creado");
                            }

                            System.out.println("json creado");
                        }catch (IOException e){
                            System.out.println("catch de controller");
                        }

                    }else{
                        System.out.println("la ruta no existe creando...");
                        rutaRecogida.mkdirs();
                    }
                }

            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("No deje ningun campo en blanco");
                alert.showAndWait();
            }
        });

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