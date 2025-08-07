//package controller.cashier;
//
//import dao.BestSellingProductDAO;
//import dao.DashboardDAO;
//import dao.DatabaseConnection;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.VBox;
//import model.BestSellingProduct;
//import model.Product;
//import model.TransactionDetail;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ControlPanelConfigController {
//
//    @FXML private Label revenueLabel, invoiceCountLabel;
//    @FXML private FlowPane productCardContainer;
//    @FXML private ImageView productImageView;
//    @FXML private Label detailProductName, detailProductCode, detailBrand, detailType, detailPrice, detailDescription;
//
//    @FXML private TableView<TransactionDetail> transactionTable;
//    @FXML private TableColumn<TransactionDetail, String> productNameCol;
//    @FXML private TableColumn<TransactionDetail, String> customerNameCol;
//    @FXML private TableColumn<TransactionDetail, String> paymentMethodCol;
//    @FXML private TableColumn<TransactionDetail, Number> priceCol;
//    @FXML private TableColumn<TransactionDetail, Number> quantityCol;
//
//
//    private final DashboardDAO dashboardDAO = new DashboardDAO();
//    private final BestSellingProductDAO bestSellingProductDAO;
//
//    {
//        try {
//            bestSellingProductDAO = new BestSellingProductDAO(DatabaseConnection.getConnection());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @FXML
//    public void initialize() {
//        loadRevenueAndInvoices();
//        loadProductCards();
//    }
//
//    private void loadRevenueAndInvoices() {
//        try {
//            double totalRevenue = dashboardDAO.getTotalRevenue();
//            int invoiceCount = dashboardDAO.getInvoiceCount();
//            revenueLabel.setText(String.format("$%.2f", totalRevenue));
//            invoiceCountLabel.setText(String.valueOf(invoiceCount));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Failed to load revenue and invoice count.");
//        }
//    }
//
//    private void loadProductCards() {
//        productCardContainer.getChildren().clear();
//        try {
//            List<Product> products = dashboardDAO.getAllProducts();
//            for (Product product : products) {
//                VBox productCard = createProductCard(product);
//                productCardContainer.getChildren().add(productCard);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Failed to load product cards.");
//        }
//    }
//
//    private VBox createProductCard(Product product) {
//        VBox card = new VBox(5);
//        card.setPrefWidth(120);
//        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 8; -fx-padding: 10; -fx-background-radius: 8; -fx-background-color: #f9f9f9;");
//
//        ImageView imageView = new ImageView();
//        File imageFile = new File(product.getImage());
//        if (imageFile.exists()) {
//            imageView.setImage(new Image(imageFile.toURI().toString()));
//        }
//        imageView.setFitWidth(100);
//        imageView.setFitHeight(100);
//        imageView.setPreserveRatio(true);
//
//        Label nameLabel = new Label(product.getProductName());
//        nameLabel.setStyle("-fx-font-weight: bold; -fx-wrap-text: true;");
//        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
//
//        card.getChildren().addAll(imageView, nameLabel, priceLabel);
//
//        card.setOnMouseClicked(event -> showProductDetails(product));
//        return card;
//    }
//
//    private void showProductDetails(Product product) {
//        detailProductName.setText("Name: " + product.getProductName());
//        detailProductCode.setText("Code: " + product.getProductCode());
//        detailBrand.setText("Brand: " + product.getBrand());
//        detailType.setText("Type: " + product.getType());
//        detailPrice.setText("Price: $" + product.getPrice());
//        detailDescription.setText("Description: " + product.getDescription());
//
//        File imageFile = new File(product.getImage());
//        if (imageFile.exists()) {
//            productImageView.setImage(new Image(imageFile.toURI().toString()));
//        } else {
//            productImageView.setImage(null);
//        }
//    }
//
//    private void showAlert(Alert.AlertType type, String message) {
//        Alert alert = new Alert(type);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//}
