package controller.staff;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EditCustomerController {

    public static void showDialog(Customer customer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin khách hàng");

        TextField nameField = new TextField(customer.getName());
        TextField phoneField = new TextField(customer.getPhone());
        TextField typeField = new TextField(customer.getType());

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        grid.addRow(0, new Label("Họ tên:"), nameField);
        grid.addRow(1, new Label("SĐT:"), phoneField);
        grid.addRow(2, new Label("Loại:"), typeField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                customer.setName(nameField.getText());
                customer.setPhone(phoneField.getText());
                customer.setType(typeField.getText());
            }
            return null;
        });

        dialog.showAndWait();
    }
}
