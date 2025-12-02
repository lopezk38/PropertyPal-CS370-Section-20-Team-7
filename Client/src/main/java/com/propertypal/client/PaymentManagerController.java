package main.java.com.propertypal.client;

import com.propertypal.client.PDFViewer;
import com.propertypal.client.SceneManager;
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

import java.io.File;
import java.time.LocalDate;

public class PaymentManagerController
{
    @FXML
    private VBox root;

    @FXML
    private Label errorLabel;

    @FXML
    private TableView<ObservableList<String>> documentTable;

    @FXML
    private TableColumn<ObservableList<String>, String> titleCol;

    @FXML
    private TableColumn<ObservableList<String>, String> dateCol;

//    private DocumentLogic logic = new DocumentLogic();    // TODO: Make this class

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        Platform.runLater(() -> root.requestFocus()); // Prevents focus of elements when page loads

        setupTableColumns();
        loadDocuments();
    }

    @FXML
    private void onBackButtonClick()
    {
        // TODO: This needs to switch to correct page based on user role (session manager?)
        SceneManager.switchTo("/fxml/main.fxml");
    }

    @FXML
    private void onDocUploadButtonClick()
    {
        try
        {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Upload PDF Document");

            chooser.getExtensionFilters().setAll(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            Stage stage = (Stage) root.getScene().getWindow();
            File uploadedFile = chooser.showOpenDialog(stage);

            if (uploadedFile == null)
            {
                errorLabel.setText("Document upload cancelled");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!uploadedFile.getName().toLowerCase().endsWith(".pdf"))
            {
                errorLabel.setText("Only PDF files are allowed");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // TODO: For actual implementation, use something like logic.uploadDocument(uploadedFile);

            // For demo, just add to table:
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(uploadedFile.getName());  // title
            row.add(LocalDate.now().toString()); // date
            row.add(uploadedFile.getAbsolutePath()); // stored path

            documentTable.getItems().add(row);

            errorLabel.setText("Document uploaded");
            errorLabel.setStyle("-fx-text-fill: green;");
        }
        catch (Exception e)
        {
            errorLabel.setText("Failed to upload document");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void onDocViewButtonClick()
    {
        var selectedFile = documentTable.getSelectionModel().getSelectedItem();

        if (selectedFile == null)
        {
            errorLabel.setText("Please select a document first");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String filepath = selectedFile.get(2);  // stored during upload

        try
        {
            PDFViewer.openPDF(filepath);
        }
        catch (Exception e)
        {
            errorLabel.setText("Unable to open document");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void setupTableColumns()
    {
        titleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(0))
        );

        dateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(1))
        );
    }

    private void loadDocuments()
    {
        // TODO: For actual implementation, load from DocumentLogic

        // For demo, keep table empty
        documentTable.getItems().clear();
    }
}