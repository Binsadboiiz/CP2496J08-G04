package controller.cashier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Product;
import model.Transaction;

import java.util.Arrays;
import java.util.List;

public class ControllPanelController {

    @FXML private Label lblTodayRevenue;
    @FXML private Label lblTodayTransactions;
    @FXML private Label lblAvgInvoiceValue;

    @FXML private TableView<Transaction> tblTransactions;
    @FXML private TableColumn<Transaction, Integer> colInvoiceID;
    @FXML private TableColumn<Transaction, String> colCustomer;
    @FXML private TableColumn<Transaction, String> colTime;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colStatus;

    @FXML private VBox productCard1, productCard2, productCard3, productCard4, productCard5;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadRecentTransactions();
        loadSummaryData();
        loadTopSellingProducts();
    }

    private void loadSummaryData() {
        double totalRevenue = transactionList.stream().mapToDouble(Transaction::getAmount).sum();
        int totalTransactions = transactionList.size();
        double avgInvoice = totalTransactions > 0 ? totalRevenue / totalTransactions : 0;

        lblTodayRevenue.setText("🪙 Revenue today: " + String.format("%,.0f", totalRevenue) + " VND");
        lblTodayTransactions.setText("🔄Number of transactions today : " + totalTransactions);
        lblAvgInvoiceValue.setText("📊 Average value per invoice: " + String.format("%,.0f", avgInvoice) + " VND");
    }

    private void loadRecentTransactions() {
        transactionList.setAll(Arrays.asList(
                new Transaction(101, "Nguyen Van A", "09:15 AM", 1250000, "Paid"),
                new Transaction(102, "Tran Thi B", "10:30 AM", 850000, "Paid"),
                new Transaction(103, "Le Van C", "11:45 AM", 640000, "Paid"),
                new Transaction(104, "Pham Minh D", "01:20 PM", 2320000, "Paid"),
                new Transaction(105, "Hoang Thu E", "03:10 PM", 1200000, "Paid")
        ));

        colInvoiceID.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getInvoiceID()).asObject());
        colCustomer.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCustomer()));
        colTime.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));
        colAmount.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject());
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        tblTransactions.setItems(transactionList);
    }

    private void loadTopSellingProducts() {
        List<Product> topProducts = Arrays.asList(
                new Product(1, "iPhone 15 Pro Max", "iphone15", "Apple", "Smartphone", 31990000, "Flagship model", "iphone15.png", "2025-01-01", "2025-01-02", "iPhone 15 Pro Max", 120),
                new Product(2, "Samsung Galaxy S24 Ultra", "s24ultra", "Samsung", "Smartphone", 28990000, "Flagship Samsung", "s24ultra.png", "2025-01-01", "2025-01-02", "Samsung Galaxy S24 Ultra", 95),
                new Product(3, "Xiaomi 14", "xiaomi14", "Xiaomi", "Smartphone", 20990000, "High-end Xiaomi", "Xiaomi14.png", "2025-01-01", "2025-01-02", "Xiaomi 14", 75),
                new Product(4, "Oppo Reno 5", "reno5", "Oppo", "Smartphone", 23990000, "Flagship Oppo", "OppoReno5.png", "2025-01-01", "2025-01-02", "Oppo Reno 5", 60),
                new Product(5, "Vivo X100", "vivox100", "Vivo", "Smartphone", 19990000, "Vivo high-end", "VivoX100.png", "2025-01-01", "2025-01-02", "Vivo X100", 45)
        );

        VBox[] productCards = {productCard1, productCard2, productCard3, productCard4, productCard5};
        for (int i = 0; i < productCards.length; i++) {
            VBox card = productCards[i];
            card.getChildren().clear();

            Product product = topProducts.get(i);

            Image image = new Image(getClass().getResourceAsStream("/images/" + product.getImage()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            Label name = new Label(product.getName());
            name.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label sales = new Label(product.getSales() + "product sold ");
            sales.setTextFill(Color.GRAY);

            Tooltip.install(card, new Tooltip("Revenue: " + String.format("%,.0f", product.getPrice() * product.getSales()) + " VND"));

            card.setSpacing(5);
            card.getChildren().addAll(imageView, name, sales);

            card.setOnMouseClicked((MouseEvent e) -> showProductDetail(product));
            card.setOnMouseEntered(e -> card.setStyle("-fx-border-color: #FF9900; -fx-border-width: 2; -fx-padding: 10;"));
            card.setOnMouseExited(e -> card.setStyle("-fx-border-color: black; -fx-padding: 10;"));
        }
    }

    private void showProductDetail(Product product) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Product Details");

        // Tạo layout chứa ảnh và thông tin
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        // Load ảnh sản phẩm
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/" + product.getImage())));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        Label name = new Label(product.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label details = new Label(
                "Mã: " + product.getProductCode() +
                        "\nFirm: " + product.getBrand() +
                        "\nType: " + product.getType() +
                        "\nPrice: " + String.format("%,.0f VND", product.getPrice()) +
                        "\nSold: " + product.getSales() + " product "
        );

        content.getChildren().addAll(imageView, name, details);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

}
