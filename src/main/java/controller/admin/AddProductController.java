package controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private Product editingProduct = null;

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

    public void setProduct(Product product) {
        this.editingProduct = product;
        // Gán dữ liệu lên form
        txtName.setText(product.getProductName());
        txtPrice.setText(String.valueOf(product.getPrice()));

    }
    public void setEditProduct(Product product) {
        this.editingProduct = product;
        txtName.setText(product.getProductName());
        txtCode.setText(product.getProductCode());
        txtBrand.setText(product.getBrand());
        txtType.setText(product.getType());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtDescription.setText(product.getDescription());
        txtImage.setText(product.getImage());
    }
    @FXML
    private void onSave() {
        String name = txtName.getText().trim();
        String brand = txtBrand.getText().trim();
        String type = txtType.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String desc = txtDescription.getText().trim();
        String image = txtImage.getText().trim();
        double price = 0;

        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new Exception();
        } catch (Exception e) {
            showAlert("Giá sản phẩm không hợp lệ!");
            return;
        }

        if (editingProduct != null) {
            // EDIT
            editingProduct.setProductName(name);
            editingProduct.setBrand(brand);
            editingProduct.setType(type);
            editingProduct.setPrice(price);
            editingProduct.setDescription(desc);
            editingProduct.setImage(image);
            boolean ok1 = ProductDAO.update(editingProduct);
            showAlert(ok1 ? "Cập nhật thành công!" : "Cập nhật thất bại!");
        } else {
            // ADD
            Product product = new Product();
            product.setProductName(name);
            product.setBrand(brand);
            product.setType(type);
            product.setPrice(price);
            product.setDescription(desc);
            product.setImage(image);
            boolean ok2 = ProductDAO.insert(product);
            showAlert(ok2 ? "Thêm mới thành công!" : "Thêm mới thất bại!");
        }

        ((Stage) txtName.getScene().getWindow()).close();
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

}
