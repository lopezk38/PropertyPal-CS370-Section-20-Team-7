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
            return;
        }

        // Check amount field format
        if (!requestAmount.matches("^\\d+(\\.\\d{1,2})?$"))
        {
            errorLabel.setText("Amount must be a valid number with up to 2 decimal places");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Auto-format PayPal link
        String paypalString = paypalLink.toLowerCase().replace(" ", "");
        if (!paypalString.startsWith("paypal.me/"))
        {
            paypalString = "paypal.me/" + paypalString;
        }

        // Client-side validation passed

        int dueDay = 1;

        try
        {
            long leaseID = manager.getLeaseID();
            manager.updateAmountDue(leaseID, paypalLink, requestAmount, dueDay);

            errorLabel.setText("Your payment request has been successfully submitted");
            errorLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception error)
        {
            errorLabel.setText("Failed: " + error.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
}