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
        //TEMPORARY HELLO WINDOW TESTBENCH
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        //END TESTBENCH
    }
}
