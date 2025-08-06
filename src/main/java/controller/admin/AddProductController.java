package controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Product;
import dao.ProductDAO;

public class AddProductController {
    @FXML private TextField txtName;
    @FXML private TextField txtCode;
    @FXML private TextField txtBrand;
    @FXML private TextField txtType;
    @FXML private TextField txtPrice;
    @FXML private TextField txtDescription;
    @FXML private TextField txtImage;

    private Stage dialogStage;
    private Product editingProduct = null;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /** Gọi khi mở dialog cho EDIT */
    public void setEditProduct(Product product) {
        this.editingProduct = product;
        if (product != null) {
            txtName.setText(product.getProductName());
            txtCode.setText(product.getProductCode());
            txtBrand.setText(product.getBrand());
            txtType.setText(product.getType());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtDescription.setText(product.getDescription());
            txtImage.setText(product.getImage());
        }
    }

    /** Gọi khi mở dialog cho ADD (không cần setEditProduct) */
    @FXML
    private void onSave(ActionEvent e) {
        String name = txtName.getText().trim();
        String code = txtCode.getText().trim();
        String brand = txtBrand.getText().trim();
        String type = txtType.getText().trim();
        String desc = txtDescription.getText().trim();
        String img = txtImage.getText().trim();
        double price;

        try {
            price = Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException ex) {
            showAlert("Product price is invalid!");
            return;
        }

        if (name.isEmpty() || code.isEmpty() || brand.isEmpty() || type.isEmpty()) {
            showAlert("Please fill all required fields!");
            return;
        }

        Product product = new Product();
        product.setProductName(name);
        product.setProductCode(code);
        product.setBrand(brand);
        product.setType(type);
        product.setPrice(price);
        product.setDescription(desc);
        product.setImage(img);

        boolean ok = ProductDAO.insert(product);
        showAlert(ok ? "Added new product successfully!" : "Add failed!");
        if (ok && dialogStage != null) dialogStage.close();
    }

    /** Gọi khi mở dialog cho EDIT (setEditProduct xong) */
    @FXML
    private void onUpdate(ActionEvent e) {
        if (editingProduct == null) {
            showAlert("Error: No product is being edited!");
            return;
        }
        String name = txtName.getText().trim();
        String code = txtCode.getText().trim();
        String brand = txtBrand.getText().trim();
        String type = txtType.getText().trim();
        String desc = txtDescription.getText().trim();
        String img = txtImage.getText().trim();
        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException ex) {
            showAlert("Product price is invalid!");
            return;
        }

        editingProduct.setProductName(name);
        editingProduct.setBrand(brand);
        editingProduct.setType(type);
        editingProduct.setPrice(price);
        editingProduct.setDescription(desc);
        editingProduct.setImage(img);

        boolean ok = ProductDAO.update(editingProduct);
        showAlert(ok ? "Product updated successfully!" : "Update failed!");
        if (ok && dialogStage != null) dialogStage.close();
    }

    @FXML
    private void onCancel(ActionEvent e) {
        if (dialogStage != null) dialogStage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
