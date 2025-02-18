package com.sentinel.sentinel_pm;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class menu1Controller {

    @FXML
    private Button añadirClase;

    @FXML
    private ComboBox<String> dropdown;

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button añadirCuenta;

    @FXML
    private Button mostrarContraseña;

    @FXML
    private Button actualizarContraseña;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private GridPane grid;

    private double lastY;

    //////////////////////////////////////////// RUTAS ARCHIVOS DE
    //////////////////////////////////////////// ENCRIPTADO////////////////////////////////////////
    String nombreUsuarioWin = System.getProperty("user.name");
    String ruta = "C:\\Users\\" + nombreUsuarioWin + "\\Documents\\logsPass\\acc.gpg"; //cambiar por variable
    String rutaTemp = "C:\\Users\\" + nombreUsuarioWin + "\\Documents\\logsPass\\acc.txt"; //cambiar por la variable de arriba
    private static String key = "Rammusmaricones.";
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("rawtypes")
    private ComboBox dropdownUsuarios = new ComboBox();// Desplegable para mostrar las cuentas

    @SuppressWarnings("unchecked")
    @FXML
    protected void initialize() {
        cargarItemsEnDropdown(dropdown);

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

        exitButton.setOnAction(event -> {
            String archivoEnTXT = rutaTemp;

            try {
                encryptFile(archivoEnTXT, ruta);
            } catch (Exception d) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error al desencriptar el archivo");
                alert.showAndWait();
            }

            File file = new File(archivoEnTXT);
            if (!file.delete()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error al borrar el archivo");
                alert.showAndWait();
            }

            Platform.exit();
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

        // Mostrar Contraseña
        mostrarContraseña.setOnAction(e -> {
            mostrarContrasenas(dropdown);
        });

        // Añadir Clase
        añadirClase.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Añadir Clase");
            dialog.setHeaderText(null); // Eliminar el texto de cabecera
            dialog.setGraphic(null); // Eliminar el icono

            // Obtener el Stage y establecer el estilo a TRANSPARENT
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.initStyle(StageStyle.TRANSPARENT);

            // Establecer el fondo de la Scene en transparente
            dialog.getDialogPane().getScene().setFill(Color.TRANSPARENT);

            // Crear un controlador de eventos de mouse para hacer la ventana arrastrable
            final double[] xOffset = new double[1];
            final double[] yOffset = new double[1];
            dialog.getDialogPane().setOnMousePressed(event -> {
                xOffset[0] = event.getSceneX();
                yOffset[0] = event.getSceneY();
            });
            dialog.getDialogPane().setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset[0]);
                stage.setY(event.getScreenY() - yOffset[0]);
            });

            // Modificar el estilo de la DialogPane
            dialog.getDialogPane().setStyle(
                    "-fx-min-width: 300px; -fx-min-height: 180px; -fx-background-color: #28344c; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-border-color: white; -fx-border-width: 2px;");

            // Modificar el estilo del TextField
            TextField editor = dialog.getEditor();
            editor.setStyle(
                    "-fx-background-color: #28344c; -fx-border-color: white; -fx-border-width: 0 0 1 0; -fx-text-fill: white; -fx-caret-color: white; -fx-font-size: 15px; -fx-caret-blink-rate: 500ms; -fx-pref-height: 20px;");

            // Crear un VBox para contener el Label del nombre, el TextField y el mensaje de
            // error
            VBox vbox = new VBox();
            vbox.setSpacing(10); // Espacio entre los elementos

            // Crear un Label para el nombre
            Label nameLabel = new Label("Clase:");
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px;"); // Establecer el color
            // del texto en blanco
            // y aumentar el tamaño
            // de la fuente

            // Añadir el Label del nombre, el TextField y el mensaje de error al VBox
            vbox.getChildren().addAll(nameLabel, dialog.getEditor());

            // Establecer el VBox como el contenido del DialogPane
            dialog.getDialogPane().setContent(vbox);

            // Modificar el botón de aceptar
            Button acceptButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            acceptButton.addEventFilter(ActionEvent.ACTION, event -> {
                String item = dialog.getEditor().getText();
                if (!item.isEmpty()) {
                    String itemmayusculas = item.toUpperCase();
                    guardarItemEnArchivo(itemmayusculas);
                    cargarItemsEnDropdown(dropdown); // Actualizar el dropdown
                    dialog.close();
                }
            });

            // Modificar el estilo de los botones
            ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
            buttonBar.getButtons().forEach(b -> {
                Button button = (Button) b;
                button.setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;");
                button.setCursor(Cursor.HAND);
                button.setOnMouseEntered(event -> button
                        .setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: white; -fx-border-radius: 20px;"));
                button.setOnMouseExited(event -> button
                        .setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;"));
            });

            dialog.show();
        });
        // Establecer el cursor a mano de botones

        dropdown.setOnMouseEntered(event -> dropdown.getScene().setCursor(Cursor.HAND));
        dropdown.setOnMouseExited(event -> dropdown.getScene().setCursor(Cursor.DEFAULT));

        mostrarContraseña.setOnMouseEntered(event -> mostrarContraseña.getScene().setCursor(Cursor.HAND));
        mostrarContraseña.setOnMouseExited(event -> mostrarContraseña.getScene().setCursor(Cursor.DEFAULT));

        añadirClase.setOnMouseEntered(event -> añadirClase.getScene().setCursor(Cursor.HAND));
        añadirClase.setOnMouseExited(event -> añadirClase.getScene().setCursor(Cursor.DEFAULT));

        actualizarContraseña.setOnMouseEntered(event -> actualizarContraseña.getScene().setCursor(Cursor.HAND));
        actualizarContraseña.setOnMouseExited(event -> actualizarContraseña.getScene().setCursor(Cursor.DEFAULT));

        añadirCuenta.setOnMouseEntered(event -> añadirCuenta.getScene().setCursor(Cursor.HAND));
        añadirCuenta.setOnMouseExited(event -> añadirCuenta.getScene().setCursor(Cursor.DEFAULT));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        actualizarContraseña.setOnAction(evento -> {
            Stage ventanaActualizar = new Stage();
            ventanaActualizar.initStyle(StageStyle.UNDECORATED); // Remove the title bar
            ventanaActualizar.setWidth(370);
            ventanaActualizar.setHeight(230);
            ventanaActualizar.setResizable(false);

            // Crear desplegable
            ComboBox<String> dropdownAct = new ComboBox<>();
            // Establecer posición del desplegable
            dropdownAct.setLayoutX(20);
            dropdownAct.setLayoutY(50);
            dropdownAct.setPromptText("Selecciona una clase");
            dropdownAct.setPrefWidth(160);

            cargarItemsEnDropdown(dropdownAct);

            // crear segundo desplegable
            ComboBox<String> dropdownCuentasAct = new ComboBox<>();
            dropdownCuentasAct.setLayoutX(190);
            dropdownCuentasAct.setLayoutY(50);
            dropdownCuentasAct.setPromptText("Selecciona una cuenta");
            dropdownCuentasAct.setPrefWidth(160);

            Label label = new Label("Usuario:");
            label.setTextFill(Color.WHITE); // Set the text color to white
            TextField textField = new TextField();
            textField.setStyle(
                    "-fx-background-color: transparent; -fx-border-color: transparent transparent white transparent; -fx-text-fill: white;");

            // Establecer posición del campo de texto
            label.setLayoutX(60);
            label.setLayoutY(100);

            // Establecer posición del campo de texto
            textField.setLayoutX(115);
            textField.setLayoutY(90);
            textField.setMinWidth(200);

            Label labelContrasena = new Label("Contraseña:");
            labelContrasena.setTextFill(Color.WHITE); // Set the text color to white
            TextField textFieldContrasena = new TextField();
            textFieldContrasena.setStyle(
                    "-fx-background-color: transparent; -fx-border-color: transparent transparent white transparent; -fx-text-fill: white;");
            // Establecer posición del campo de texto
            labelContrasena.setLayoutX(41);
            labelContrasena.setLayoutY(140);

            // Establecer posición del campo de texto
            textFieldContrasena.setLayoutX(115);
            textFieldContrasena.setLayoutY(130);
            textFieldContrasena.setMinWidth(200);

            // boton aceptar
            Button aceptar = new Button("Aceptar");
            aceptar.setLayoutX(110);
            aceptar.setLayoutY(190);
            aceptar.setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;");
            aceptar.setCursor(Cursor.HAND);
            aceptar.setOnMouseEntered(event -> aceptar
                    .setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: white; -fx-border-radius: 20px;"));
            aceptar.setOnMouseExited(event -> aceptar
                    .setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;"));

            // boton cancelar
            Button cancelar = new Button("Cancelar");
            cancelar.setLayoutX(190);
            cancelar.setLayoutY(190);
            cancelar.setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;");
            cancelar.setCursor(Cursor.HAND);
            cancelar.setOnMouseEntered(event -> cancelar
                    .setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: white; -fx-border-radius: 20px;"));
            cancelar.setOnMouseExited(event -> cancelar
                    .setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;"));

            // funcion boton aceptar
            aceptar.setOnAction(event -> {
                actualizarContraseña(dropdownAct, dropdownCuentasAct, textFieldContrasena, textField);
                ventanaActualizar.close();
            });

            // funcion boton cancelar
            cancelar.setOnAction(event -> {
                ventanaActualizar.close();
            });

            Group root = new Group();
            root.getChildren().add(dropdownAct);
            root.getChildren().add(dropdownCuentasAct);
            root.getChildren().addAll(label, textField);
            root.getChildren().addAll(labelContrasena, textFieldContrasena);
            root.getChildren().add(aceptar);
            root.getChildren().add(cancelar);

            // Create a pane with a white border
            Pane borderedPane = new Pane(root);
            borderedPane.setStyle(
                    "-fx-border-color: white; -fx-border-width: 2; -fx-background-color: #28344c; -fx-border-radius: 20px;");

            Scene scene = new Scene(borderedPane);
            scene.setFill(Color.web("#28344c"));
            ventanaActualizar.setScene(scene);

            // Create a non-focusable container
            Pane nonFocusableContainer = new Pane();
            nonFocusableContainer.setFocusTraversable(true);
            root.getChildren().add(nonFocusableContainer);

            ventanaActualizar.show();

            // Give focus to the non-focusable container
            nonFocusableContainer.requestFocus();

            // Make the window draggable
            final double[] xOffset = new double[1];
            final double[] yOffset = new double[1];
            scene.setOnMousePressed(event -> {
                xOffset[0] = event.getSceneX();
                yOffset[0] = event.getSceneY();
            });
            scene.setOnMouseDragged(event -> {
                ventanaActualizar.setX(event.getScreenX() - xOffset[0]);
                ventanaActualizar.setY(event.getScreenY() - yOffset[0]);
            });

            dropdownAct.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                if (newValue != null) {
                    cargarItemsEnDropdownMinusculas(dropdownCuentasAct, dropdownAct);
                }
            });
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        dropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                cargarItemsEnDropdownMinusculas(dropdownUsuarios, dropdown);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Añadir Cuenta
        añadirCuenta.setOnAction(d -> {
            Stage tempStage = new Stage();
            tempStage.initStyle(StageStyle.UNDECORATED); // Remove the title bar
            tempStage.setWidth(350);
            tempStage.setHeight(300);
            tempStage.setResizable(false);

            // Crear desplegable
            ComboBox<String> dropdown = new ComboBox<>();

            // Establecer posición del desplegable
            dropdown.setLayoutX(90);
            dropdown.setLayoutY(35);
            dropdown.setPromptText("Selecciona una clase");
            dropdown.setPrefWidth(200);

            cargarItemsEnDropdown(dropdown);

            Label label = new Label("Usuario:");
            label.setTextFill(Color.WHITE); // Set label color to white
            TextField textField = new TextField();
            // Establecer posición del campo de texto
            label.setLayoutX(48);
            label.setLayoutY(90);

            // Establecer posición del campo de texto
            textField.setLayoutX(50);
            textField.setLayoutY(110);

            textField.setStyle(
                    "-fx-background-color: transparent; -fx-border-color: transparent transparent white transparent; -fx-text-fill: white; -fx-pref-width: 250;");

            Label labelContrasena = new Label("Contraseña:");
            labelContrasena.setTextFill(Color.WHITE); // Set label color to white
            TextField textFieldContrasena = new TextField();
            textFieldContrasena.setStyle(
                    "-fx-background-color: transparent; -fx-border-color: transparent transparent white transparent; -fx-text-fill: white; -fx-pref-width: 250;");

            // Establecer posición del campo de texto
            labelContrasena.setLayoutX(48);
            labelContrasena.setLayoutY(160);

            // Establecer posición del campo de texto
            textFieldContrasena.setLayoutX(50);
            textFieldContrasena.setLayoutY(175);

            /////////////////////////////////////////// BOTONES ACEPTAR Y
            /////////////////////////////////////////// CANCELAR////////////////////////////////////////
            Button aceptar = new Button("Aceptar");
            aceptar.setLayoutX(110);
            aceptar.setLayoutY(250);
            aceptar.setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;");
            aceptar.setCursor(Cursor.HAND);
            aceptar.setOnMouseEntered(event -> aceptar
                    .setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: white; -fx-border-radius: 20px;"));
            aceptar.setOnMouseExited(event -> aceptar
                    .setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;"));

            // boton cancelar
            Button cancelar = new Button("Cancelar");
            cancelar.setLayoutX(190);
            cancelar.setLayoutY(250);
            cancelar.setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;");
            cancelar.setCursor(Cursor.HAND);
            cancelar.setOnMouseEntered(event -> cancelar
                    .setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: white; -fx-border-radius: 20px;"));
            cancelar.setOnMouseExited(event -> cancelar
                    .setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;"));

            // evento del boton
            aceptar.setOnAction(event -> {
                try {
                    guardarCuentas(dropdown, textField.getText(), textFieldContrasena.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                tempStage.close();
            });

            cancelar.setOnAction(event -> {
                tempStage.close();
            });

            // Agregar cosas a ventana temporal
            Group root = new Group();
            root.getChildren().add(dropdown);
            root.getChildren().addAll(label, textField);
            root.getChildren().addAll(labelContrasena, textFieldContrasena);
            root.getChildren().add(aceptar);
            root.getChildren().add(cancelar);

            // Request focus on the root to remove focus from other elements
            Platform.runLater(() -> root.requestFocus());

            Pane borderedPane = new Pane(root);
            borderedPane.setStyle(
                    "-fx-border-color: white; -fx-border-width: 2; -fx-background-color: #28344c; -fx-border-radius: 20px;");

            Scene scene = new Scene(borderedPane);
            scene.setFill(Color.web("#28344c"));
            tempStage.setScene(scene);

            tempStage.setScene(scene);
            tempStage.show();

            // Make the window draggable
            final double[] xOffset = new double[1];
            final double[] yOffset = new double[1];
            scene.setOnMousePressed(event -> {
                xOffset[0] = event.getSceneX();
                yOffset[0] = event.getSceneY();
            });
            scene.setOnMouseDragged(event -> {
                tempStage.setX(event.getScreenX() - xOffset[0]);
                tempStage.setY(event.getScreenY() - yOffset[0]);
            });

        });

        // Encriptar y desencriptar:
        try {
            encryptFile(rutaTemp, ruta);
            decryptFile(ruta, rutaTemp);
        } catch (Exception s) {
            s.printStackTrace();
        }
    };









    ///////////////////////////////// METODOS DE ENCRIPTADO Y
    ///////////////////////////////// DESENCRIPTADO//////////////////////////////////////////////
    public static void encryptFile(String inputFile, String outputFile) throws Exception {
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputFile));
        byte[] outputBytes = encrypt(inputBytes, key);
        Files.write(Paths.get(outputFile), outputBytes);
    }

    public static void decryptFile(String inputFile, String outputFile) throws Exception {
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputFile));
        byte[] outputBytes = decrypt(inputBytes, key);
        Files.write(Paths.get(outputFile), outputBytes);
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////








    // Guardar Cuentas en el archivo
    public void guardarCuentas(ComboBox<String> dropdownVar, String usernameField, String passwordField)
            throws IOException {
        // comprueba si existen elementos en el dropdown
        if (dropdownVar.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("No puedes añadir una cuenta a algo vacio");
            alert.showAndWait();
        } else { // si existen elementos
            File file = new File(rutaTemp);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));// leer el archivo
                String line;
                StringBuilder fileContent = new StringBuilder();// almacena el contenido del archivo

                while ((line = br.readLine()) != null) {// mientras haya lineas en el archivo
                    fileContent.append(line).append(System.lineSeparator());// añade la linea al contenido del archivo
                    if (line.equals(dropdownVar.getValue().toUpperCase())) {// si la linea es igual al nombre del
                        // dropdown
                        fileContent.append("-").append(usernameField).append(" : ").append(passwordField).append(System.lineSeparator());// añade el usuario y la contraseña
                    }
                }
                br.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(file));// escribir en el archivo
                bw.write(fileContent.toString());
                bw.close();// cerrar el archivo
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error al guardar las cuentas");
                alert.showAndWait();
            }
        }
    }

    // metodo para guardar items en archivo
    private void guardarItemEnArchivo(String item) {
        String filePath = rutaTemp;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(item);
            writer.newLine();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al guardar el item");
            alert.showAndWait();
        }
    }

    // metodo para cargar items en dropdown
    private void cargarItemsEnDropdown(ComboBox<String> dropdown1) {
        String filePath = rutaTemp;
        dropdown1.getItems().clear(); // Limpiar el dropdown antes de cargar los nuevos elementos
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(line.toUpperCase())) {
                    dropdown1.getItems().add(line);
                }
            }
            if (dropdown1.getItems().isEmpty()) {
                dropdown1.setPromptText("No hay clases añadidas");
                dropdown1.setDisable(true); // Deshabilitar el dropdown si no hay elementos
            } else {
                dropdown1.setPromptText("Selecciona una clase");
                dropdown1.setDisable(false); // Habilitar el dropdown si hay elementos
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al cargar los items");
            alert.showAndWait();
        }
    }

    public void actualizarContraseña(ComboBox<String> primerDropdown, ComboBox<String> segundoDropdown, @SuppressWarnings("exports") TextField nuevoUsuario, @SuppressWarnings("exports") TextField nuevaContraseña) {
        String apartadoSeleccionado = primerDropdown.getValue();
        String cuentaSeleccionada = segundoDropdown.getValue();
        String nuevoUsuarioTexto = nuevoUsuario.getText();
        String nuevaContraseñaTexto = nuevaContraseña.getText();

        // buscar cuenta en archivo y actualizarla
        File file = new File(rutaTemp);
        File tempFile = new File("tempFile.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            String line;
            boolean dentroDelApartadoCorrecto = false;

            while ((line = br.readLine()) != null) {
                if (line.equals(apartadoSeleccionado.toUpperCase())) {
                    dentroDelApartadoCorrecto = true;
                } else if (line.equals(line.toUpperCase())) {
                    dentroDelApartadoCorrecto = false;
                }

                if (dentroDelApartadoCorrecto && line.equals(cuentaSeleccionada)) {
                    // Reemplazar la línea con los nuevos valores
                    bw.write("-" + nuevaContraseñaTexto + " : " + nuevoUsuarioTexto);
                    bw.newLine();
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }
            br.close();
            bw.close();

            // Eliminar el archivo original y renombrar el archivo temporal
            file.delete();
            tempFile.renameTo(file);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al actualizar la contraseña");
            alert.showAndWait();
        }
    }

    public void mostrarContrasenas(ComboBox<String> dropdownString) {

        scrollpane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            lastY = event.getY();
            scrollpane.setCursor(Cursor.CLOSED_HAND); // Cambiar el cursor a una mano cerrada cuando se presiona el ratón
        });
        scrollpane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            double deltaY = lastY - event.getY();
            lastY = event.getY();
            scrollpane.setVvalue(scrollpane.getVvalue() + 2.75 * deltaY / scrollpane.getContent().getBoundsInLocal().getHeight());
        });
        scrollpane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            scrollpane.setCursor(Cursor.DEFAULT); // Cambiar el cursor de vuelta al predeterminado cuando se suelta el ratón
        });

        String opcionMostrar = dropdownString.getValue();

        File file = new File(rutaTemp);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            boolean encontrado = false;

            VBox vbox = new VBox(); // Crear un VBox en lugar de un FlowPane
            vbox.setAlignment(Pos.TOP_CENTER); // Centrar el contenido del VBox en la parte superior
            vbox.setPadding(new Insets(0, 70, 10, 70)); // Agregar padding

            while ((line = br.readLine()) != null) {
                line = line.replaceFirst("^- ", ""); // Elimina el guion inicial

                if (encontrado) {
                    if (line.equals(line.toUpperCase())) {
                        break;
                    }

                    Label label = new Label(line);
                    FlowPane flowPane = new FlowPane(); // Crear un nuevo FlowPane para cada label
                    flowPane.setAlignment(Pos.CENTER); // Centrar el contenido del FlowPane
                    flowPane.getChildren().add(label); // Agregar el label al FlowPane
                    flowPane.setStyle(
                            "-fx-border-color: transparent transparent rgba(255, 255, 255, 0.5) transparent; -fx-border-width: 0 0 1px 0;");
                    flowPane.setPadding(new Insets(12, 0, 12, 0)); // Agregar padding inferior de 15px
                    vbox.getChildren().add(flowPane); // Agregar el FlowPane al VBox
                }

                if (line.equals(opcionMostrar)) {
                    encontrado = true;
                }
            }

            // Desactivar el desplazamiento horizontal
            scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            // Limitar el ancho del VBox al ancho del ScrollPane
            vbox.setMaxWidth(scrollpane.getWidth());

            scrollpane.setContent(vbox);

            br.close();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al mostrar las contraseñas");
            alert.showAndWait();
        }
    }

    private void cargarItemsEnDropdownMinusculas(ComboBox<String> segundoDropdown, ComboBox<String> primerDropdown) {
        segundoDropdown.getItems().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaTemp))) {
            String line;
            boolean dentroDelApartado = false;

            while ((line = reader.readLine()) != null) {
                if (primerDropdown.getValue() != null && line.equals(primerDropdown.getValue().toUpperCase())) {
                    dentroDelApartado = true;
                } else if (line.equals(line.toUpperCase())) {
                    dentroDelApartado = false;
                } else if (dentroDelApartado) {
                    segundoDropdown.getItems().add(line);
                }
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al cargar los items");
            alert.showAndWait();
        }
    }

}