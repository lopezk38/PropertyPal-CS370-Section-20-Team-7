module com.propertypal.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.web;
   	requires org.apache.httpcomponents.httpclient;
    requires com.propertypal.shared.network;
    requires jdk.compiler;
    requires java.desktop;

    opens com.propertypal.client to javafx.fxml;
    opens main.java.com.propertypal.client to javafx.fxml;
    exports com.propertypal.client;
}