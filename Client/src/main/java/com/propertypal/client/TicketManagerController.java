package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SelectedTicket;
import com.propertypal.client.SessionManager;
import com.propertypal.shared.network.enums.TicketEnums;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TicketManagerController
{
    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private Label ticketCountLabel;

    @FXML
    private TableView<ObservableList<String>> ticketTable;
    @FXML
    private TableColumn<ObservableList<String>, String> titleCol;
    @FXML
    private TableColumn<ObservableList<String>, String> dateCol;
    @FXML
    private TableColumn<ObservableList<String>, String> statusCol;

    @FXML
    private Button tktCreateButton;
    @FXML
    private Region tktCreateRegion;

    private SessionManager manager;

    public TicketManagerController()
    {
        manager = SessionManager.getInstance();
    }

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus()); // Prevents focus of elements when page loads

        var role = manager.getRole();

        if (role == SessionManager.Role.LANDLORD)
        {
            landlordUI();
        }
        else if (role == SessionManager.Role.TENANT)
        {
            tenantUI();
        }

        setupTableColumns();
        loadTickets();
    }

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/main.fxml");
    }

    @FXML
    private void onTktCreateButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketCreate.fxml");
    }

    @FXML
    private void onTktReviewButtonClick()
    {
        var selected = ticketTable.getSelectionModel().getSelectedItem();

        if (selected == null)
        {
            errorLabel.setText("Please select a ticket first");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        SelectedTicket.set(selected);

        SceneManager.switchTo("/fxml/ticketReview.fxml");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void landlordUI()
    {
        tktCreateButton.setVisible(false);
        tktCreateButton.setManaged(false);

        tktCreateRegion.setVisible(false);
        tktCreateRegion.setManaged(false);
    }

    private void tenantUI()
    {
        tktCreateButton.setVisible(true);
        tktCreateButton.setManaged(true);

        tktCreateRegion.setVisible(true);
        tktCreateRegion.setManaged(true);
    }

    private void setupTableColumns()
    {
        titleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(0))
        );

        dateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(1))
        );

        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(2))
        );
    }

    private void loadTickets()
    {
        try
        {
            long leaseID = manager.getLeaseID();

            var ids = manager.getTicketIDList(leaseID);

            if (ids == null)
            {
                //No tickets
                updateTicketCount(0);
                return;
            }

            ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();

            for (Long id : ids)
            {
                var info = manager.getTicketInfo(id); //get details
                ObservableList<String> row = FXCollections.observableArrayList();

                row.add(info.TITLE); //ticket title
                row.add(info.LAST_UPDATED.toString()); //date
                row.add(getReadableState(info.STATE)); //state of ticket
                row.add(info.DESCRIPTION); //store for review page
                row.add(id.toString()); //Ticket ID
                rows.add(row);
            }

            ticketTable.getItems().setAll(rows);

            long openCount = rows.stream().filter(r -> !r.get(2).equalsIgnoreCase("Closed")).count();
            updateTicketCount((int) openCount);
        }
        catch (Exception error)
        {
            errorLabel.setText("Failed to load tickets");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    //convert ticket state int to readable words
    private String getReadableState(int state)
    {
        /*
        return switch (state)
        {
            case TicketEnums.State.NEW -> "New";
            case TicketEnums.State.UNDER_REVIEW -> "Under Review";
            case TicketEnums.State.NEEDS_PREPAYMENT -> "Needs Prepayment";
            case TicketEnums.State.IN_PROGRESS -> "In Progress";
            case TicketEnums.State.NEEDS_PAYMENT -> "Needs Payment";
            case TicketEnums.State.CLOSED -> "Closed";
            default -> "Unknown";
        };
        */
        return switch (state)
        {
            case TicketEnums.State.NEW -> "Open";
            case TicketEnums.State.UNDER_REVIEW -> "Open";
            case TicketEnums.State.NEEDS_PREPAYMENT -> "Open";
            case TicketEnums.State.IN_PROGRESS -> "Open";
            case TicketEnums.State.NEEDS_PAYMENT -> "Open";
            case TicketEnums.State.CLOSED -> "Closed";
            default -> "Unknown";
        };
    }

    private void updateTicketCount(int count)
    {
        ticketCountLabel.setText("There " + (count == 1 ? "is " : "are ") + count + " open ticket" + (count == 1 ? "" : "s"));
    }
}