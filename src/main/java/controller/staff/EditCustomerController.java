package controller.staff;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Customer;

public class EditCustomerController {

    public static void showDialog(Customer customer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer Information");

        TextField nameField = new TextField(customer.getFullName());
        TextField phoneField = new TextField(customer.getPhone());
        TextField emailField = new TextField(customer.getEmail());
        TextField addressField = new TextField(customer.getAddress());
        TextField pointsField = new TextField(String.valueOf(customer.getLoyaltyPoints()));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, new Label("Full Name:"), nameField);
        grid.addRow(1, new Label("Phone:"), phoneField);
        grid.addRow(2, new Label("Email:"), emailField);
        grid.addRow(3, new Label("Address:"), addressField);
        grid.addRow(4, new Label("Loyalty Points:"), pointsField);

        dialog.getDialogPane().setContent(grid);
        ButtonType okButton = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Node saveBtn = dialog.getDialogPane().lookupButton(okButton);
        saveBtn.setDisable(true);

        Runnable validateAll = () -> validate(nameField, phoneField, emailField, addressField, saveBtn);
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateAll.run());
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> validateAll.run());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateAll.run());
        addressField.textProperty().addListener((obs, oldVal, newVal) -> validateAll.run());

        dialog.setResultConverter(button -> {
            if (button == okButton) {
                customer.setFullName(nameField.getText());
                customer.setPhone(phoneField.getText());
                customer.setEmail(emailField.getText());
                customer.setAddress(addressField.getText());
                try {
                    customer.setLoyaltyPoints(Integer.parseInt(pointsField.getText()));
                } catch (NumberFormatException e) {
                    customer.setLoyaltyPoints(0);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void validate(TextField name, TextField phone, TextField email, TextField address, Node saveButton) {
        boolean isValid = !name.getText().trim().isEmpty()
                && phone.getText().matches("\\d{9,11}")
                && email.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                && !address.getText().trim().isEmpty();
        saveButton.setDisable(!isValid);
    }
}
