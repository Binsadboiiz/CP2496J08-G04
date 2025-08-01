package controller.staff;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AddCustomerController {

    public static Customer showDialog() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Thêm khách hàng");

        Label nameLabel = new Label("Họ tên:");
        TextField nameField = new TextField();

        Label phoneLabel = new Label("Số điện thoại:");
        TextField phoneField = new TextField();

        Label typeLabel = new Label("Loại khách:");
        TextField typeField = new TextField();

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        grid.addRow(0, nameLabel, nameField);
        grid.addRow(1, phoneLabel, phoneField);
        grid.addRow(2, typeLabel, typeField);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Customer(nameField.getText(), phoneField.getText(), typeField.getText());
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
