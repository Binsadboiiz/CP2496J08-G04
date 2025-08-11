package controller.staff;

import dao.CustomerDAO;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Customer;

public class EditCustomerController {

    public static void showDialog(Customer customer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer Information");

        TextField nameField = new TextField(customer.getFullName());
        Label nameError = new Label();
        nameError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        TextField phoneField = new TextField(customer.getPhone());
        Label phoneError = new Label();
        phoneError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        TextField emailField = new TextField(customer.getEmail());
        Label emailError = new Label();
        emailError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        TextField addressField = new TextField(customer.getAddress());
        Label addressError = new Label();
        addressError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(10);

        grid.addRow(0, new Label("Full Name:"), nameField);
        grid.addRow(1, new Label(""), nameError);
        grid.addRow(2, new Label("Phone:"), phoneField);
        grid.addRow(3, new Label(""), phoneError);
        grid.addRow(4, new Label("Email:"), emailField);
        grid.addRow(5, new Label(""), emailError);
        grid.addRow(6, new Label("Address:"), addressField);
        grid.addRow(7, new Label(""), addressError);

        dialog.getDialogPane().setContent(grid);
        ButtonType okButton = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Node saveBtn = dialog.getDialogPane().lookupButton(okButton);
        saveBtn.setDisable(true);

        // Validation listeners với thông báo lỗi chi tiết
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateName(newVal.trim(), nameError);
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveBtn);
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePhone(newVal.trim(), phoneError, customer.getCustomerID());
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveBtn);
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateEmail(newVal.trim(), emailError, customer.getCustomerID());
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveBtn);
        });

        addressField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateAddress(newVal.trim(), addressError);
            updateSaveButton(nameField, phoneField, emailField, addressField,
                    nameError, phoneError, emailError, addressError, saveBtn);
        });

        // Validate ban đầu
        validateName(nameField.getText().trim(), nameError);
        validatePhone(phoneField.getText().trim(), phoneError, customer.getCustomerID());
        validateEmail(emailField.getText().trim(), emailError, customer.getCustomerID());
        validateAddress(addressField.getText().trim(), addressError);
        updateSaveButton(nameField, phoneField, emailField, addressField,
                nameError, phoneError, emailError, addressError, saveBtn);

        dialog.setResultConverter(button -> {
            if (button == okButton) {
                customer.setFullName(nameField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setAddress(addressField.getText().trim());
                // Không thay đổi loyalty points
            }
            return null;
        });

        dialog.showAndWait();
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

    private static void validatePhone(String phone, Label errorLabel, int currentCustomerID) {
        if (phone.isEmpty()) {
            errorLabel.setText("Phone number is required");
        } else if (!phone.matches("\\d{10}")) {
            errorLabel.setText("Phone must be exactly 10 digits");
        } else if (!phone.matches("^(03|05|07|08|09)\\d{8}$")) {
            errorLabel.setText("Invalid Vietnamese phone number format (03x, 05x, 07x, 08x, 09x)");
        } else if (CustomerDAO.isPhoneExistsForOtherCustomer(phone, currentCustomerID)) {
            errorLabel.setText("This phone number already exists for another customer");
        } else {
            errorLabel.setText("");
        }
    }

    private static void validateEmail(String email, Label errorLabel, int currentCustomerID) {
        if (email.isEmpty()) {
            errorLabel.setText("Email is required");
        } else if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Invalid email format");
        } else if (email.length() > 100) {
            errorLabel.setText("Email must not exceed 100 characters");
        } else if (CustomerDAO.isEmailExistsForOtherCustomer(email, currentCustomerID)) {
            errorLabel.setText("This email already exists for another customer");
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