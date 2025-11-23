package com.propertypal.client;

import com.propertypal.client.DEMOSelectedTicket;
import com.propertypal.client.SceneManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class TicketManagerTenantController
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
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus());   // Prevents focus of elements when page loads

        setupTableColumns();
    }

    // DEMO button trigger START
    private boolean demoActive = false;

    @FXML
    private void onDEMOButtonClick()
    {

        if (!demoActive)
        {
            ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();

            rows.add(createTicket("Request for maintenance", "2025-11-22", "Open", "The garage door is busted."));
            rows.add(createTicket("Broken light", "2025-11-22", "Closed", "I don't know how to change a lightbulb."));
            rows.add(createTicket("Plumbing issue", "2025-11-22", "Open", "I clogged the toilet."));

            ticketTable.getItems().setAll(rows);

            long openCount = rows.stream()
                    .filter(r -> r.get(2).equalsIgnoreCase("Open"))
                    .count();

            updateTicketCount((int) openCount);

            demoActive = true;
        }
        else
        {
            ticketTable.getItems().clear();
            updateTicketCount(0);

            demoActive = false;
        }
    }
    // DEMO button trigger END

    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/mainTenant.fxml");   // Needs to be mainTenant.fxml
    }

    @FXML
    private void onTktCreateButtonClick()
    {
        SceneManager.switchTo("/fxml/ticketcreate.fxml");   // Needs to be mainTenant.fxml
    }

    @FXML
    private void onTktReviewButtonClick()
    {
        ObservableList<String> selected = ticketTable.getSelectionModel().getSelectedItem();    // DEMO

        if (selected == null)
        {
            errorLabel.setText("Please select a ticket first");
            return;
        }

        DEMOSelectedTicket.set(selected);   // DEMO

        SceneManager.switchTo("/fxml/ticketReview.fxml");
    }

    // Helper functions

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

    private ObservableList<String> createTicket(String title, String date, String status, String description)
    {
        ObservableList<String> ticket = FXCollections.observableArrayList();
        ticket.add(title);
        ticket.add(date);
        ticket.add(status);
        ticket.add(description);
        return ticket;
    }

    private void updateTicketCount(int count)
    {
        ticketCountLabel.setText("You have " + count + " open ticket" + (count == 1 ? "" : "s"));
    }
}