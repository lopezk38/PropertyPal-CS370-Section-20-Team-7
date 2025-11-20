module com.propertypal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.propertypal to javafx.fxml;
    exports com.propertypal;
}