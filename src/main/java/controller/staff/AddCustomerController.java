package controller.staff;

import dao.CustomerDAO;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Customer;

public class AddCustomerController {

    public static Customer showDialog() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");

        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();
        Label nameError = new Label();
        nameError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        Label phoneError = new Label();
        phoneError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label emailError = new Label();
        emailError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        Label addressError = new Label();
        addressError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(10);

        // Thêm các field và error labels
        grid.addRow(0, nameLabel, nameField);
        grid.addRow(1, new Label(""), nameError);
        grid.addRow(2, phoneLabel, phoneField);
        grid.addRow(3, new Label(""), phoneError);
        grid.addRow(4, emailLabel, emailField);
        grid.addRow(5, new Label(""), emailError);
        grid.addRow(6, addressLabel, addressField);
        grid.addRow(7, new Label(""), addressError);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.setDisable(true);

        // Validation listeners với thông báo lỗi chi tiết
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateName(newVal.trim(), nameError);
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveButton);
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePhone(newVal.trim(), phoneError);
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveButton);
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateEmail(newVal.trim(), emailError);
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveButton);
        });

        addressField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateAddress(newVal.trim(), addressError);
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveButton);
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Customer customer = new Customer(nameField.getText().trim(),
                        phoneField.getText().trim(),
                        addressField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setLoyaltyPoints(0); // Mặc định 0 điểm tích lũy
                return customer;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static void validateName(String name, Label errorLabel) {
        if (name.isEmpty()) {
            errorLabel.setText("Full name is required");
        } else if (name.length() < 2) {
            errorLabel.setText("Name must be at least 2 characters");
        } else if (name.length() > 50) {
            errorLabel.setText("Name must not exceed 50 characters");
        } else if (!name.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            errorLabel.setText("Name can only contain letters and spaces");
        } else {
            errorLabel.setText("");
        }
    }

    private static void validatePhone(String phone, Label errorLabel) {
        if (phone.isEmpty()) {
            errorLabel.setText("Phone number is required");
        } else if (!phone.matches("\\d{10}")) {
            errorLabel.setText("Phone must be exactly 10 digits");
        } else if (!phone.matches("^(03|05|07|08|09)\\d{8}$")) {
            errorLabel.setText("Invalid Vietnamese phone number format (03x, 05x, 07x, 08x, 09x)");
        } else if (CustomerDAO.isPhoneExists(phone)) {
            errorLabel.setText("This phone number already exists in system");
        } else {
            errorLabel.setText("");
        }
    }

    private static void validateEmail(String email, Label errorLabel) {
        if (email.isEmpty()) {
            errorLabel.setText("Email is required");
        } else if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Invalid email format");
        } else if (email.length() > 100) {
            errorLabel.setText("Email must not exceed 100 characters");
        } else if (CustomerDAO.isEmailExists(email)) {
            errorLabel.setText("This email already exists in system");
        } else {
            errorLabel.setText("");
        }
    }

    private static void validateAddress(String address, Label errorLabel) {
        if (address.isEmpty()) {
            errorLabel.setText("Address is required");
        } else if (address.length() < 5) {
            errorLabel.setText("Address must be at least 5 characters");
        } else if (address.length() > 200) {
            errorLabel.setText("Address must not exceed 200 characters");
        } else {
            errorLabel.setText("");
        }
    }

    private static void updateSaveButton(TextField nameField, TextField phoneField,
                                         TextField emailField, TextField addressField,
                                         Label nameError, Label phoneError,
                                         Label emailError, Label addressError,
                                         Node saveButton) {
        boolean isValid = nameError.getText().isEmpty() &&
                phoneError.getText().isEmpty() &&
                emailError.getText().isEmpty() &&
                addressError.getText().isEmpty() &&
                !nameField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !addressField.getText().trim().isEmpty();

        saveButton.setDisable(!isValid);
    }
}