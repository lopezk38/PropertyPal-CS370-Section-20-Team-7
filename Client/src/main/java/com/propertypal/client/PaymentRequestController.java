package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PaymentRequestController
{

    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private TextField paypalField;
    @FXML
    private TextField requestField;
    // TODO dateField?

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
        SceneManager.switchTo("/fxml/main.fxml");
    }

    @FXML
    private void onPmtRequestButtonClick()
    {
        // Client-side validation

        String paypalLink = paypalField.getText().trim();
        String requestAmount = requestField.getText().trim();

        // TODO Add more validation checks

        // Check empty fields
        if (paypalLink.isEmpty() || requestAmount.isEmpty())
        {
            errorLabel.setText("All fields are required");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
        else
        {
            // Client-side validation passed

            try
            {
                // TODO Add PaymentLogic

//                // Ticket Create hint
//                long leaseID = manager.getLeaseID();
//                manager.createTicket(leaseID, title, description);

                errorLabel.setText("Your payment request has been successfully submitted");
                errorLabel.setStyle("-fx-text-fill: green;");
            }
            catch (Exception error)
            {
                errorLabel.setText("Your payment request has failed to submit, please try again");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
}