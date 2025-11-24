package com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.ClientLogic.AcctLogic;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginController
{
    public AcctLogic login_info = new AcctLogic();
    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads
    }

    // Log In button trigger
    @FXML
    private void onLogInButtonClick()
    {
        validateLogin();
    }

    // TEMPORARY Create Account button trigger
    @FXML
    private void onCreateAccountButtonClick()
    {
        errorLabel.setText("REDIRECT: CREATE ACCOUNT PAGE");
    }

    private void validateLogin()
    {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        boolean loginSuccess = false;
        ;

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

        // Valid email + non-empty password
        try
        {
            //TODO need to direct tenant or landlord to corresponding landing pages

            // send login info to AcctLogic
            loginSuccess = login_info.acctLogin(email, password);

            // if no exception, go to landlord main screen
            if(loginSuccess)
            {
                System.out.println("Login info correctly matches the database.");
                System.out.println("Now switching to mainLandlord.fxml");
                SceneManager.switchTo("/fxml/mainLandlord.fxml");
            }
        }
        catch (IOException e)
        {
            System.out.println("ERROR: acctLogin threw IOException");
            // optional: show an error to the user
            errorLabel.setText("Login failed. Please try again.");
        }
    }
}