package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LL_MainController
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


    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads
    }

    // DEMO button trigger START
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
        // DEMO button trigger END
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
        errorLabel.setText("REDIRECT: DOCUMENT MANAGER PAGE");
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
}