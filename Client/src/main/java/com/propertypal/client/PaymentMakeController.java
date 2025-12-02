package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;
import com.propertypal.client.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.IOException;

public class PaymentMakeController
{
    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private WebView webview;

    private SessionManager manager = null;

    private String payPalLink = null;

    public PaymentMakeController()
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