package org.example.pruebaexe;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    String nombreUsuarioWin = System.getProperty("user.name");
    String ruta ="C:\\Users\\"+ nombreUsuarioWin +"\\Documents\\logsPass\\acc.gpg";
    String rutaTemp ="C:\\Users\\"+ nombreUsuarioWin +"\\Documents\\logsPass\\acc.txt";
    private static String key = "Rammusmaricones.";

    @FXML
    protected void initialize() {

        // crear o verificar si existe carpeta
        String username = System.getProperty("user.name");
        File directorio = new File("C:\\Users\\" + username + "\\Documents\\logsPass");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        File archivo = new File(directorio, "acc.gpg");
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error al crear el archivo");
                alert.showAndWait();
            }
        }

        // Desencriptar el archivo
        try {
            decryptFile(ruta, rutaTemp);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al descifrar el archivo");
            alert.showAndWait();
        }

        // Configuración de los botones

        botonInicio.setOnMouseEntered(event -> botonInicio.getScene().setCursor(Cursor.HAND));
        botonInicio.setOnMouseExited(event -> botonInicio.getScene().setCursor(Cursor.DEFAULT));

        /////////////////////////////////////////////////////////////////////// BARRA DE
        /////////////////////////////////////////////////////////////////////// ARRIBA////////////////////////

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
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////// METODOS
    /////////////////////////////////////////////////////////////////////// DE
    /////////////////////////////////////////////////////////////////////// CIFRADO
    /////////////////////////////////////////////////////////////////////// Y
    /////////////////////////////////////////////////////////////////////// DESCIFRADO////////////
    public static void decryptFile(String inputFile, String outputFile) throws Exception {
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputFile));
        byte[] outputBytes = decrypt(inputBytes, key);
        Files.write(Paths.get(outputFile), outputBytes);
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void iniciarSesion() {
        String password = passwordField.getText();
        if (password.isEmpty()) {
            mensajeError.setText("Tienes que introducir una contraseña");
            mensajeError.setVisible(true);
        } else if (!password.equals("1234")) { // Replace "1234" with the decrypted password
            mensajeError.setText("La contraseña es incorrecta");
            mensajeError.setVisible(true);
        } else {
            Stage stage = (Stage) botonInicio.getScene().getWindow();
            appInitializer.changeScene(stage, "menu1.fxml");
        }
    }

}