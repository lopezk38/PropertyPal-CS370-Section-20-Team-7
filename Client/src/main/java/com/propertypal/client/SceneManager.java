package com.propertypal.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager
{
    private static Stage primaryStage;

    private static Scene currentScene;  // Store current scene

    public static void init(Stage stage, Scene scene) {
        primaryStage = stage;
        currentScene = scene;
    }
    public static void setStage(Stage stage)
    {
        primaryStage = stage;

        // Set the minimum size constraints
        primaryStage.setMinWidth(1280); // Minimum width
        primaryStage.setMinHeight(720); // Minimum height

        // Initialize the scene with the default size (1280x720)
        currentScene = new Scene(new Parent() {}, 1280, 720);
        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    public static void switchTo(String fxml)
    {
        try
        {
            // If the scene is not initialized yet, just use the primaryStage size
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();

            if (currentWidth == 0 || currentHeight == 0)
            {
                currentWidth = 1280; // Default width
                currentHeight = 720; // Default height
            }

            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent root = loader.load();

            // Set the new content in the existing scene
            currentScene.setRoot(root);
            primaryStage.setWidth(currentWidth);
            primaryStage.setHeight(currentHeight);

            // Optional: Update the scene size just to ensure it reflects the stage size
            primaryStage.sizeToScene();

            //debugging
            System.out.println("currentScene is: " + currentScene);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}