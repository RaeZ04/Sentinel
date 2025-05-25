package com.sentinel.sentinel_pm;

import com.sentinel.sentinel_pm.config.ConfigManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InicioController {

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button botonInicio;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label mensajeError;

    private AppInitializer appInitializer = new AppInitializer();

    @FXML
    protected void initialize() {
        // Configuración de los botones
        botonInicio.setOnMouseEntered(event -> botonInicio.getScene().setCursor(Cursor.HAND));
        botonInicio.setOnMouseExited(event -> botonInicio.getScene().setCursor(Cursor.DEFAULT));

        // boton de inicio
        botonInicio.setOnAction(event -> iniciarSesion());
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                iniciarSesion();
            }
        });

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
    }    private void iniciarSesion() {
        String passwordText = passwordField.getText();
        
        if (passwordText.isEmpty()) {
            mensajeError.setText("Tienes que introducir una contraseña");
            mensajeError.setVisible(true);
        } else if (!ConfigManager.verificarPassword(passwordText)) {
            mensajeError.setText("La contraseña es incorrecta");
            mensajeError.setVisible(true);
        } else {
            // Guardar la contraseña en el gestor de contraseñas para usarla en el cifrado
            com.sentinel.sentinel_pm.config.PasswordManager.setMasterPassword(passwordText);
            
            Stage stage = (Stage) botonInicio.getScene().getWindow();
            appInitializer.changeScene(stage, "/com/sentinel/sentinel_pm/menu1.fxml");
        }
    }
}