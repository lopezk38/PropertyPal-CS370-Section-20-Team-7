package com.propertypal.client;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;

import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;

import javafx.geometry.HPos;
import javafx.geometry.VPos;

import javafx.scene.paint.Color;

import javafx.stage.Stage;

public class SendInviteDialog extends TextInputDialog
{
    SessionManager manager = null;

    Label errorLabel = null;

    boolean succeeded = false;

    public SendInviteDialog()
    {
        super();
        manager = SessionManager.getInstance();

        DialogPane pane = this.getDialogPane();

        Stage stage = (Stage) pane.getScene().getWindow();

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        this.setTitle("Invite a tenant");
        this.setHeaderText("You must invite a tenant to your lease before proceeding");
        this.setContentText("Enter their email address");

        TextField inputField = this.getEditor();
        Button submitButton = (Button) pane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);

        inputField.setOnAction(event -> { onSubmit(event); });

        submitButton.setText("Submit");
        submitButton.addEventFilter(ActionEvent.ACTION, event -> { onSubmit(event); });

        cancelButton.addEventFilter(ActionEvent.ACTION, event -> { onCancel(event); });

        stage.setOnCloseRequest((WindowEvent event) -> { onCancel(event); });

        setOnShowing((DialogEvent) -> { injectErrorLabel(DialogEvent); });
    }

    public boolean join()
    {
        showAndWait();
        return succeeded;
    }

    void injectErrorLabel(Event event)
    {
        DialogPane pane = this.getDialogPane();

        GridPane paneGrid = (GridPane) pane.getContent();
        paneGrid.add(errorLabel, 1, 1);
        paneGrid.setHalignment(errorLabel, HPos.CENTER);
        paneGrid.setValignment(errorLabel, VPos.BOTTOM);

    }

    void onSubmit(Event event)
    {
        String tenEmail = this.getEditor().getText().trim();

        //Validate entry
        // Check empty fields
        if (tenEmail.isBlank())
        {
            errorLabel.setText("You must enter an email address");
            event.consume(); //Don't close the dialog
            return;
        }

        // Check email is in correct format
        if (!tenEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        {
            errorLabel.setText("Please enter a valid email address");
            event.consume(); //Don't close the dialog
            return;
        }

        System.out.println("debug send invite");

        succeeded = true;
    }

    void onCancel(Event event)
    {
        succeeded = false;
    }
}
