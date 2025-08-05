package controller.staff;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Customer;

public class EditCustomerController {

    public static void showDialog(Customer customer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin khách hàng");

        TextField nameField = new TextField(customer.getFullName());
        TextField phoneField = new TextField(customer.getPhone());
        TextField emailField = new TextField(customer.getEmail());
        TextField addressField = new TextField(customer.getAddress());
        TextField pointsField = new TextField(String.valueOf(customer.getLoyaltyPoints()));

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        grid.addRow(0, new Label("Họ tên:"), nameField);
        grid.addRow(1, new Label("SĐT:"), phoneField);
        grid.addRow(2, new Label("Email:"), emailField);
        grid.addRow(3, new Label("Địa chỉ:"), addressField);
        grid.addRow(4, new Label("Điểm tích lũy:"), pointsField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
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
}
