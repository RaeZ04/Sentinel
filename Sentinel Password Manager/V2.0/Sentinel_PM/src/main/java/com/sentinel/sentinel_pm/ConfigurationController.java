package com.sentinel.sentinel_pm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ConfigurationController {

    @FXML
    private TextField rutaArchivoTextBox;

    @FXML
    private Button botonBuscarCarpeta;

    @FXML
    private void initialize() {
        DirectoryChooser selectorDirectorio = new DirectoryChooser();
        selectorDirectorio.setTitle("Selecciona la carpeta donde se almacenara el archivo (elija un lugar seguro)");

        File directorioSeleccionado = selectorDirectorio.showDialog(new Stage());

        if(directorioSeleccionado != null) {
            rutaArchivoTextBox.setText(directorioSeleccionado.getAbsolutePath());
        }

    }

}
