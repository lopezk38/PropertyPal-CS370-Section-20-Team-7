package com.propertypal.client;

import com.propertypal.client.DEMOSelectedTicket;
import com.propertypal.client.SceneManager;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class TicketReviewController
{

    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea descArea;

    private ObservableList<String> currentTicket;

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

        loadTicket();
    }

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketManagerLandlord.fxml");
    }

    @FXML
    private void onTktCloseButtonClick()
    {
        if (currentTicket == null)
        {
            errorLabel.setText("No ticket selected");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String status = currentTicket.get(2);

        if (!status.equalsIgnoreCase("Open"))
        {
            errorLabel.setText("Ticket is already closed");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Create confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Close Ticket");
        confirmAlert.setHeaderText("Are you sure you want to close this ticket?");
        confirmAlert.setContentText("Ticket: " + currentTicket.get(0));

        // Wait for user response
        confirmAlert.showAndWait().ifPresent(response ->
        {
                    if (response == ButtonType.OK)
                    {
                        // User confirmed: close the ticket
                        currentTicket.set(2, "Closed");
                        statusLabel.setText("Status: Closed");
                        errorLabel.setText("Ticket closed successfully");
                        errorLabel.setStyle("-fx-text-fill: green;");
                    } else
                    {
                        // User cancelled
                        errorLabel.setText("Ticket not closed");
                        errorLabel.setStyle("-fx-text-fill: red;");
                    }
        });
    }

    // Helper functions

    private void loadTicket()
    {
        currentTicket = DEMOSelectedTicket.get();

        if (currentTicket != null)
        {
            titleLabel.setText("Title: " + currentTicket.get(0));
            dateLabel.setText("Created: " + currentTicket.get(1));
            statusLabel.setText("Status: " + currentTicket.get(2));
            descArea.setText(currentTicket.get(3));
        } else
        {
            errorLabel.setText("No ticket selected");
        }
    }
}