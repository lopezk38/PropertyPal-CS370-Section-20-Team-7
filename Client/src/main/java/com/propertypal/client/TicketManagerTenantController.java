package com.propertypal.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class TicketManagerTenantController
{
    @FXML
    private AnchorPane root;

    @FXML
    private Label ticketCountLabel;

    @FXML
    private Button createBtn;

    @FXML
    private Button backBtn;

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());
        updateTicketCount(0);
    }

    @FXML
    private void onBackButtonClick()
    {
        System.out.println("REDIRECT: MAIN MENU PAGE");
    }

    @FXML
    private void onCreateTicketButtonClick()
    {
        System.out.println("REDIRECT: CREATE NEW TICKET PAGE");
    }

    private void updateTicketCount(int count)
    {
        ticketCountLabel.setText("You have " + count + " open tickets.");
    }
}
