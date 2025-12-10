package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SessionManager;
import com.propertypal.client.SendInviteDialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;


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

    private SessionManager manager;

    public LoginController()
    {
        manager = SessionManager.getInstance();
    }

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
            // Get role
            SessionManager.Role role = manager.loginAndGetRole(email, password);

            // Set session
            manager.login(email, role);

            //Check if account is not ready
            if (!manager.isLeaseReady())
            {
                //Handle differently for LL or TT
                if (role == SessionManager.Role.LANDLORD)
                {
                    //Landlord. Force them to do an invite
                    SendInviteDialog invDialog = new SendInviteDialog();
                    if (!invDialog.join()) //Show dialog and return if we got a lease or not
                    {
                        //Invite failed/no lease/not accepted yet
                        return;
                    }

                    //Check one more time if invite was accepted
                    if (!manager.isLeaseReady())
                    {
                        //Still not accepted
                        return;
                    }
                }
                else
                {
                    //Tenant. Don't let them login until they accept an invite
                    ArrayList<Long> invites = null;

                    try
                    {
                        invites = manager.getInvites();
                    }
                    catch (IOException e)
                    {
                        //Couldn't talk to server, assume no invites
                        System.out.println("ERROR: While attempting to get invites, threw " + e.toString());
                        showTenAcctNotReady();
                        return;
                    }

                    if (invites == null || invites.isEmpty())
                    {
                        //No invites in yet
                        showTenAcctNotReady();
                        return;
                    }

                    //We have invites
                    //TODO SHOW INVITE ACCEPT SCREEN
                    errorLabel.setText("TODO accept invite prompt");

                    return;
                }
            }

            SceneManager.switchTo("/fxml/main.fxml");
        }
        catch (IOException e)
        {
            System.out.println("ERROR in loginAndGetRole(String, String): " + e.getMessage());
            errorLabel.setText("Login failed, please try again");
        }
    }

    private void showTenAcctNotReady()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account not ready yet");
        alert.setHeaderText("Waiting for invite");

        alert.setContentText("You must wait for a Landlord to invite you to a lease before continuing.");

        alert.showAndWait();
    }
}