package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.ClientLogic.TicketLogic;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class TicketCreateController
{

    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descArea;

    private TicketLogic logic = new TicketLogic();

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads
    }

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/TT_ticketManager.fxml");
    }

    @FXML
    private void onTktCreateButtonClick()
    {
        // Get the values from the text fields
        String title = titleField.getText().trim();
        String description = descArea.getText().trim();

        // Check if both fields are filled
        if (title.isEmpty() || description.isEmpty())
        {
            // Display error message if any of the fields are empty
            errorLabel.setText("Both title and description are required");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
        else
        {
            // If both fields are valid, show success message

            //SUBMISSION LOGIC
            try
            {
                long leaseID = 1; //temp until login provides real lease ID
                logic.createticket(leaseID, description);

                errorLabel.setText("Your ticket has been successfully submitted");
                errorLabel.setStyle("-fx-text-fill: green;");

                titleField.clear();
                descArea.clear();
            }
            catch (Exception error)
            {
                errorLabel.setText("Your ticket has failed to submit. Please try again");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

        }
    }
}