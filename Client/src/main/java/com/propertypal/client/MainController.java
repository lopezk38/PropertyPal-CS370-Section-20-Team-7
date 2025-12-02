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

    private SessionManager manager;

    public MainController()
    {
        manager = SessionManager.getInstance();
    }

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

        var role = manager.getRole();

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

        errorLabel.setText("ACTION: LL+TT LINK WINDOW");
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
        SceneManager.switchTo("/fxml/ticketManager.fxml");
    }

    // TEMPORARY Payment Manager button trigger
    @FXML
    private void onPmtMgrButtonClick()
    {
        SceneManager.switchTo("/fxml/paymentManager.fxml");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void landlordUI()
    {
        linkButton.setVisible(true);

        helloXLabel.setText(String.format("Hello, %s!", manager.getLLFname()));

        snapshotLabel.setText("Your Tenant's Snapshot");

        nameXLabel.setText(String.format("Name: %s %s", manager.getTTFname(), manager.getTTLname()));
        emailXLabel.setText(String.format("Email: %s", manager.getTTEmail()));
        phoneXLabel.setText(String.format("Phone: %s", manager.getTTPhone()));
    }

    private void tenantUI()
    {
        linkButton.setVisible(false);

        helloXLabel.setText(String.format("Hello, %s!", manager.getTTFname()));

        snapshotLabel.setText("Your Landlord's Snapshot");

        nameXLabel.setText(String.format("Name: %s %s", manager.getLLFname(), manager.getLLLname()));
        emailXLabel.setText(String.format("Email: %s", manager.getLLEmail()));
        phoneXLabel.setText(String.format("Phone: %s", manager.getLLPhone()));
    }
}