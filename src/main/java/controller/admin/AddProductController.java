package controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Product;
import dao.ProductDAO;
import util.DialogUtil;  // nếu bạn có class util này

public class AddProductController {
    @FXML private TextField txtName;
    @FXML private TextField txtCode;
    @FXML private TextField txtBrand;
    @FXML private TextField txtType;
    @FXML private TextField txtPrice;
    @FXML private TextField txtDescription;
    @FXML private TextField txtImage;

    private Stage dialogStage;

    /** Setter để AdminController truyền stage vào */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onSave(ActionEvent e) {
        try {
            // 1. Đọc dữ liệu từ form
            String name  = txtName.getText().trim();
            String code  = txtCode.getText().trim();
            String brand = txtBrand.getText().trim();
            String type  = txtType.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            String desc  = txtDescription.getText().trim();
            String img   = txtImage.getText().trim();

            // 2. Tạo model và lưu vào DB
            Product p = new Product();
            p.setProductName(name);
            p.setProductCode(code);
            p.setBrand(brand);
            p.setType(type);
            p.setPrice(price);
            p.setDescription(desc);
            p.setImage(img);
            // CreatedAt, UpdatedAt sẽ mặc định DB set GETDATE()

            boolean ok = ProductDAO.insert(p);
            if (ok) {
                DialogUtil.info("Success", "New product has been added.");
                dialogStage.close();
            } else {
                DialogUtil.error("Error", "Unable to add the product.");
            }
        } catch (NumberFormatException ex) {
            DialogUtil.error("Invalid Format", "Price must be a number.");
        }
    }

    @FXML
    private void onCancel(ActionEvent e) {
        dialogStage.close();
    }
}
