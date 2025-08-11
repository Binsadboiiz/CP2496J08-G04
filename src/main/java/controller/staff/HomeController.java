package controller.staff;

import dao.ProductDAO;
import dao.InventoryDAO;
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
                System.err.println("Search error: " + e.getMessage());
                loadProducts(ProductDAO.getAll());
                showAlert("Notice", "Search encountered an error. Displaying all products.");
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
        card.setPrefSize(160, 270);
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

        int stockQuantity = InventoryDAO.getCurrentStock(product.getProductID());
        Label stockLabel = new Label("Inventory: " + stockQuantity);

        if (stockQuantity <= 5) {
            stockLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Red for low stock
        } else if (stockQuantity <= 20) {
            stockLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Yellow for medium stock
        } else {
            stockLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // Green for adequate stock
        }

        Button detailsBtn = new Button("Details");
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

        InputStream fallback = getClass().getResourceAsStream("/images/register_logo.png");
        return (fallback != null) ? new Image(fallback, 120, 120, true, true) : null;
    }

    private void showProductDetails(Product product) {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 20; -fx-spacing: 10;");

        NumberFormat format = NumberFormat.getNumberInstance(new Locale("de", "DE"));
        String priceStr = format.format(product.getPrice()) + " VND";

        int currentStock = InventoryDAO.getCurrentStock(product.getProductID());

        Label titleLabel = new Label("PRODUCT INFORMATION");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label nameLabel = new Label("Product Name: " + product.getProductName());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label codeLabel = new Label("Product Code: " + product.getProductCode());
        Label brandLabel = new Label("Brand: " + product.getBrand());
        Label typeLabel = new Label("Type: " + product.getType());
        Label priceLabel = new Label("Price: " + priceStr);
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label stockLabel = new Label("Stock Quantity: " + currentStock);
        stockLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label descLabel = new Label("Description: " + (product.getDescription() != null ? product.getDescription() : "No description"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);

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
        stage.setTitle("Product Details - " + product.getProductName());
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

    @FXML
    private void onRefresh() {
        loadProducts(ProductDAO.getAll());
    }

    @FXML
    private void showLowStockProducts() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Low Stock Products");
        alert.setHeaderText("Products with stock ≤ 10");

        List<Product> allProducts = ProductDAO.getAll();
        StringBuilder content = new StringBuilder();
        boolean hasLowStock = false;

        for (Product product : allProducts) {
            int stock = InventoryDAO.getCurrentStock(product.getProductID());
            if (stock <= 10) {
                content.append(product.getProductName())
                        .append(": ")
                        .append(stock)
                        .append(" items\n");
                hasLowStock = true;
            }
        }

        if (!hasLowStock) {
            alert.setContentText("All products have adequate stock!");
        } else {
            alert.setContentText(content.toString());
        }

        alert.showAndWait();
    }
}