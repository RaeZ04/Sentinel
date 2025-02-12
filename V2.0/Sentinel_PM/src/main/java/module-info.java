module com.sentinel.sentinel_pm {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.sentinel.sentinel_pm to javafx.fxml;
    exports com.sentinel.sentinel_pm;
}