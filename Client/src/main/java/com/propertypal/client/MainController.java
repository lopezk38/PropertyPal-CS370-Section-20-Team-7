package com.propertypal.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainController
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

//    private void handleDemo()
//    {
//        String email = emailField.getText().trim();
//        String password = passwordField.getText().trim();
//
//        // Check empty fields
//        if (email.isEmpty() || password.isEmpty())
//        {
//            errorLabel.setText("All fields are required");
//            return;
//        }
//
//        // Email validation (simple pattern)
//        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
//        {
//            errorLabel.setText("Please enter a valid email address");
//            return;
//        }
//
//        // TEMPORARY Valid email + non-empty password
//        errorLabel.setText("LOGIN SUCCESS");
//    }
}