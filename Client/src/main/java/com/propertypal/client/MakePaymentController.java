package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.web.WebView;

import java.io.IOException;

public class MakePaymentController
{
    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private WebView webview;

    private SessionManager manager = null;

    private String payPalLink = null;

    public MakePaymentController()
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

        initWebView();
    }

    @FXML
    private void onBackButtonClick()
    {
        // TODO: This needs to switch to correct page based on user role (session manager?)
        SceneManager.switchTo("/fxml/main.fxml");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void initWebView()
    {
        try
        {
            payPalLink = manager.getPayPalLink();
            errorLabel.setText("");
            //payPalLink = "https://www.google.com";

            //webview = new WebView();
            webview.getEngine().load(payPalLink);
        }
        catch (IOException | IllegalArgumentException e)
        {
            errorLabel.setText("Unable to retrieve payment link from server");
        }
    }
}