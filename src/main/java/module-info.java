module org.example.pruebaexe {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.example.pruebaexe to javafx.fxml;
    exports org.example.pruebaexe;
}