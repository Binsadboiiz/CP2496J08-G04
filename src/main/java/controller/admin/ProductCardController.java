package controller.admin;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import model.Product;
import java.io.InputStream;

public class ProductCardController {
    @FXML private ImageView imgProduct;
    @FXML private Label lblName, lblBrand, lblType, lblPrice, lblDesc;
    @FXML private Button btnEdit, btnDelete;

    private Product product;
    private ProductManagementController parent;

    public void setData(Product p, ProductManagementController parent) {
        this.product = p;
        this.parent = parent;
        lblName.setText(p.getProductName());
        lblBrand.setText(p.getBrand());
        lblType.setText(p.getType());
        lblPrice.setText(String.format("%,d₫", (long)p.getPrice()));
        lblDesc.setText(p.getDescription());

        String imgName = (product.getImage() != null && !product.getImage().isEmpty()) ? product.getImage() : "placeholder.png";
        Image image = null;

        try {
            if (imgName.startsWith("http://") || imgName.startsWith("https://")) {
                image = new Image(imgName, 120, 120, true, true);
                if (image.isError()) {
                    System.err.println("Lỗi tải ảnh online: " + image.getException());
                    image = null;
                }
            } else if (imgName.startsWith("/") || imgName.matches("^[A-Za-z]:.*")) {
                imgName = imgName.replace("\"", "");
                String localFileUrl = "file:///" + imgName.replace("\\", "/");
                image = new Image(localFileUrl, 120, 120, true, true);
            } else {
                InputStream imageStream = getClass().getResourceAsStream("/images/" + imgName);
                if (imageStream != null) {
                    image = new Image(imageStream, 120, 120, true, true);
                    if (image.isError()) {
                        System.err.println("Lỗi load ảnh local: " + image.getException());
                        image = null;
                    }
                } else {
                    System.err.println("Không tìm thấy ảnh local: /images/" + imgName);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception khi tải ảnh: " + e.getMessage());
            image = null;
        }

        // Nếu không có ảnh, dùng placeholder
        if (image == null) {
            InputStream placeholder = getClass().getResourceAsStream("/images/placeholder.png");
            if (placeholder != null) {
                image = new Image(placeholder, 120, 120, true, true);
            } else {
                System.err.println("Không tìm thấy placeholder.png, vui lòng kiểm tra lại thư mục /images/");
            }
        }

        imgProduct.setImage(image); // CHỈ SỬ DỤNG imgProduct ở đây!
    }


    @FXML
    private void onEdit() { parent.editProduct(product); }

    @FXML
    private void onDelete() { parent.deleteProduct(product); }
}
