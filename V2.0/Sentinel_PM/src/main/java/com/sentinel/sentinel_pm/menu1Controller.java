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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sentinel.sentinel_pm.cifrado.utilesCifrado;

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
    private Button borrarClasesBoton;

    @FXML
    private GridPane grid;

    private double lastY;

    //--------------------RUTAS DE ARCHIVOS--------------------------------------//
    String ruta = obtenerRutaJSON();//obtener desde json
    String rutaTemp = ruta+"/acc.json";//obtener desde json
    String rutaConfig = "config.json"; //ruta archivo configuracion
    //-------------------------------------------------------------------------------//

    @SuppressWarnings("rawtypes")
    private ComboBox dropdownUsuarios = new ComboBox();// Desplegable para mostrar las cuentas

    //=========================================INITIALIZE=======================================================//
    @SuppressWarnings("unchecked")
    @FXML
    protected void initialize() {
        //comprobar existencia de JSON si no existe lo crea con el formato inicial
        File file = new File(rutaTemp);
        if (!file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode rootNode = objectMapper.createObjectNode();
                rootNode.set("cuentas", objectMapper.createArrayNode());
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error al crear el archivo JSON");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }

        //al iniciar carga todos las etiquetas con los nombres de las cuentas
        cargarItemsEnDropdown(dropdown);

        //----------------------------CONFIGURACION DE BOTONES BARRA DE ARRIBA------------------------------------//
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
            try {
                utilesCifrado.encryptFile(rutaTemp);
                utilesCifrado.encryptFile(rutaConfig);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error al encriptar el archivo");
                alert.setContentText(ex.getMessage());
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
        //-----------------------------------------------------------------------------------------------------------------//

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
                    guardarItemEnArchivo(item);
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

        //boton para borrar clases del JSON
        borrarClasesBoton.setOnAction(e ->{
            borrarClases();
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
                actualizarContraseña(dropdownAct, dropdownCuentasAct, textField, textFieldContrasena);
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
            tempStage.setWidth(400);
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

            // Botón para generar contraseña aleatoria
            Button generarPassButton = new Button("Generar");
            generarPassButton.setLayoutX(310);
            generarPassButton.setLayoutY(175);
            generarPassButton.setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;");
            generarPassButton.setCursor(Cursor.HAND);
            generarPassButton.setOnMouseEntered(event -> generarPassButton
                    .setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: white; -fx-border-radius: 20px;"));
            generarPassButton.setOnMouseExited(event -> generarPassButton
                    .setStyle("-fx-background-color: #162031; -fx-text-fill: white; -fx-border-radius: 20px;"));

            // Evento del botón para generar contraseña aleatoria
            generarPassButton.setOnAction(event -> {
                String passAleatoria = generarPassAleatoria();
                textFieldContrasena.setText(passAleatoria);
            });

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
            root.getChildren().add(generarPassButton); // Asegúrate de agregar el botón al Group
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
    };
    //================================FIN DE INITIALIZE=======================================================================//





    //===============================METODOS=============================================================================//
    // Guardar Cuentas en el archivo JSON
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
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;

            // Leer el archivo JSON
            if (file.exists()) {
                rootNode = objectMapper.readTree(file);
            } else {
                rootNode = objectMapper.createObjectNode();
            }

            // Obtener el nodo de cuentas
            ArrayNode cuentasNode = (ArrayNode) rootNode.path("cuentas");

            // Buscar si ya existe la clase en el JSON
            boolean claseEncontrada = false;

            for (JsonNode cuentaNode : cuentasNode) {
                if (cuentaNode.has(dropdownVar.getValue())) {
                    ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(dropdownVar.getValue());
                    ObjectNode nuevaCuenta = objectMapper.createObjectNode();
                    nuevaCuenta.put(usernameField, passwordField);
                    cuentasArray.add(nuevaCuenta);
                    claseEncontrada = true;
                    break;
                }
            }

            // Si no se encontró la clase, añadir una nueva
            if (!claseEncontrada) {
                ObjectNode nuevaClase = objectMapper.createObjectNode();
                ArrayNode cuentasArray = objectMapper.createArrayNode();
                ObjectNode nuevaCuenta = objectMapper.createObjectNode();
                nuevaCuenta.put(usernameField, passwordField);
                cuentasArray.add(nuevaCuenta);
                nuevaClase.set(dropdownVar.getValue(), cuentasArray);
                cuentasNode.add(nuevaClase);
            }

            // Escribir el archivo JSON actualizado
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
        }
    }

    // metodo para guardar items en archivo JSON
    private void guardarItemEnArchivo(String item) {
        String filePath = rutaTemp;
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;

        try {
            // Leer el archivo JSON
            if (file.exists()) {
                rootNode = objectMapper.readTree(file);
            } else {
                rootNode = objectMapper.createObjectNode();
            }

            // Obtener el nodo de cuentas
            ArrayNode cuentasNode = (ArrayNode) rootNode.path("cuentas");

            // Buscar si ya existe la clase en el JSON
            boolean claseEncontrada = false;

            for (JsonNode cuentaNode : cuentasNode) {
                if (cuentaNode.has(item)) {
                    claseEncontrada = true;
                    break;
                }
            }

            // Si no se encontró la clase, añadir una nueva
            if (!claseEncontrada) {
                ObjectNode nuevaClase = objectMapper.createObjectNode();
                ArrayNode cuentasArray = objectMapper.createArrayNode();
                nuevaClase.set(item, cuentasArray);
                cuentasNode.add(nuevaClase);
            }

            // Escribir el archivo JSON actualizado
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al guardar el item");
            alert.showAndWait();
        }
    }

    // metodo para cargar items en dropdown segun inicia la aplicacion (JSON)
    private void cargarItemsEnDropdown(ComboBox<String> dropdownArg) {

        dropdownArg.getItems().clear(); // Limpiar el dropdown antes de cargar los nuevos elementos

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(rutaTemp);

            if (!file.exists()) {
                throw new FileNotFoundException("El archivo JSON no existe: " + rutaTemp);
            }

            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode cuentasNode = rootNode.path("cuentas");

            if (cuentasNode.isArray()) {
                for (JsonNode cuentaNode : cuentasNode) {
                    cuentaNode.fieldNames().forEachRemaining(cuenta -> {
                        dropdownArg.getItems().add(cuenta);
                    });
                }
            }

            if (dropdownArg.getItems().isEmpty()) {
                dropdownArg.setPromptText("No hay clases añadidas");
                dropdownArg.setDisable(true); // Deshabilitar el dropdown si no hay elementos
            } else {
                dropdownArg.setPromptText("Selecciona una clase");
                dropdownArg.setDisable(false); // Habilitar el dropdown si hay elementos
            }
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Archivo JSON no encontrado");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al cargar los items (dropdown)");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    //metodo para actualizar la contrasena en el JSON
    public void actualizarContraseña(ComboBox<String> primerDropdown, ComboBox<String> segundoDropdown,
            @SuppressWarnings("exports") TextField nuevoUsuario,
            @SuppressWarnings("exports") TextField nuevaContraseña) {
        String apartadoSeleccionado = primerDropdown.getValue(); //coje el nombre del nodo del primer dropdown
        String cuentaSeleccionada = segundoDropdown.getValue(); //coje el usuario del segundo dropdown
        String nuevoUsuarioTexto = nuevoUsuario.getText(); //coje el texto del usuario
        String nuevaContraseñaTexto = nuevaContraseña.getText(); //coje el texto de la passwd

        // buscar cuenta en archivo y actualizarla
        File file = new File(rutaTemp);
        try {
            //abre el json y busca el nodo cuentas
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode cuentasNode = rootNode.path("cuentas");

            //verifica si es un array y lo recorre
            if (cuentasNode.isArray()) {
                for (JsonNode cuentaNode : cuentasNode) {
                    //hace un bucle buscando los nodos, si algun nodo coincide con el seleccionado en el primer dropdown entra
                    if (cuentaNode.has(apartadoSeleccionado)) {
                        ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(apartadoSeleccionado);
                        //bucle para recorrer dentro del apartado seleccionado en el pirmer dropdown
                        for (JsonNode cuenta : cuentasArray) {
                            if (cuenta.has(cuentaSeleccionada)) {//si tiene la cuenta seleccionada recoje el texto
                                ObjectNode cuentaObject = (ObjectNode) cuenta;
                                String usuarioActual = cuentaSeleccionada;
                                String contrasenaActual = cuentaObject.get(cuentaSeleccionada).asText();

                                // Actualizar usuario y contraseña
                                String usuarioFinal = nuevoUsuarioTexto.isEmpty() ? usuarioActual : nuevoUsuarioTexto;
                                String contrasenaFinal = nuevaContraseñaTexto.isEmpty() ? contrasenaActual : nuevaContraseñaTexto;

                                cuentaObject.remove(cuentaSeleccionada);
                                cuentaObject.put(usuarioFinal, contrasenaFinal);
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            // Escribir el archivo JSON actualizado
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);

        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Archivo JSON no encontrado");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al actualizar la contraseña");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // metodo para mostrar las contrasenas en el scroll pane central cuando se pulsa en mostrar (JSON)
    public void mostrarContrasenas(ComboBox<String> dropdownString) {
        scrollpane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            lastY = event.getY();
            scrollpane.setCursor(Cursor.CLOSED_HAND);
        });
        scrollpane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            double deltaY = lastY - event.getY();
            lastY = event.getY();
            scrollpane.setVvalue(
                    scrollpane.getVvalue() + 2.75 * deltaY / scrollpane.getContent().getBoundsInLocal().getHeight());
        });
        scrollpane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            scrollpane.setCursor(Cursor.DEFAULT);
        });

        String opcionMostrar = dropdownString.getValue();

        File file = new File(rutaTemp);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode cuentasNode = rootNode.path("cuentas");

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(10, 20, 10, 20));
            vbox.setSpacing(10); // Añadir espacio entre los elementos del VBox
            vbox.setMaxWidth(Double.MAX_VALUE);
            vbox.setMinWidth(scrollpane.getWidth());//establecer el ancho del vbox que muestra las cuentas al ancho del scrollpane

            boolean encontrado = false;

            if (cuentasNode.isArray()) {
                for (JsonNode cuentaNode : cuentasNode) {
                    if (cuentaNode.has(opcionMostrar)) {
                        encontrado = true;
                        ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(opcionMostrar);
                        for (JsonNode cuenta : cuentasArray) {
                            cuenta.fields().forEachRemaining(entry -> {
                                String usuario = entry.getKey();
                                String contrasena = entry.getValue().asText();
                                String texto = usuario + ": " + contrasena;

                                Label label = new Label(texto);
                                Button eliminarButton = new Button("Eliminar");
                                eliminarButton.setStyle(
                                        "-fx-background-color: #3b4a6b; -fx-text-fill: white; -fx-border-radius: 5px;");
                                eliminarButton.setOnAction(event -> {
                                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                                    confirmDialog.setTitle("Confirmación de eliminación");
                                    confirmDialog.setHeaderText("¿Estás seguro de que deseas eliminar esta cuenta?");
                                    confirmDialog.setContentText("Usuario: " + usuario);

                                    ButtonType buttonTypeYes = new ButtonType("Sí");
                                    ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                                    confirmDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                                    confirmDialog.showAndWait().ifPresent(response -> {
                                        if (response == buttonTypeYes) {
                                            borrarCuentas(opcionMostrar, usuario);
                                            mostrarContrasenas(dropdownString);
                                        }
                                    });
                                });

                                HBox hbox = new HBox(10); // Crear un HBox para contener el label y el botón
                                hbox.setAlignment(Pos.CENTER);
                                hbox.getChildren().addAll(label, eliminarButton);
                                hbox.setStyle(
                                        "-fx-border-color: transparent transparent rgba(255, 255, 255, 0.5) transparent; -fx-border-width: 0 0 1px 0;");
                                hbox.setPadding(new Insets(12, 0, 12, 0)); // Agregar padding inferior de 15px
                                hbox.setMaxWidth(Double.MAX_VALUE); // Extender el HBox hasta los laterales
                                HBox.setHgrow(label, Priority.ALWAYS); // Permitir que el label crezca
                                vbox.getChildren().add(hbox); // Agregar el HBox al VBox
                            });
                        }
                        break;
                    }
                }
            }

            if (!encontrado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText("No se encontraron contraseñas para la clase seleccionada");
                alert.showAndWait();
            }

            // Desactivar el desplazamiento horizontal
            scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Limitar el ancho del VBox al ancho del ScrollPane
            vbox.setMaxWidth(scrollpane.getWidth());

            // Centrar el VBox dentro del ScrollPane
            StackPane centeredPane = new StackPane(vbox);
            centeredPane.setAlignment(Pos.TOP_CENTER); // Asegurar que el VBox se alinee en la parte superior
            centeredPane.setMaxWidth(Double.MAX_VALUE); // Extender el StackPane hasta los laterales
            scrollpane.setContent(centeredPane);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al mostrar las contraseñas");
            alert.showAndWait();
        }
    }

    //metodo para cargar las cuentas y contrasenas en el dropdown de actualizar contrasenas
    private void cargarItemsEnDropdownMinusculas(ComboBox<String> segundoDropdown, ComboBox<String> primerDropdown) {
        segundoDropdown.getItems().clear();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(rutaTemp);

            if (!file.exists()) {
                throw new FileNotFoundException("El archivo JSON no existe: " + rutaTemp);
            }

            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode cuentasNode = rootNode.path("cuentas");

            if (cuentasNode.isArray()) {
                for (JsonNode cuentaNode : cuentasNode) {
                    if (cuentaNode.has(primerDropdown.getValue())) {
                        ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(primerDropdown.getValue());
                        for (JsonNode cuenta : cuentasArray) {
                            cuenta.fieldNames().forEachRemaining(segundoDropdown.getItems()::add);
                        }
                        break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Archivo JSON no encontrado");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al cargar los items");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    //metodo para obtener la ruta del json
    private String obtenerRutaJSON(){
        try{
            // Leer la ruta desde el archivo JSON
            String jsonFilePath = "config.json";
            File jsonFile = new File(jsonFilePath);

            if(jsonFile.exists()){
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonFile);
                String ruta = jsonNode.get("ruta").asText();
                return ruta;
            }
            return "";

        }catch(Exception e){
            return null;
        }
    }

    // borrar cuentas de JSON
    private void borrarCuentas(String clase, String cuenta) {
        File file = new File(rutaTemp);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode cuentasNode = rootNode.path("cuentas");

            if (cuentasNode.isArray()) {
                for (JsonNode cuentaNode : cuentasNode) {
                    if (cuentaNode.has(clase)) {
                        ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(clase);
                        for (int i = 0; i < cuentasArray.size(); i++) {
                            JsonNode cuentaJson = cuentasArray.get(i);
                            if (cuentaJson.has(cuenta)) {
                                cuentasArray.remove(i);
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            // Escribir el archivo JSON actualizado
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);

        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Archivo JSON no encontrado");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error al borrar la cuenta");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    //boton borrar clases JSON
    private void borrarClases() {
        String claseSeleccionada = dropdown.getValue(); // Obtener la clase seleccionada del dropdown

        if (claseSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("No hay clase seleccionada");
            alert.setContentText("Por favor, selecciona una clase para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmación de eliminación");
        confirmDialog.setHeaderText("¿Estás seguro de que deseas eliminar esta clase?");
        confirmDialog.setContentText("Clase: " + claseSeleccionada);

        ButtonType buttonTypeYes = new ButtonType("Sí");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                File file = new File(rutaTemp);
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(file);
                    JsonNode cuentasNode = rootNode.path("cuentas");

                    if (cuentasNode.isArray()) {
                        for (int i = 0; i < cuentasNode.size(); i++) {
                            JsonNode cuentaNode = cuentasNode.get(i);
                            if (cuentaNode.has(claseSeleccionada)) {
                                ((ArrayNode) cuentasNode).remove(i);
                                break;
                            }
                        }
                    }

                    // Escribir el archivo JSON actualizado
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);

                    // Actualizar el dropdown después de borrar la clase
                    cargarItemsEnDropdown(dropdown);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Información");
                    alert.setHeaderText("Clase eliminada");
                    alert.setContentText("La clase " + claseSeleccionada + " ha sido eliminada.");
                    alert.showAndWait();

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Error al borrar la clase");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    // Función para generar una contraseña aleatoria
    private String generarPassAleatoria() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    //=============================== FIN METODOS=========================================================================//
}