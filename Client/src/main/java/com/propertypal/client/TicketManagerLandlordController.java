package com.propertypal.client;

import com.propertypal.client.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TicketManagerLandlordController
{
    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private Label ticketCountLabel;

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

        //updateTicketCount(0);   // This should be the number of tickets retrieved from the server
    }

    // DEMO button trigger START
    private int demoCount = 0;

    @FXML
    private void onDEMOButtonClick()
    {

        updateTicketCount(demoCount);

        demoCount = (demoCount + 1) % 3;
    }
    // DEMO button trigger END

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/mainLandlord.fxml");
    }

    @FXML
    private void onTktReviewButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketreview.fxml");
    }

    private void updateTicketCount(int count)
    {
        ticketCountLabel.setText("You have " + count + " open ticket" + (count == 1 ? "" : "s"));
    }
}