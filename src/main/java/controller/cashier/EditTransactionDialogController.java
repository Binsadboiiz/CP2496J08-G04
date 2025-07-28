package controller.cashier;

import dao.ProductDAO;
import dao.TransactionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Product;
import model.Transaction;

import java.util.List;

public class EditTransactionDialogController {

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

    @FXML
    public void initialize() {
        loadSummaryData();
        loadRecentTransactions();
        loadTopSellingProducts();
    }

    private void loadSummaryData() {
        // Giả lập lấy dữ liệu từ DB (sau này bạn có thể gọi từ DAO)
        double todayRevenue = TransactionDAO.getTodayRevenue(); // ví dụ 5,000,000 VND
        int todayTransactions = TransactionDAO.getTodayTransactionCount(); // ví dụ 20
        double avgInvoice = todayTransactions == 0 ? 0 : todayRevenue / todayTransactions;

        lblTodayRevenue.setText(String.format("🪙 Doanh thu hôm nay: %,d VND", (int) todayRevenue));
        lblTodayTransactions.setText("🔄 Số giao dịch hôm nay: " + todayTransactions);
        lblAvgInvoiceValue.setText(String.format("📊 Giá trị trung bình mỗi hóa đơn: %,d VND", (int) avgInvoice));
    }

    private void loadRecentTransactions() {
        colInvoiceID.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getInvoiceID()).asObject());
        colCustomer.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCustomer()));
        colTime.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));
        colAmount.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject());
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        List<Transaction> transactions = TransactionDAO.getRecentTransactions();
        ObservableList<Transaction> list = FXCollections.observableArrayList(transactions);
        tblTransactions.setItems(list);
    }

    private void loadTopSellingProducts() {
        VBox[] productCards = {productCard1, productCard2, productCard3, productCard4, productCard5};

        List<Product> topProducts = ProductDAO.getTopSellingProducts();
        int count = Math.min(topProducts.size(), productCards.length);

        for (int i = 0; i < count; i++) {
            Product product = topProducts.get(i);
            VBox card = productCards[i];
            card.getChildren().clear();

            // Load Image từ DB nếu có (giả lập URL)
            String imageUrl = product.getImage() != null ? product.getImage() : "https://via.placeholder.com/100";
            ImageView imageView = new ImageView(new Image(imageUrl));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            Label name = new Label(product.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label sales = new Label(product.getSales() + " sales");
            sales.setStyle("-fx-text-fill: gray;");

            card.getChildren().addAll(imageView, name, sales);
        }

        // Clear remaining cards if products < 5
        for (int i = count; i < productCards.length; i++) {
            productCards[i].getChildren().clear();
        }
    }
}
