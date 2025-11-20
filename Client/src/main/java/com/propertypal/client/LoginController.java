package com.propertypal.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController
{
    @FXML
    private VBox root;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label errorLabel;

    // Prevents focus of the Email field when page spawns
    @FXML
    private void initialize()
    {
        Platform.runLater(() -> {
            root.requestFocus();
        });
    }

    // Log In button trigger
    @FXML
    private void onLogInButtonClick()
    {
        handleLogin();
    }

    // TEMPORARY Create Account button trigger
    @FXML
    private void onCreateAccountButtonClick()
    {
        errorLabel.setText("REDIRECT: CREATE ACCOUNT PAGE");
    }

    private void handleLogin()
    {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Check empty fields
        if (email.isEmpty() || password.isEmpty())
        {
            errorLabel.setText("All fields are required");
            return;
        }

        // Email validation (simple pattern)
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        // TEMPORARY Valid email + non-empty password
        errorLabel.setText("LOGIN SUCCESS");
    }
}