package com.sentinel.sentinel_pm;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sentinel.sentinel_pm.config.ConfigManager;

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
    String ruta = ConfigManager.obtenerRuta(); // Obtener ruta desde el nuevo ConfigManager
    String rutaTemp = ruta+"/acc.json"; // Ruta del archivo de cuentas
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
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al crear el archivo JSON: " + e.getMessage());
            }
        }

        //al iniciar carga todos las etiquetas con los nombres de las cuentas
        cargarItemsEnDropdown(dropdown);

        //----------------------------CONFIGURACION DE BOTONES BARRA DE ARRIBA------------------------------------//
        // Botón de salir
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0)));
            timeline.setOnFinished(e -> {
                Platform.exit();
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
        // Botón Actualizar Contraseña - Usar el nuevo FXML
        actualizarContraseña.setOnAction(evento -> {
            try {
                // Cargar el archivo FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentinel/sentinel_pm/ActualizarPasswordPopup.fxml"));
                Parent root = loader.load();
                
                // Crear la escena y el escenario
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                
                Stage popupStage = new Stage();
                popupStage.initStyle(StageStyle.TRANSPARENT);
                popupStage.setScene(scene);
                popupStage.setResizable(false);
                
                // Hacer que la ventana sea arrastrable
                final double[] xOffset = new double[1];
                final double[] yOffset = new double[1];
                root.setOnMousePressed(event -> {
                    xOffset[0] = event.getSceneX();
                    yOffset[0] = event.getSceneY();
                });
                root.setOnMouseDragged(event -> {
                    popupStage.setX(event.getScreenX() - xOffset[0]);
                    popupStage.setY(event.getScreenY() - yOffset[0]);
                });
                
                // Obtener referencias a componentes del FXML
                ComboBox<String> clasesComboBox = (ComboBox<String>) scene.lookup("#clasesComboBox");
                ComboBox<String> cuentasComboBox = (ComboBox<String>) scene.lookup("#cuentasComboBox");
                TextField nuevoUsuarioField = (TextField) scene.lookup("#nuevoUsuarioField");
                TextField nuevaContrasenaField = (TextField) scene.lookup("#nuevaContrasenaField");
                Button aceptarButton = (Button) scene.lookup("#aceptarButton");
                Button cancelarButton = (Button) scene.lookup("#cancelarButton");
                
                // Cargar datos en los combobox
                cargarItemsEnDropdown(clasesComboBox);
                
                // Añadir comportamiento a los combobox
                clasesComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarItemsEnDropdownMinusculas(cuentasComboBox, clasesComboBox);
                    }
                });
                
                // Configurar comportamiento para los botones
                aceptarButton.setOnAction(event -> {
                    actualizarContraseña(clasesComboBox, cuentasComboBox, nuevoUsuarioField, nuevaContrasenaField);
                    popupStage.close();
                });
                
                cancelarButton.setOnAction(event -> {
                    popupStage.close();
                });
                
                popupStage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar la ventana de actualizar contraseña: " + e.getMessage());
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        dropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                cargarItemsEnDropdownMinusculas(dropdownUsuarios, dropdown);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Botón Añadir Cuenta - Usar el nuevo FXML
        añadirCuenta.setOnAction(d -> {
            try {
                // Cargar el archivo FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentinel/sentinel_pm/AnadirCuentaPopup.fxml"));
                Parent root = loader.load();
                
                // Crear la escena y el escenario
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                
                Stage popupStage = new Stage();
                popupStage.initStyle(StageStyle.TRANSPARENT);
                popupStage.setScene(scene);
                popupStage.setResizable(false);
                
                // Hacer que la ventana sea arrastrable
                final double[] xOffset = new double[1];
                final double[] yOffset = new double[1];
                root.setOnMousePressed(event -> {
                    xOffset[0] = event.getSceneX();
                    yOffset[0] = event.getSceneY();
                });
                root.setOnMouseDragged(event -> {
                    popupStage.setX(event.getScreenX() - xOffset[0]);
                    popupStage.setY(event.getScreenY() - yOffset[0]);
                });
                
                // Obtener referencias a componentes del FXML
                ComboBox<String> clasesComboBox = (ComboBox<String>) scene.lookup("#clasesComboBox");
                TextField usuarioField = (TextField) scene.lookup("#usuarioField");
                TextField contrasenaField = (TextField) scene.lookup("#contrasenaField");
                Button generarButton = (Button) scene.lookup("#generarButton");
                Button aceptarButton = (Button) scene.lookup("#aceptarButton");
                Button cancelarButton = (Button) scene.lookup("#cancelarButton");
                
                // Cargar datos en el combobox
                cargarItemsEnDropdown(clasesComboBox);
                
                // Configurar comportamiento para los botones
                generarButton.setOnAction(event -> {
                    contrasenaField.setText(generarPassAleatoria());
                });
                
                aceptarButton.setOnAction(event -> {
                    try {
                        guardarCuentas(clasesComboBox, usuarioField.getText(), contrasenaField.getText());
                        popupStage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar la cuenta: " + e.getMessage());
                    }
                });
                
                cancelarButton.setOnAction(event -> {
                    popupStage.close();
                });
                
                popupStage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar la ventana de añadir cuenta: " + e.getMessage());
            }
        });
    };
    //================================FIN DE INITIALIZE=======================================================================//

    //===============================METODOS=============================================================================//
    // Métodos auxiliares
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Guardar Cuentas en el archivo JSON
    public void guardarCuentas(ComboBox<String> dropdownVar, String usernameField, String passwordField) throws IOException {
        // comprueba si existen elementos en el dropdown
        if (dropdownVar.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No puedes añadir una cuenta a algo vacío");
            return;
        }
    
        if (usernameField == null || usernameField.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El nombre de usuario no puede estar vacío");
            return;
        }
        
        File file = new File(rutaTemp);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(file);
    
        // Obtener el nodo de cuentas
        ArrayNode cuentasNode = (ArrayNode) rootNode.path("cuentas");
    
        // Buscar si ya existe la clase en el JSON
        boolean claseEncontrada = false;
    
        for (JsonNode cuentaNode : cuentasNode) {
            if (cuentaNode.has(dropdownVar.getValue())) {
                ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(dropdownVar.getValue());
                
                // Verificar si ya existe una cuenta con el mismo nombre de usuario
                boolean cuentaExistente = false;
                for (JsonNode cuenta : cuentasArray) {
                    if (cuenta.has(usernameField)) {
                        cuentaExistente = true;
                        // Preguntar al usuario si desea sobrescribir
                        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmDialog.setTitle("Cuenta existente");
                        confirmDialog.setHeaderText("Ya existe una cuenta con ese nombre de usuario");
                        confirmDialog.setContentText("¿Desea sobrescribir la cuenta existente?");
                        
                        ButtonType buttonTypeYes = new ButtonType("Sí");
                        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                        
                        confirmDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                        
                        Optional<ButtonType> result = confirmDialog.showAndWait();
                        if (result.isPresent() && result.get() == buttonTypeYes) {
                            // Eliminar la cuenta existente
                            for (int i = 0; i < cuentasArray.size(); i++) {
                                if (cuentasArray.get(i).has(usernameField)) {
                                    ((ObjectNode) cuentasArray.get(i)).put(usernameField, passwordField);
                                    try {
                                        // Escribir el archivo JSON actualizado
                                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
                                        
                                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cuenta actualizada correctamente");
                                    } catch (IOException e) {
                                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar la cuenta: " + e.getMessage());
                                    }
                                    break;
                                }
                            }
                        }
                        return;
                    }
                }
                
                // Si no existe la cuenta, añadirla
                if (!cuentaExistente) {
                    ObjectNode nuevaCuenta = objectMapper.createObjectNode();
                    nuevaCuenta.put(usernameField, passwordField);
                    cuentasArray.add(nuevaCuenta);
                }
                
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
    
        try {
            // Escribir el archivo JSON actualizado si no estamos sobrescribiendo
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
            
            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cuenta guardada correctamente");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar la cuenta: " + e.getMessage());
            throw e; // Re-lanzar la excepción para que sea manejada por el llamador
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar el item: " + e.getMessage());
        }
    }

    // metodo para cargar items en dropdown segun inicia la aplicacion (JSON)
    public void cargarItemsEnDropdown(ComboBox<String> dropdownArg) {
        dropdownArg.getItems().clear(); // Limpiar el dropdown antes de cargar los nuevos elementos

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(rutaTemp);

            if (!file.exists()) {
                throw new FileNotFoundException("El archivo JSON no existe: " + rutaTemp);
            }

            // Leer el contenido del archivo como un String
            String jsonContent = Files.readString(Paths.get(rutaTemp), StandardCharsets.UTF_8);
            
            // Limpiar el contenido de caracteres de control inválidos
            jsonContent = limpiarContenidoJSON(jsonContent);
            
            // Escribir el contenido limpio de vuelta al archivo
            Files.writeString(Paths.get(rutaTemp), jsonContent, StandardCharsets.UTF_8);
            
            // Ahora analizar el JSON limpio
            JsonNode rootNode = objectMapper.readTree(jsonContent);
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Archivo JSON no encontrado: " + e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los items (dropdown): " + e.getMessage());
        }
    }
    
    /**
     * Limpia el contenido JSON de caracteres de control inválidos
     * @param json El contenido JSON original
     * @return El contenido JSON limpio
     */
    private String limpiarContenidoJSON(String json) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            // Permitir solo caracteres válidos para JSON:
            // - Espacios en blanco regulares (\r, \n, \t, espacio)
            // - Caracteres imprimibles (código ASCII 32-126)
            if (c == '\r' || c == '\n' || c == '\t' || c == ' ' || (c >= 32 && c <= 126)) {
                sb.append(c);
            }
        }
        return sb.toString();
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
            
            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Contraseña actualizada correctamente");

        } catch (FileNotFoundException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Archivo JSON no encontrado: " + e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar la contraseña: " + e.getMessage());
        }
    }

    // metodo para mostrar las contrasenas en el scroll pane central cuando se pulsa en mostrar (JSON)
    public void mostrarContrasenas(ComboBox<String> dropdownString) {
        // Configurar el comportamiento del desplazamiento del ScrollPane
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
        
        // Si no hay clase seleccionada, mostrar mensaje
        if (opcionMostrar == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Por favor, selecciona una clase primero");
            return;
        }

        // Desactivar el desplazamiento horizontal de manera explícita (aplicar SIEMPRE al principio)
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setFitToWidth(true);
        scrollpane.setPannable(false);

        File file = new File(rutaTemp);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode cuentasNode = rootNode.path("cuentas");

            // Crear un contenedor principal para todas las cuentas con estilos mejorados
            VBox contenedorPrincipal = new VBox();
            contenedorPrincipal.getStyleClass().add("accounts-container");
            contenedorPrincipal.setAlignment(Pos.TOP_CENTER);
            contenedorPrincipal.setPadding(new Insets(10));
            contenedorPrincipal.setSpacing(15);
            
            // Establecer ancho FIJO para evitar el scroll horizontal
            double anchoDisponible = scrollpane.getWidth() - 25; // Restamos para barra de scroll y margen
            contenedorPrincipal.setMinWidth(anchoDisponible);
            contenedorPrincipal.setPrefWidth(anchoDisponible);
            contenedorPrincipal.setMaxWidth(anchoDisponible);
            
            // Crear un HBox para el título con un ícono o gráfico
            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.setSpacing(10);
            headerBox.setPadding(new Insets(0, 0, 12, 0));
            headerBox.setPrefWidth(anchoDisponible);
            headerBox.setMaxWidth(anchoDisponible);
            
            // Añadir icono (puedes cambiarlo por cualquier otro de los disponibles en Media/)
            try {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/sentinel/sentinel_pm/Media/icon1.png")));
                icon.setFitHeight(24);
                icon.setFitWidth(24);
                headerBox.getChildren().add(icon);
            } catch (Exception e) {
                // Si no se puede cargar el icono, continuamos sin él
            }
            
            // Añadir título (con alineación vertical ajustada)
            Label tituloSeccion = new Label("Cuentas de " + opcionMostrar);
            tituloSeccion.getStyleClass().add("account-section-title");
            
            // Ajustar la alineación vertical del texto con el icono usando propiedades de alineación directamente
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            // Crear un contenedor para ajustar el título verticalmente
            StackPane titleContainer = new StackPane(tituloSeccion);
            titleContainer.setAlignment(Pos.CENTER_LEFT);
            titleContainer.setPadding(new Insets(5, 0, 0, 0));
            
            headerBox.getChildren().add(titleContainer);
            
            // Añadir el header al contenedor principal
            contenedorPrincipal.getChildren().add(headerBox);
            
            // Añadir un borde inferior sutil (reemplaza el separador)
            headerBox.setStyle("-fx-border-color: transparent transparent rgba(255,255,255,0.3) transparent; -fx-border-width: 0 0 1px 0;");

            boolean encontrado = false;

            if (cuentasNode.isArray()) {
                for (JsonNode cuentaNode : cuentasNode) {
                    if (cuentaNode.has(opcionMostrar)) {
                        encontrado = true;
                        ArrayNode cuentasArray = (ArrayNode) cuentaNode.get(opcionMostrar);
                        
                        // Si no hay cuentas, mostrar un mensaje
                        if (cuentasArray.size() == 0) {
                            Label sinCuentas = new Label("No hay cuentas guardadas en esta clase");
                            sinCuentas.getStyleClass().add("no-accounts-message");
                            contenedorPrincipal.getChildren().add(sinCuentas);
                        }
                        
                        // Contador para animación escalonada
                        final int[] contador = {0};
                        
                        for (JsonNode cuenta : cuentasArray) {
                            cuenta.fields().forEachRemaining(entry -> {
                                String usuario = entry.getKey();
                                String contrasena = entry.getValue().asText();
                                
                                // Crear un ítem de cuenta con efecto de tarjeta
                                VBox itemCuenta = new VBox();
                                itemCuenta.getStyleClass().add("account-item");
                                itemCuenta.setMaxWidth(anchoDisponible);
                                itemCuenta.setPrefWidth(anchoDisponible);
                                itemCuenta.setSpacing(8);
                                
                                // Aplicar animación de entrada con retardo escalonado
                                // basado en la posición del elemento
                                contador[0]++;
                                PauseTransition delay = new PauseTransition(Duration.millis(contador[0] * 50));
                                delay.setOnFinished(event -> {
                                    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), itemCuenta);
                                    fadeIn.setFromValue(0);
                                    fadeIn.setToValue(1);
                                    fadeIn.play();
                                });
                                itemCuenta.setOpacity(0);
                                delay.play();
                                
                                // Nombre de usuario destacado
                                Label usuarioLabel = new Label("Usuario:");
                                usuarioLabel.getStyleClass().add("account-label");
                                
                                Label usuarioValor = new Label(usuario);
                                usuarioValor.getStyleClass().add("account-username");
                                
                                HBox usuarioContainer = new HBox(10);
                                usuarioContainer.setAlignment(Pos.CENTER_LEFT);
                                usuarioContainer.getChildren().addAll(usuarioLabel, usuarioValor);
                                
                                // Contraseña con opción para mostrar/ocultar
                                Label contrasenaLabel = new Label("Contraseña:");
                                contrasenaLabel.getStyleClass().add("account-label");
                                
                                Label contrasenaValor = new Label("••••••••");
                                contrasenaValor.getStyleClass().add("account-password");
                                
                                ToggleButton mostrarContrasenaBtn = new ToggleButton("Mostrar");
                                mostrarContrasenaBtn.getStyleClass().add("account-action-button");
                                mostrarContrasenaBtn.setCursor(Cursor.HAND);
                                mostrarContrasenaBtn.setOnAction(event -> {
                                    if (mostrarContrasenaBtn.isSelected()) {
                                        contrasenaValor.setText(contrasena);
                                        contrasenaValor.getStyleClass().add("password-revealed");
                                        mostrarContrasenaBtn.setText("Ocultar");
                                    } else {
                                        contrasenaValor.setText("••••••••");
                                        contrasenaValor.getStyleClass().remove("password-revealed");
                                        mostrarContrasenaBtn.setText("Mostrar");
                                    }
                                });
                                
                                HBox contrasenaContainer = new HBox(10);
                                contrasenaContainer.setAlignment(Pos.CENTER_LEFT);
                                contrasenaContainer.getChildren().addAll(contrasenaLabel, contrasenaValor, mostrarContrasenaBtn);
                                HBox.setHgrow(contrasenaValor, Priority.ALWAYS);
                                
                                // Botones de acción
                                Button copiarContrasenaBtn = new Button("Copiar");
                                copiarContrasenaBtn.getStyleClass().addAll("account-action-button", "account-copy-button");
                                copiarContrasenaBtn.setCursor(Cursor.HAND);
                                copiarContrasenaBtn.setOnAction(event -> {
                                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                                    final ClipboardContent content = new ClipboardContent();
                                    content.putString(contrasena);
                                    clipboard.setContent(content);
                                    
                                    // Cambiar temporalmente el texto del botón para confirmar
                                    copiarContrasenaBtn.setText("¡Copiado!");
                                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                                    pause.setOnFinished(e -> copiarContrasenaBtn.setText("Copiar"));
                                    pause.play();
                                });
                                
                                Button eliminarBtn = new Button("Eliminar");
                                eliminarBtn.getStyleClass().addAll("account-action-button", "account-delete-button");
                                eliminarBtn.setCursor(Cursor.HAND);
                                eliminarBtn.setOnAction(event -> {
                                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                                    confirmDialog.setTitle("Confirmación de eliminación");
                                    confirmDialog.setHeaderText("¿Estás seguro de que deseas eliminar esta cuenta?");
                                    confirmDialog.setContentText("Usuario: " + usuario);

                                    ButtonType buttonTypeYes = new ButtonType("Sí");
                                    ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    confirmDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                                    confirmDialog.showAndWait().ifPresent(response -> {
                                        if (response == buttonTypeYes) {
                                            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), itemCuenta);
                                            fadeOut.setFromValue(1.0);
                                            fadeOut.setToValue(0.0);
                                            fadeOut.setOnFinished(e -> {
                                                borrarCuentas(opcionMostrar, usuario);
                                                mostrarContrasenas(dropdownString);
                                            });
                                            fadeOut.play();
                                        }
                                    });
                                });
                                
                                HBox botonesContainer = new HBox(10);
                                botonesContainer.setAlignment(Pos.CENTER_RIGHT);
                                botonesContainer.getChildren().addAll(copiarContrasenaBtn, eliminarBtn);
                                
                                // Añadir todos los elementos al ítem de cuenta
                                itemCuenta.getChildren().addAll(usuarioContainer, contrasenaContainer, botonesContainer);
                                
                                // Añadir el ítem al contenedor principal
                                contenedorPrincipal.getChildren().add(itemCuenta);
                            });
                        }
                        break;
                    }
                }
            }

            if (!encontrado) {
                Label noEncontrado = new Label("No se encontraron cuentas para la clase seleccionada");
                noEncontrado.getStyleClass().add("no-accounts-message");
                contenedorPrincipal.getChildren().add(noEncontrado);
            }

            // Aplicar configuraciones al ScrollPane
            scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            
            // Asegurar que no hay scroll horizontal
            StackPane centeredPane = new StackPane(contenedorPrincipal);
            centeredPane.setAlignment(Pos.TOP_CENTER);
            centeredPane.setMaxWidth(anchoDisponible);
            centeredPane.setPrefWidth(anchoDisponible);
            centeredPane.setMinWidth(anchoDisponible);

            // Agregar el contenedor al ScrollPane
            scrollpane.setContent(centeredPane);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al mostrar las contraseñas: " + e.getMessage());
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Archivo JSON no encontrado: " + e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los items: " + e.getMessage());
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Archivo JSON no encontrado: " + e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar la cuenta: " + e.getMessage());
        }
    }

    //boton borrar clases JSON
    private void borrarClases() {
        String claseSeleccionada = dropdown.getValue(); // Obtener la clase seleccionada del dropdown

        if (claseSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay clase seleccionada. Por favor, selecciona una clase para borrar.");
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

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "La clase " + claseSeleccionada + " ha sido eliminada.");

                } catch (IOException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar la clase: " + e.getMessage());
                }
            }
        });
    }

    // Función para generar una contraseña aleatoria
    private String generarPassAleatoria() {
        int length = 12; // Aumentar la longitud a 12 caracteres para mayor seguridad
        String mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String especiales = "!@#$%^&*()_-+=<>?";
        String chars = mayusculas + minusculas + numeros + especiales;
        
        StringBuilder password = new StringBuilder();
        
        // Asegurarse de incluir al menos un carácter de cada tipo
        password.append(mayusculas.charAt((int) (Math.random() * mayusculas.length())));
        password.append(minusculas.charAt((int) (Math.random() * minusculas.length())));
        password.append(numeros.charAt((int) (Math.random() * numeros.length())));
        password.append(especiales.charAt((int) (Math.random() * especiales.length())));
        
        // Completar el resto de la contraseña
        for (int i = 4; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        // Mezclar los caracteres
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int j = (int) (Math.random() * passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }

    //=============================== FIN METODOS=========================================================================//
}