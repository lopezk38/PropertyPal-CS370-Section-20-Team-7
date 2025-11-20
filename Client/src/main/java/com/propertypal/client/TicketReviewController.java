package com.propertypal.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class TicketReviewLandlordController {

    @FXML
    private AnchorPane root;

    @FXML
    private Button backBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Label ticketTitle;

    @FXML
    private Label ticketDate;

    @FXML
    private TextArea ticketDescription;

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());
    }

    private void loadTicket(String title, String date, String description)
    {
        ticketTitle.setText(title);
        ticketDate.setText("Ticket date: " + date);
        ticketDescription.setText(description);
    }

    @FXML
    private void onBackBtnClick()
    {
        System.out.println("REDIRECT: Back to Ticket Manager");
    }

    @FXML
    private void onCloseBtnClick()
    {
        System.out.println("ACTION: Close Ticket");
    }
}
