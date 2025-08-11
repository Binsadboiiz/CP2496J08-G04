package controller.staff;

import dao.ProductDAO;
import dao.StockEntryDetailDAO;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeController {

    @FXML private FlowPane productGrid;
    @FXML private TextField searchField;

    private StaffController staffController;

    @FXML
    public void initialize() {
        loadProducts(ProductDAO.getAll());
    }

    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        if (searchText == null || searchText.trim().isEmpty()) {
            loadProducts(ProductDAO.getAll());
        } else {
            try {
                loadProducts(ProductDAO.search(searchText.trim()));
            } catch (Exception e) {
                // Nếu có lỗi với search, fallback về getAll
                System.err.println("Lỗi khi tìm kiếm: " + e.getMessage());
                loadProducts(ProductDAO.getAll());
                showAlert("Thông báo", "Tìm kiếm gặp lỗi. Hiển thị tất cả sản phẩm.");
            }
        }
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        loadProducts(ProductDAO.getAll());
    }

    private void loadProducts(List<Product> products) {
        productGrid.getChildren().clear();
        for (Product product : products) {
            productGrid.getChildren().add(createProductCard(product));
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(160, 270); // Giảm chiều cao vì chỉ có 1 nút
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12; -fx-background-color: #fff;");

        ImageView imageView = new ImageView();
        imageView.setImage(loadProductImage(product.getImage()));
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(140);

        String priceStr = NumberFormat.getNumberInstance(new Locale("de", "DE"))
                .format(product.getPrice()) + " VND";
        Label priceLabel = new Label(priceStr);
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        // Lấy số lượng tồn kho
        int stockQuantity = StockEntryDetailDAO.getTotalReceivedByProduct(product.getProductID());
        Label stockLabel = new Label("Tồn kho: " + stockQuantity);

        // Thêm màu sắc để phân biệt mức tồn kho
        if (stockQuantity <= 5) {
            stockLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Đỏ cho tồn kho thấp
        } else if (stockQuantity <= 20) {
            stockLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Vàng cho tồn kho trung bình
        } else {
            stockLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // Xanh cho tồn kho đầy đủ
        }

        // Chỉ có nút Chi tiết
        Button detailsBtn = new Button("Chi tiết");
        detailsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-pref-width: 120;");
        detailsBtn.setOnAction(e -> showProductDetails(product));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, detailsBtn);
        return card;
    }

    private Image loadProductImage(String imgName) {
        try {
            if (imgName != null && !imgName.isEmpty()) {
                if (imgName.startsWith("http://") || imgName.startsWith("https://")) {
                    return new Image(imgName, 120, 120, true, true);
                } else if (imgName.startsWith("/") || imgName.matches("^[A-Za-z]:.*")) {
                    return new Image("file:///" + imgName.replace("\\", "/"), 120, 120, true, true);
                } else {
                    InputStream imageStream = getClass().getResourceAsStream("/images/" + imgName);
                    if (imageStream != null) {
                        return new Image(imageStream, 120, 120, true, true);
                    }
                }
            }
        } catch (Exception ignored) {}

        // Fallback image
        InputStream fallback = getClass().getResourceAsStream("/images/register_logo.png");
        return (fallback != null) ? new Image(fallback, 120, 120, true, true) : null;
    }

    private void showProductDetails(Product product) {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 20; -fx-spacing: 10;");

        NumberFormat format = NumberFormat.getNumberInstance(new Locale("de", "DE"));
        String priceStr = format.format(product.getPrice()) + " VND";

        // Lấy thông tin tồn kho chi tiết
        int totalReceived = StockEntryDetailDAO.getTotalReceivedByProduct(product.getProductID());

        // Tạo các label thông tin sản phẩm
        Label titleLabel = new Label("THÔNG TIN SẢN PHẨM");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label nameLabel = new Label("Tên sản phẩm: " + product.getProductName());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label codeLabel = new Label("Mã sản phẩm: " + product.getProductCode());
        Label brandLabel = new Label("Thương hiệu: " + product.getBrand());
        Label typeLabel = new Label("Loại: " + product.getType());
        Label priceLabel = new Label("Giá: " + priceStr);
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label stockLabel = new Label("Số lượng tồn kho: " + totalReceived);
        stockLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label descLabel = new Label("Mô tả: " + (product.getDescription() != null ? product.getDescription() : "Không có"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);

        // Thêm hình ảnh sản phẩm
        ImageView productImage = new ImageView();
        productImage.setImage(loadProductImage(product.getImage()));
        productImage.setFitWidth(200);
        productImage.setFitHeight(200);
        productImage.setPreserveRatio(true);

        content.getChildren().addAll(
                titleLabel,
                productImage,
                nameLabel,
                codeLabel,
                brandLabel,
                typeLabel,
                priceLabel,
                stockLabel,
                descLabel
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(500, 600);

        Stage stage = new Stage();
        stage.setScene(new Scene(scrollPane, 500, 600));
        stage.setTitle("Chi tiết sản phẩm - " + product.getProductName());
        stage.setResizable(false);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Làm mới dữ liệu sản phẩm
    @FXML
    private void onRefresh() {
        loadProducts(ProductDAO.getAll());
    }

    // Hiển thị sản phẩm có tồn kho thấp
    @FXML
    private void showLowStockProducts() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sản phẩm tồn kho thấp");
        alert.setHeaderText("Các sản phẩm có tồn kho ≤ 10");

        var lowStockProducts = StockEntryDetailDAO.getLowStockProducts(10);
        if (lowStockProducts.isEmpty()) {
            alert.setContentText("Tất cả sản phẩm đều có đủ tồn kho!");
        } else {
            StringBuilder content = new StringBuilder();
            for (var product : lowStockProducts) {
                content.append(product.getProductName())
                        .append(": ")
                        .append(product.getQuantity())
                        .append(" sản phẩm\n");
            }
            alert.setContentText(content.toString());
        }

        alert.showAndWait();
    }
}