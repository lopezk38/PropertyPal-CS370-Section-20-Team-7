package com.propertypal.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        // Login page
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
