package com.propertypal.client;

import com.propertypal.client.DEMOSelectedTicket;
import com.propertypal.client.SceneManager;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class TicketCreateController
{

    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descArea;

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads
    }

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketManagerTenant.fxml");
    }

    @FXML
    private void onTktCreateButtonClick()
    {
        // Get the values from the text fields
        String title = titleField.getText().trim();
        String description = descArea.getText().trim();

        // Check if both fields are filled
        if (title.isEmpty() || description.isEmpty())
        {
            // Display error message if any of the fields are empty
            errorLabel.setText("Both title and description are required");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
        else
        {
            // If both fields are valid, show success message

            // TODO: SUBMISSION LOGIC HERE

            errorLabel.setText("Your ticket has been successfully submitted");
            errorLabel.setStyle("-fx-text-fill: green;");

            titleField.clear();
            descArea.clear();
        }
    }
}