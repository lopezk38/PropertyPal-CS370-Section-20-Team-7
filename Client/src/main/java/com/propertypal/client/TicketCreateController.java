package com.propertypal.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class TicketCreateController {

    @FXML
    private AnchorPane root;

    @FXML
    private Button backBtn;

    @FXML
    private Button submitBtn;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private void initialize() {
        Platform.runLater(() -> root.requestFocus());
    }

    @FXML
    private void onBackBtnClick() {
        System.out.println("REDIRECT: Back to Ticket Manager");
    }

    @FXML
    private void onSubmitBtnClick() {
        String title = titleField.getText().trim();
        String desc = descriptionField.getText().trim();

        System.out.println("SUBMIT: Title=" + title);
        System.out.println("SUBMIT: Description=" + desc);
    }
}



