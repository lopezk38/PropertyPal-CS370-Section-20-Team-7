package main.java.com.propertypal.client;

import com.propertypal.client.PDFViewer;
import com.propertypal.client.SceneManager;
import com.propertypal.shared.network.helpers.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.enums.*;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;

public class DocumentManagerController
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

    private SessionManager manager = null;

    public DocumentManagerController()
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

        setupTableColumns();
        loadDocuments();
    }

    @FXML
    private void onBackButtonClick()
    {
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

            //Send to server
            manager.uploadDocument(uploadedFile.toPath(), uploadedFile.getName(), "N/A", true);

            /*
            // For demo, just add to table:
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(uploadedFile.getName());  // title
            row.add(LocalDate.now().toString()); // date
            row.add(uploadedFile.getAbsolutePath()); // stored path

            documentTable.getItems().add(row);
             */

            //Update table
            loadDocuments();

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

        Long docID = Long.parseLong(selectedFile.get(2));  // stored during upload

        //Download file to memory
        byte[] docBlob = null;
        long expectedSize = -1;
        int mime = -1;
        try
        {
            ViewDocResponse resp = manager.viewDocument(docID);
            if (resp.STATUS == 0)
            {
                expectedSize = resp.FILE_SIZE;
                String rawData = resp.DOC_DATA;
                mime = resp.MIME_TYPE;
                // Decode Base64
                byte[] compressed;
                try
                {
                    compressed = Base64.getDecoder().decode(rawData);
                }
                catch (IllegalArgumentException e)
                {
                    System.out.printf("ERROR: Downloaded document %s (%d) is in a bad base64 string%n", selectedFile.get(0), docID);
                    errorLabel.setText("Failed to download document");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                // Decompress gzip
                try
                {
                    docBlob = CompressionUtil.ungzip(compressed);
                }
                catch (IOException e)
                {
                    System.out.printf("ERROR: Downloaded document %s (%d) is in a bad gzip archive%n", selectedFile.get(0), docID);
                    errorLabel.setText("Failed to decompress document");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
            }
            else
            {
                System.out.printf("WARNING: Got error response %d while downloading doc %s (%d) from server%n", resp.STATUS, selectedFile.get(0), docID);
            }

        }
        catch (IOException e)
        {
            System.out.printf("WARNING: Could not get info for doc $s (%d) from server%n", selectedFile.get(0), docID);
            errorLabel.setText("Unable to open document");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (docBlob == null || docBlob.length < 1)
        {
            System.out.printf("WARNING: Got empty doc %s (%d) from server%n", selectedFile.get(0), docID);
            errorLabel.setText("Document was empty");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Compare size with FILE_SIZE
        if (docBlob.length != expectedSize)
        {
            System.out.println("ERROR: Size of document does not match manifest. Rejecting. Expected " + expectedSize +
                    " but got " + docBlob.length);

            errorLabel.setText("Document was corrupt");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try
        {
            switch(mime)
            {
                case DocTypeEnum.Type.pdf:
                    PDFViewer.openPDF(docBlob);
                    break;

                case DocTypeEnum.Type.png:
                    errorLabel.setText("PNG document type is currently not supported");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    break; //TODO

                case DocTypeEnum.Type.jpeg:
                    errorLabel.setText("JPEG document type is currently not supported");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    break; //TODO

                case DocTypeEnum.Type.txt:
                    errorLabel.setText("Text document type is currently not supported");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    break; //TODO

                default:
                    errorLabel.setText("Unknown document type, cannot open");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    break;
            }
        }
        catch (Exception e)
        {
            errorLabel.setText("Unable to open document");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
            return;
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
        documentTable.getItems().clear();

        ArrayList<Long> docIDs = null;
        try
        {
            docIDs = manager.getDocumentList();
        }
        catch (IOException e)
        {
            System.out.println("ERROR: Failed to receive document list from server");
            return;
        }

        if (docIDs == null)
        {
            System.out.println("WARNING: docIDs is null instead of empty");
            return;
        }

        //Request info for each doc
        for (Long id : docIDs)
        {
            if (id > 0)
            {
                try
                {
                    GetDocInfoResponse resp = manager.getDocumentInfo(id);
                    if (resp.STATUS == 0)
                    {
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(resp.NAME);  // title
                        //row.add(LocalDate.from(resp.MOD_DATE.atZone(ZoneId.systemDefault()).toInstant()).toString()); // date converted to local time
                        row.add(resp.MOD_DATE.toString());
                        row.add(id.toString()); // ID

                        documentTable.getItems().add(row);
                    }
                    else
                    {
                        System.out.printf("WARNING: Got error response %d while requesting info for doc %d from server%n", resp.STATUS, id);
                    }

                }
                catch (IOException e)
                {
                    System.out.printf("WARNING: Could not get info for doc %d from server%n", id);
                }
            }
            else
            {
                System.out.println("WARNING: Got invalid docID from server");
            }
        }

        /*
        // For demo, keep table empty
        documentTable.getItems().clear();
        */

    }
}