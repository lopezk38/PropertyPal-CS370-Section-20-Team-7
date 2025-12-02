package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SelectedTicket;
import com.propertypal.client.SessionManager;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class TicketReviewController
{

    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private Label statusLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private TextArea descArea;

    private SessionManager manager;

    public TicketReviewController()
    {
        manager = SessionManager.getInstance();
    }

    private long ticketID; //parsed from row

    private ObservableList<String> currentTicket;

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

        loadTicket();
    }

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketManager.fxml");
    }

    @FXML
    private void onTktCloseButtonClick()
    {
        if (currentTicket == null)
        {
            errorLabel.setText("No ticket selected");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String status = currentTicket.get(2);

        if (status.equalsIgnoreCase("Closed"))
        {
            errorLabel.setText("Ticket is already closed");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Create confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancel Ticket");
        confirmAlert.setHeaderText("Are you sure you want to cancel this ticket?");
        confirmAlert.setContentText("Ticket: " + currentTicket.get(0));

        // Wait for user response
        confirmAlert.showAndWait().ifPresent(response ->
        {
            if (response == ButtonType.OK)
            {
                try
                {
                    manager.closeTicket(ticketID); //server call
                    currentTicket.set(2, "Closed");
                    statusLabel.setText("Status: Closed");
                    errorLabel.setText("Ticket cancelled successfully");
                    errorLabel.setStyle("-fx-text-fill: green;");
                }
                catch (Exception error)
                {
                    errorLabel.setText("Failed to cancel ticket");
                    errorLabel.setStyle("-fx-text-fill: red;");
                }
            }

            else // User cancelled
            {
                errorLabel.setText("Ticket not closed");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

        });
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void landlordUI()
    {

    }

    private void tenantUI()
    {

    }

    private void loadTicket()
    {
        currentTicket = SelectedTicket.get();

        if (currentTicket == null)
        {
            errorLabel.setText("No ticket selected");
            return;
        }

        //extract ticket id from first cell
        String fourth = currentTicket.get(4);
        ticketID = Long.parseLong(fourth);

        titleLabel.setText("Title: " + currentTicket.get(0));
        dateLabel.setText("Created: " + currentTicket.get(1));
        statusLabel.setText("Status: " + currentTicket.get(2));
        descArea.setText(currentTicket.get(3));
    }
}