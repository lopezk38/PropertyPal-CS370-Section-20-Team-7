package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PaymentRequestController
{

    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descArea;

    private SessionManager manager;

    public PaymentRequestController()
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
    }

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketManager.fxml");
    }

    @FXML
    private void onTktCreateButtonClick()
    {
        // Client-side validation

        String title = titleField.getText().trim();
        String description = descArea.getText().trim();

        // Check empty fields
        if (title.isEmpty() || description.isEmpty())
        {
            errorLabel.setText("Both title and description are required");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
        else
        {
            // Client-side validation passed

            try
            {
                long leaseID = manager.getLeaseID();
                manager.createTicket(leaseID, title, description);

                errorLabel.setText("Your ticket has been successfully submitted");
                errorLabel.setStyle("-fx-text-fill: green;");

                titleField.clear();
                descArea.clear();
            }
            catch (Exception error)
            {
                errorLabel.setText("Your ticket has failed to submit, please try again");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
}