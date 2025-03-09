package com.sentinel.sentinel_pm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.sentinel_pm.entidadesJson.passRuta;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

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
      private Button botonGuardar;

      @FXML
      private Button botonBuscarCarpeta;

      // ----------------------------------------------------USOS DE BOTONES-------------------------------------------
      @FXML
      private void initialize() {

            File jsonFile = new File("../config.json");

            //boton guardar para crear configuracion
            botonGuardar.setOnAction(event -> {

                  // comprobacion de que ambos campos estan recogidos
                  if (PasswdTextBox.getText().trim().length() != 0 && rutaArchivoTextBox.getText().trim().length() != 0) {
                        File rutaRecogida = new File(rutaArchivoTextBox.getText()); // coger ruta

                        //si la ruta recogida no existe la crea
                        if (!rutaRecogida.exists()) {

                              //crea la ruta y ejecuta para crear el JSON
                              System.out.println("la ruta no existe creando...");
                              rutaRecogida.mkdirs();

                              crearJSON(jsonFile);

                        } else {
                              System.out.println("la ruta existe");
                              crearJSON(jsonFile);
                        }

                  } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Dialog");
                        alert.setHeaderText("No deje ningun campo en blanco");
                        alert.showAndWait();
                  }
            });

            //boton de carpeta para elegir carpeta
            botonBuscarCarpeta.setOnAction(event -> {
                  buscarCarpeta();
            });

            // ---------------------------------------------------BARRA DE ARRIBA------------------------------------------------------
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
                                                new KeyFrame(Duration.seconds(0),
                                                            new KeyValue(stage.opacityProperty(), 0.0)),
                                                new KeyFrame(Duration.seconds(0.1),
                                                            new KeyValue(stage.opacityProperty(), 1.0)));
                                    timeline.play();
                              }
                        });

                        minimizeButton.setOnAction(event -> {
                              Timeline timeline = new Timeline(
                                          new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                                          new KeyFrame(Duration.seconds(0.1),
                                                      new KeyValue(stage.opacityProperty(), 0.0)));
                              timeline.setOnFinished(e -> stage.setIconified(true));
                              timeline.play();
                        });
                  }
            });

      }// ----------------------------------------------------FIN DE INITIALIZE---------------------------------------------------


      // -------------------------------------------------------METODOS---------------------------------------------------------
      public void obtenerPasswd() {
            String nuevaPasswd = PasswdTextBox.getText();
            System.out.println(nuevaPasswd);
      }

      public void obtenerRuta() {
            String nuevaRuta = rutaArchivoTextBox.getText();
            System.out.println(nuevaRuta);
      }

      public void crearJSON(File jsonFile) {
            String passwdRecogida = PasswdTextBox.getText();
            String rutaGuardar = rutaArchivoTextBox.getText();

            passRuta passRuta = new passRuta(passwdRecogida, rutaGuardar);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                  objectMapper.writeValue(jsonFile, passRuta);

                  if (jsonFile.exists()) {
                        System.out.println("json creado");
                  } else {
                        System.out.println("json no creado");
                  }
            } catch (IOException e) {
                  System.out.println("Error al crear el JSON: " + e.getMessage());
                  e.printStackTrace();
            }

            try {
                  FXMLLoader fxmlLoader = new FXMLLoader(
                              AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Inicio.fxml"));
                  Parent root = fxmlLoader.load();
                  Scene scene = new Scene(root);
                  scene.setFill(Color.TRANSPARENT);

                  Stage stage = (Stage) botonGuardar.getScene().getWindow();
                  stage.setScene(scene);
                  stage.setResizable(false);

                  stage.show();
            } catch (IOException e) {
                  System.out.println("Error al cargar la nueva escena: " + e.getMessage());
                  e.printStackTrace();
            }
      }

      public void buscarCarpeta() {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Seleccionar Carpeta");
            Window stage = botonBuscarCarpeta.getScene().getWindow();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if (selectedDirectory != null) {
                  rutaArchivoTextBox.setText(selectedDirectory.getAbsolutePath());
            } else {
                  System.out.println("No se seleccionó ninguna carpeta");
            }
      }
}