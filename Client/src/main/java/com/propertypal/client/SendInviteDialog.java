package com.propertypal.client;

import com.propertypal.shared.network.responses.CreateInviteResponse;

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

import java.io.IOException;

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

        if (succeeded)
        {
            showSuccessDialog();
        }

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

        //Have server perform the invite
        try
        {
            CreateInviteResponse resp = manager.inviteTenant(tenEmail);
            if (resp.STATUS != 0)
            {
                switch (resp.STATUS)
                {
                    case CreateInviteResponse.InviteStatus.ERR_BAD_TARGET_USER:
                        errorLabel.setText("That email was invalid");
                        break;

                    case CreateInviteResponse.InviteStatus.ERR_BAD_PROPERTY:
                        errorLabel.setText("Your account is corrupted");
                        break;

                    case CreateInviteResponse.InviteStatus.ERR_TARGET_CANNOT_BE_LANDLORD:
                        errorLabel.setText("You cannot invite another landlord");
                        break;

                    case CreateInviteResponse.InviteStatus.ERR_TARGET_ALREADY_IN_LEASE:
                        errorLabel.setText("That user is taken, try another");
                        break;

                    case CreateInviteResponse.InviteStatus.ERR_INVITE_ALREADY_EXISTS:
                        showExistingInviteDialog();
                        return;

                    default:
                        errorLabel.setText("Error code " + resp.STATUS + " while sending request");
                        break;
                }

                event.consume(); //Don't close the dialog
                return;
            }
        }
        catch (IOException e)
        {
            errorLabel.setText("Error while sending request");

            event.consume(); //Don't close the dialog
            return;
        }
        catch (IllegalArgumentException e)
        {
            errorLabel.setText("That email was invalid");

            event.consume(); //Don't close the dialog
            return;
        }

        succeeded = true;
    }

    void onCancel(Event event)
    {
        succeeded = false;
    }

    private void showSuccessDialog()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invite created");
        alert.setHeaderText("Success! Wait for them to accept your invite");

        alert.showAndWait();
    }

    private void showExistingInviteDialog()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invite failed");
        alert.setHeaderText("You have a pending invite and cannot create another");

        alert.showAndWait();
    }
}
