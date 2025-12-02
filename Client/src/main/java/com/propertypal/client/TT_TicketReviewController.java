package main.java.com.propertypal.client;

import com.propertypal.client.SelectedTicket;
import com.propertypal.client.SceneManager;
import com.propertypal.client.ClientLogic.TicketLogic;

import com.propertypal.client.SessionManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class TT_TicketReviewController
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

    private ObservableList<String> currentTicket;

    private SessionManager manager;
    private long ticketID; //parsed from row

    public TT_TicketReviewController()
    {
        manager = SessionManager.getInstance();
    }

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

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

    // Helper functions

    private void loadTicket()
    {
        currentTicket = SelectedTicket.get();

        if (currentTicket == null)
        {
            errorLabel.setText("No ticket selected");
            return;
        }

        //extract ticket id from first cell
        String first = currentTicket.get(0);
        ticketID = Long.parseLong(first.split(" ")[1]);

        titleLabel.setText("Title: " + currentTicket.get(0));
        dateLabel.setText("Created: " + currentTicket.get(1));
        statusLabel.setText("Status: " + currentTicket.get(2));
        descArea.setText(currentTicket.get(3));
    }
}