module com.sentinel.sentinel_pm {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.sentinel.sentinel_pm to javafx.fxml;
    exports com.sentinel.sentinel_pm;
    exports com.sentinel.sentinel_pm.entidadesJson to com.fasterxml.jackson.databind;
}