package main.java.com.propertypal.client;

import com.propertypal.client.SelectedTicket;
import com.propertypal.client.SceneManager;
import com.propertypal.client.ClientLogic.TicketLogic;
import com.propertypal.shared.network.enums.TicketEnums;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class LL_TicketManagerController
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

    private TicketLogic logic = new TicketLogic();

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

        setupTableColumns();
        loadTickets();
    }

//    // DEMO button trigger START
//    private boolean demoActive = false;
//
//    @FXML
//    private void onDEMOButtonClick()
//    {
//
//        if (!demoActive)
//        {
//            ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
//
//            rows.add(createTicket("Request for maintenance", "2025-11-22", "Open", "The garage door is busted."));
//            rows.add(createTicket("Broken light", "2025-11-22", "Closed", "I don't know how to change a lightbulb."));
//            rows.add(createTicket("Plumbing issue", "2025-11-22", "Open", "I clogged the toilet."));
//
//            ticketTable.getItems().setAll(rows);
//
//            long openCount = rows.stream()
//                    .filter(r -> r.get(2).equalsIgnoreCase("Open"))
//                    .count();
//
//            updateTicketCount((int) openCount);
//
//            demoActive = true;
//        }
//        else
//        {
//            ticketTable.getItems().clear();
//            updateTicketCount(0);
//
//            demoActive = false;
//        }
//    }
//    // DEMO button trigger END

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/main.fxml");
    }

    @FXML
    private void onTktReviewButtonClick()
    {
        var selected = ticketTable.getSelectionModel().getSelectedItem();
        //ObservableList<String> selected = ticketTable.getSelectionModel().getSelectedItem();   //  DEMO

        if (selected == null)
        {
            errorLabel.setText("Please select a ticket first");
            return;
        }

        //DEMOSelectedTicket.set(selected);   // DEMO
        SelectedTicket.set(selected);

        SceneManager.switchTo("/fxml/LL_ticketReview.fxml");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void loadTickets() //load tickets from server
    {
        try
        {
            long leaseID = 1; //temp until login provides real lease ID

            var ids = logic.getTicketIDList(leaseID);

            ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();

            for (Long id : ids)
            {
                var info = logic.getTicketInfo(id); //get details
                ObservableList<String> row = FXCollections.observableArrayList();

                row.add("Ticket " + id); //ticket #
                row.add(info.LAST_UPDATED.toString()); //date
                row.add(getReadableState(info.STATE)); //state of ticket
                row.add(info.DESCRIPTION); //store for review page
                rows.add(row);
            }

            ticketTable.getItems().setAll(rows);

            //count open tickets
            long openCount = rows.stream().filter(r -> !r.get(2).equalsIgnoreCase("Closed")).count();
            updateTicketCount((int) openCount);
        }
        catch (Exception error)
        {
            errorLabel.setText("Failed to load tickets");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

//    private ObservableList<String> createTicket(String title, String date, String status, String description)
//    {
//        ObservableList<String> ticket = FXCollections.observableArrayList();
//        ticket.add(title);
//        ticket.add(date);
//        ticket.add(status);
//        ticket.add(description);
//        return ticket;
//    }

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

    //convert ticket state int to readable words
    private String getReadableState(int state)
    {
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
    }

    private void updateTicketCount(int count)
    {
        ticketCountLabel.setText("You have " + count + " open ticket" + (count == 1 ? "" : "s"));
    }
}