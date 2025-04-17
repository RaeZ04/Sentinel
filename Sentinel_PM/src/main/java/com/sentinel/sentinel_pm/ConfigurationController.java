package com.sentinel.sentinel_pm;

import com.sentinel.sentinel_pm.config.ConfigManager;
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
import javafx.scene.Cursor;

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

      @FXML
      private Button saveConf;

      // ----------------------------------------------------USOS DE BOTONES-------------------------------------------
      @FXML
      private void initialize() {
            // Configurar efectos de hover para los botones
            configureButtonEffects();

            // Botón guardar para crear configuración
            saveConf.setOnAction(event -> {
                  // Comprobación de que ambos campos están recogidos
                  if (PasswdTextBox.getText().trim().length() != 0 && rutaArchivoTextBox.getText().trim().length() != 0) {
                        File rutaRecogida = new File(rutaArchivoTextBox.getText()); // Coger ruta

                        // Si la ruta recogida no existe la crea
                        if (!rutaRecogida.exists()) {
                              // Crea la ruta y ejecuta para guardar la configuración
                              System.out.println("La ruta no existe, creando...");
                              if (rutaRecogida.mkdirs()) {
                                  guardarConfiguracion();
                              } else {
                                  mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear la ruta especificada");
                              }
                        } else {
                              System.out.println("La ruta existe");
                              guardarConfiguracion();
                        }
                  } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No deje ningún campo en blanco");
                  }
            });

            // Botón de carpeta para elegir carpeta
            botonBuscarCarpeta.setOnAction(event -> {
                  buscarCarpeta();
            });

            // ---------------------------------------------------BARRA DE ARRIBA------------------------------------------------------
            // Botón de salir X en la esquina superior derecha
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

      // Método para configurar los efectos visuales de los botones
      private void configureButtonEffects() {
            // Configurar el cursor para los botones
            saveConf.setCursor(Cursor.HAND);
            botonBuscarCarpeta.setCursor(Cursor.HAND);
      }

      // -------------------------------------------------------MÉTODOS---------------------------------------------------------
      private void guardarConfiguracion() {
            try {
                String password = PasswdTextBox.getText();
                String ruta = rutaArchivoTextBox.getText();
                
                // Usar el nuevo ConfigManager para guardar la configuración
                if (ConfigManager.guardarConfiguracion(password, ruta)) {
                    System.out.println("Configuración guardada correctamente");
                    cargarEscenaInicio();
                } else {
                    System.out.println("Error al guardar la configuración");
                }
            } catch (Exception e) {
                System.out.println("Error al guardar la configuración: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar la configuración: " + e.getMessage());
            }
      }

      private void cargarEscenaInicio() {
          try {
              FXMLLoader fxmlLoader = new FXMLLoader(
                      AppInitializer.class.getResource("/com/sentinel/sentinel_pm/Inicio.fxml"));
              Parent root = fxmlLoader.load();
              Scene scene = new Scene(root);
              scene.setFill(Color.TRANSPARENT);

              Stage stage = (Stage) saveConf.getScene().getWindow();
              stage.setScene(scene);
              stage.setResizable(false);

              stage.show();
          } catch (IOException e) {
              System.out.println("Error al cargar la nueva escena: " + e.getMessage());
              e.printStackTrace();
              mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la pantalla de inicio: " + e.getMessage());
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
      
      private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
      }
}