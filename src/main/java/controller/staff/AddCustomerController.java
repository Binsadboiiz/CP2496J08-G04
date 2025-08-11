package controller.staff;

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

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        grid.addRow(0, nameLabel, nameField);
        grid.addRow(1, phoneLabel, phoneField);
        grid.addRow(2, emailLabel, emailField);
        grid.addRow(3, addressLabel, addressField);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.setDisable(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> validate(nameField, phoneField, emailField, addressField, saveButton));
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> validate(nameField, phoneField, emailField, addressField, saveButton));
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validate(nameField, phoneField, emailField, addressField, saveButton));
        addressField.textProperty().addListener((obs, oldVal, newVal) -> validate(nameField, phoneField, emailField, addressField, saveButton));

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Customer customer = new Customer(nameField.getText(), phoneField.getText(), addressField.getText());
                customer.setEmail(emailField.getText());
                return customer;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static void validate(TextField name, TextField phone, TextField email, TextField address, Node saveButton) {
        boolean isValid = !name.getText().trim().isEmpty()
                && phone.getText().matches("\\d{9,11}")
                && email.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                && !address.getText().trim().isEmpty();
        saveButton.setDisable(!isValid);
    }
}
