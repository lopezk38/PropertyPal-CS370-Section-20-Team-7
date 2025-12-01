package main.java.com.propertypal.client;

import com.propertypal.client.ClientLogic.AcctLogic;
import com.propertypal.client.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class LoginController
{
    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    public AcctLogic logic = new AcctLogic();

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        // Prevents focus of elements when page loads
        Platform.runLater(() -> root.requestFocus());
    }

    // Log In button trigger
    @FXML
    private void onLogInButtonClick()
    {
        validateLogin();
    }

    // Create Account button trigger
    @FXML
    private void onCreateAccountButtonClick()
    {
        SceneManager.switchTo("/fxml/accountCreate.fxml");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void validateLogin()
    {
        // Client-side validation

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Check empty fields
        if (email.isEmpty() || password.isEmpty())
        {
            errorLabel.setText("All fields are required");
            return;
        }

        // Check email is in correct format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        // Client-side validation passed

        try
        {
            //throws exception if login failed
            Boolean userRole = logic.loginAndGetRole(email, password);

            if(userRole)
            {
                System.out.println("Switching to Landlord page");
                SceneManager.switchTo("/fxml/LL_main.fxml");
            }
            else
            {
                System.out.println("Switching to Tenant page");
                SceneManager.switchTo("/fxml/TT_main.fxml");

            }
        }
        catch (IOException e)
        {
            System.out.println("ERROR in loginAndGetRole(String, String): " + e.getMessage());
            // optional: show an error to the user
            errorLabel.setText("Login failed. Please try again.");
        }
    }
}