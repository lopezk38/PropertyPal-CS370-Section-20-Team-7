package com.propertypal.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainLandlordController
{
    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private Label helloXLabel;

    @FXML
    private Label nameXLabel;

    @FXML
    private Label emailXLabel;

    @FXML
    private Label phoneXLabel;

    // Prevents focus elements when page spawns
    @FXML
    private void initialize()
    {
        Platform.runLater(() -> {
            root.requestFocus();
        });
    }

    // TEMPORARY DEMO button trigger

    private boolean demoActive = false;

    @FXML
    private void onDEMOButtonClick()
    {
        if (!demoActive)
        {
            helloXLabel.setText("Hello, Alan!");
            nameXLabel.setText("Name: John Doe");
            emailXLabel.setText("Email: johndoe@email.com");
            phoneXLabel.setText("Phone: (555) 555-5555");

            demoActive = true;
        }

        else
        {
            helloXLabel.setText("Hello, [F_NAME]!");
            nameXLabel.setText("Name: [F_NAME L_NAME]");
            emailXLabel.setText("Email: [EMAIL]");
            phoneXLabel.setText("Phone: [PHONE]");

            demoActive = false;
        }

    }

    // TEMPORARY Log Out button trigger
    @FXML
    private void onLogOutButtonClick()
    {
        errorLabel.setText("REDIRECT: LOGIN PAGE");
    }

    // TEMPORARY Document Manager button trigger
    @FXML
    private void onDocMgrButtonClick()
    {
        errorLabel.setText("REDIRECT: DOCUMENT MANAGER PAGE");
    }

    // TODO Ticket Manager button trigger
    @FXML
    private void onTktMgrButtonClick()
    {
        errorLabel.setText("REDIRECT: TICKET MANAGER PAGE");
    }

    // TEMPORARY Payment Manager button trigger
    @FXML
    private void onPmtMgrButtonClick()
    {
        errorLabel.setText("REDIRECT: PAYMENT MANAGER PAGE");
    }
}