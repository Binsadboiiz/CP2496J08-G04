package controller.cashier;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Promotion;

import java.time.LocalDate;

public class EditPromotionDialog {
    private Promotion promotion;

    public EditPromotionDialog(Promotion promotion) {
        this.promotion = promotion;
    }

    public Promotion showAndWait() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Sửa khuyến mãi");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10); grid.setHgap(10);

        TextField nameField = new TextField(promotion.getPromotionName());
        TextField descField = new TextField(promotion.getDescription());
        TextField discountField = new TextField(String.valueOf(promotion.getDiscountPercentage()));
        DatePicker startDate = new DatePicker(promotion.getStartDate());
        DatePicker endDate = new DatePicker(promotion.getEndDate());

        grid.add(new Label("Tên khuyến mãi:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Mô tả:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Tỷ lệ giảm (%):"), 0, 2);
        grid.add(discountField, 1, 2);
        grid.add(new Label("Từ ngày:"), 0, 3);
        grid.add(startDate, 1, 3);
        grid.add(new Label("Đến ngày:"), 0, 4);
        grid.add(endDate, 1, 4);

        Button btnOK = new Button("OK");
        Button btnCancel = new Button("Hủy");
        grid.add(btnOK, 0, 5);
        grid.add(btnCancel, 1, 5);

        final Promotion[] result = new Promotion[1];

        btnOK.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String desc = descField.getText();
                double discount = Double.parseDouble(discountField.getText());
                LocalDate start = startDate.getValue();
                LocalDate end = endDate.getValue();
                if (name.isEmpty() || start == null || end == null) throw new Exception("Vui lòng nhập đầy đủ!");
                result[0] = new Promotion(
                        promotion.getPromotionID(),
                        name,
                        desc,
                        discount,
                        start,
                        end
                );
                dialog.close();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ!");
                alert.showAndWait();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        dialog.setScene(new Scene(grid));
        dialog.showAndWait();

        return result[0];
    }
}
