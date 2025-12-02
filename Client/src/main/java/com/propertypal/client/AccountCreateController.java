package main.java.com.propertypal.client;

import com.propertypal.client.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class AccountCreateController
{
    @FXML
    private VBox root;
    @FXML
    private Label errorLabel;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confPasswordField;
    @FXML
    private TextField phoneField;

    @FXML
    private CheckBox landlordCheckbox;

    @FXML
    private TextField streetField;
    @FXML
    private TextField cityField;
    @FXML
    private ComboBox<String> stateCombo;
    @FXML
    private TextField zipField;

    private boolean isLandlord;

    //--------------------
    // UI Functions
    //--------------------

    @FXML
    private void initialize()
    {
        // Prevents focus of elements when page loads
        Platform.runLater(() -> root.requestFocus());

        // Populate states into ComboBox
        stateCombo.getItems().addAll(Arrays.asList(
                "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
                "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
                "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
                "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
                "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        ));

        // Disable address fields
        setAddressFieldState(false);
    }

    // Landlord checkbox trigger
    @FXML
    private void onLandlordCheckboxToggle()
    {
        boolean checked = landlordCheckbox.isSelected();
        setAddressFieldState(checked);
    }

    // Submit button trigger
    @FXML
    private void onSubmitButtonClick()
    {
        validateSubmit();
    }

    // Back button trigger
    @FXML
    private void onBackButtonClick()
    {
        SceneManager.switchTo("/fxml/login.fxml");
    }

    //--------------------
    // Helper Functions
    //--------------------

    private void setAddressFieldState(boolean enabled)
    {
        streetField.setDisable(!enabled);
        cityField.setDisable(!enabled);
        stateCombo.setDisable(!enabled);
        zipField.setDisable(!enabled);
    }

    private void validateSubmit()
    {
        // Client-side validation

        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pass = passwordField.getText().trim();
        String confirm = confPasswordField.getText().trim();

        isLandlord = landlordCheckbox.isSelected();

        // Check empty fields
        if (email.isEmpty() || first.isEmpty() || last.isEmpty() ||
                phone.isEmpty() || pass.isEmpty() || confirm.isEmpty())
        {
            errorLabel.setText("All fields are required");
            return;
        }

        // Check first and last name only contain letters
        if (!first.matches("^[A-Za-z]+$") || !last.matches("^[A-Za-z]+$"))
        {
            errorLabel.setText("First and last name must contain only letters");
            return;
        }

        // Check email is in correct format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        // Check that passwords match
        if (!pass.equals(confirm))
        {
            errorLabel.setText("Passwords do not match");
            return;
        }

        // Check phone number only contains 10 digits
        if (!phone.matches("^\\d{10}$"))
        {
            errorLabel.setText("Phone number must be 10 digits and contain no spaces/symbols");
            return;
        }

        // Checks for landlord
        if (isLandlord)
        {
            String street = streetField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateCombo.getValue();
            String zip = zipField.getText().trim();

            // Check empty address fields
            if (street.isEmpty() || city.isEmpty() || state == null || zip.isEmpty())
            {
                errorLabel.setText("All address fields are required for landlords");
                return;
            }

            // Check zip code only contains 5 digits
            if (!zip.matches("^\\d{5}$"))
            {
                errorLabel.setText("ZIP code must be 5 digits");
                return;
            }
        }

        // Client-side validation passed

        // TODO Backend logic here (including password hashing)

        showSuccessDialog();

        // TODO Get role via SessionManager
        if (isLandlord)
        {
            SceneManager.switchTo("/fxml/main.fxml");
        }
        else
        {
            SceneManager.switchTo("/fxml/main.fxml");
        }
    }

    private void showSuccessDialog()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Created");
        alert.setHeaderText("Success!");

        if (isLandlord)
        {
            alert.setContentText("Your landlord account has been created successfully!");
        }
        else
        {
            alert.setContentText("Your account has been created successfully!");
        }

        alert.showAndWait();
    }
}