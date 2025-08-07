package controller.staff;

import dao.ProductDAO;
import model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.InputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeController {
    @FXML private FlowPane productGrid;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        loadProducts(ProductDAO.getAll());
    }

    private void loadProducts(List<Product> products) {
        productGrid.getChildren().clear();
        for (Product p : products) {
            VBox card = createProductCard(p);
            productGrid.getChildren().add(card);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(160, 250);
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12; -fx-background-color: #fff;");

        ImageView img = new ImageView();
        String imgName = product.getImage() != null && !product.getImage().isEmpty() ? product.getImage() : "placeholder.png";

        Image image = null;

// Nếu đường dẫn là URL (bắt đầu bằng http hoặc https)
        if (imgName.startsWith("http://") || imgName.startsWith("https://")) {
            try {
                image = new Image(imgName, 120, 120, true, true);
            } catch (Exception e) {
                System.err.println("Không thể tải ảnh từ link: " + imgName);
            }
        } else {
            // Ảnh local từ resources (/images/)
            InputStream imageStream = getClass().getResourceAsStream("/images/" + imgName);
            if (imageStream != null) {
                image = new Image(imageStream, 120, 120, true, true);
            } else {
                System.err.println("Không tìm thấy ảnh local: /images/" + imgName);
            }
        }

// Nếu không có ảnh nào được tải, dùng placeholder
        if (image == null) {
            InputStream placeholder = getClass().getResourceAsStream("/images/placeholder.png");
            if (placeholder != null) {
                image = new Image(placeholder, 120, 120, true, true);
            } else {
                System.err.println("Không tìm thấy placeholder.png");
            }
        }

        img.setImage(image);


        Label name = new Label(product.getProductName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-wrap-text: true; -fx-text-alignment: center;");
        name.setWrapText(true);
        name.setMaxWidth(140);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("de", "DE"));
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setMinimumFractionDigits(0);

        Label price = new Label(numberFormat.format(product.getPrice()) + " VND");
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Button detailBtn = new Button("Chi tiết");
        detailBtn.setOnAction(e -> showProductDetail(product));

        Button addBtn = new Button("Thêm vào hóa đơn");
        addBtn.setOnAction(e -> addToInvoice(product));

        card.getChildren().addAll(img, name, price, detailBtn, addBtn);
        return card;
    }

    private void showProductDetail(Product product) {
        Stage stage = new Stage();
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 20;");

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("de", "DE"));
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setMinimumFractionDigits(0);

        box.getChildren().addAll(
                new Label("Tên: " + product.getProductName()),
                new Label("Mã: " + product.getProductCode()),
                new Label("Hãng: " + product.getBrand()),
                new Label("Loại: " + product.getType()),
                new Label("Giá: " + numberFormat.format(product.getPrice()) + " VND"),
                new Label("Mô tả: " + product.getDescription())
        );
        stage.setScene(new Scene(box, 350, 300));
        stage.setTitle("Chi tiết sản phẩm");
        stage.show();
    }

    private StaffController staffController;

    // Setter để nhận tham chiếu từ StaffController
    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    // Phương thức đã được sửa đổi
    private void addToInvoice(Product product) {
        // Thêm sản phẩm vào danh sách tạm của CreateInvoiceController
        CreateInvoiceController.addProductToInvoice(product);

        // Chuyển màn hình qua trang tạo hóa đơn
        if (staffController != null) {
            try {
                staffController.loadCreateInvoice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Trường hợp lỗi (không có tham chiếu StaffController)
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể chuyển trang. Vui lòng thử lại.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        loadProducts(ProductDAO.search(keyword));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        loadProducts(ProductDAO.getAll());
    }
}