package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainController
{
    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private Button linkButton;

    @FXML
    private Label helloXLabel;

    @FXML
    private Text snapshotLabel;
    @FXML
    private Label nameXLabel;
    @FXML
    private Label emailXLabel;
    @FXML
    private Label phoneXLabel;

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

        var role = SessionManager.getInstance().getRole();

        if (role == SessionManager.Role.LANDLORD)
        {
            landlordUI();
        }
        else if (role == SessionManager.Role.TENANT)
        {
            tenantUI();
        }
    }

    boolean demoActive = false; // DELETE WITH DEMO

    @FXML
    private void onLinkButtonClick()
    {
        // TODO: Implement LL+TT linking UI here

        // DEMO START

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
        // DEMO END
    }

    // Log Out button trigger
    @FXML
    private void onLogOutButtonClick()
    {
        SceneManager.switchTo("/fxml/login.fxml");
    }

    // TEMPORARY Document Manager button trigger
    @FXML
    private void onDocMgrButtonClick()
    {
        SceneManager.switchTo("/fxml/documentManager.fxml");
    }

    // Ticket Manager button trigger
    @FXML
    private void onTktMgrButtonClick()
    {
        SceneManager.switchTo("/fxml/LL_ticketManager.fxml");
    }

    // TEMPORARY Payment Manager button trigger
    @FXML
    private void onPmtMgrButtonClick()
    {
        errorLabel.setText("REDIRECT: PAYMENT MANAGER PAGE");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void landlordUI()
    {
        snapshotLabel.setText("Your Tenant's Snapshot");
        linkButton.setVisible(true);
    }

    private void tenantUI()
    {
        snapshotLabel.setText("Your Landlord's Snapshot");
        linkButton.setVisible(false);
    }
}