package controller.warehousestaff;

import dao.InventorySummaryDAO;
import dao.InventorySummaryDAO.InventorySummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InventoryManagementController {

    // UI Components for inventory table
    @FXML private TableView<InventoryItem> tblInventory;
    @FXML private TableColumn<InventoryItem, String> colProductCode;
    @FXML private TableColumn<InventoryItem, String> colProductName;
    @FXML private TableColumn<InventoryItem, String> colBrand;
    @FXML private TableColumn<InventoryItem, Integer> colTotalReceived;
    @FXML private TableColumn<InventoryItem, Integer> colTotalLoss;
    @FXML private TableColumn<InventoryItem, Integer> colCurrentStock;
    @FXML private TableColumn<InventoryItem, String> colStatus;

    // UI Components for filtering and search
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private ComboBox<String> cbBrandFilter;

    // UI Components for charts
    @FXML private PieChart pieStockStatus;
    @FXML private BarChart<String, Number> barTopProducts;

    // UI Components for statistics
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStockItems;
    @FXML private Label lblOutOfStockItems;
    @FXML private Label lblTotalValue;

    // Data
    private ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();
    private ObservableList<InventoryItem> filteredList = FXCollections.observableArrayList();

    // Constants for stock status thresholds
    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int CRITICAL_STOCK_THRESHOLD = 5;

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadInventoryData();
        setupCharts();
        updateStatistics();
    }

    private void setupTable() {
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colTotalReceived.setCellValueFactory(new PropertyValueFactory<>("totalReceived"));
        colTotalLoss.setCellValueFactory(new PropertyValueFactory<>("totalLoss"));
        colCurrentStock.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add cell factory for color coding based on stock status
        colStatus.setCellFactory(column -> new TableCell<InventoryItem, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Hết hàng":
                            setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                            break;
                        case "Sắp hết":
                            setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00;");
                            break;
                        case "Ít hàng":
                            setStyle("-fx-background-color: #fffde7; -fx-text-fill: #f57f17;");
                            break;
                        case "Đủ hàng":
                            setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        tblInventory.setItems(filteredList);
    }

    private void setupFilters() {
        // Status filter
        cbStatusFilter.setItems(FXCollections.observableArrayList(
                "Tất cả", "Hết hàng", "Sắp hết", "Ít hàng", "Đủ hàng"
        ));
        cbStatusFilter.setValue("Tất cả");

        // Brand filter - will be populated after loading data
        cbBrandFilter.setItems(FXCollections.observableArrayList("Tất cả"));
        cbBrandFilter.setValue("Tất cả");

        // Event handlers for filtering
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        cbStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        cbBrandFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void loadInventoryData() {
        try {
            // Lấy dữ liệu từ DAO mới
            List<InventorySummary> summaries = InventorySummaryDAO.getAllInventorySummary();
            inventoryList.clear();

            for (InventorySummary summary : summaries) {
                InventoryItem item = new InventoryItem();
                item.setProductID(summary.productID);
                item.setProductCode(summary.productCode);
                item.setProductName(summary.productName);
                item.setBrand(summary.brand);
                item.setPrice(summary.price);
                item.setTotalReceived(summary.totalReceived);
                item.setTotalLoss(summary.totalLoss);
                item.setCurrentStock(summary.currentStock);
                item.setStatus(summary.status);
                item.setValue(summary.value);
                inventoryList.add(item);
            }

            // Update brand filter options
            updateBrandFilter();

            // Apply current filters
            applyFilters();

        } catch (Exception e) {
            showAlert("Lỗi khi tải dữ liệu tồn kho: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateBrandFilter() {
        // Lấy danh sách thương hiệu từ DAO
        List<String> brands = InventorySummaryDAO.getAllBrands();
        cbBrandFilter.setItems(FXCollections.observableArrayList(brands));
        cbBrandFilter.setValue("Tất cả");
    }

    private void applyFilters() {
        ObservableList<InventoryItem> filtered = FXCollections.observableArrayList();

        for (InventoryItem item : inventoryList) {
            boolean matchesSearch = txtSearch.getText().isEmpty() ||
                    item.getProductName().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    item.getProductCode().toLowerCase().contains(txtSearch.getText().toLowerCase());

            boolean matchesStatus = cbStatusFilter.getValue().equals("Tất cả") ||
                    item.getStatus().equals(cbStatusFilter.getValue());

            boolean matchesBrand = cbBrandFilter.getValue().equals("Tất cả") ||
                    item.getBrand().equals(cbBrandFilter.getValue());

            if (matchesSearch && matchesStatus && matchesBrand) {
                filtered.add(item);
            }
        }

        filteredList.setAll(filtered);
        updateStatistics();
        updateCharts();
    }

    private void setupCharts() {
        // Initial chart setup - will be updated in updateCharts()
        pieStockStatus.setTitle("Phân bố trạng thái tồn kho");
        barTopProducts.setTitle("Top sản phẩm tồn kho cao nhất");
    }

    private void updateCharts() {
        updatePieChart();
        updateBarChart();
    }

    private void updatePieChart() {
        Map<String, Integer> statusCount = new HashMap<>();
        for (InventoryItem item : filteredList) {
            statusCount.merge(item.getStatus(), 1, Integer::sum);
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        statusCount.forEach((status, count) ->
                pieData.add(new PieChart.Data(status + " (" + count + ")", count))
        );

        pieStockStatus.setData(pieData);
    }

    private void updateBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng tồn kho");

        filteredList.stream()
                .sorted((a, b) -> Integer.compare(b.getCurrentStock(), a.getCurrentStock()))
                .limit(10)
                .forEach(item ->
                        series.getData().add(new XYChart.Data<>(
                                item.getProductName().length() > 15 ?
                                        item.getProductName().substring(0, 15) + "..." :
                                        item.getProductName(),
                                item.getCurrentStock()))
                );

        barTopProducts.getData().clear();
        barTopProducts.getData().add(series);
    }

    private void updateStatistics() {
        int totalProducts = filteredList.size();
        int lowStockItems = (int) filteredList.stream()
                .filter(item -> item.getStatus().equals("Ít hàng") || item.getStatus().equals("Sắp hết"))
                .count();
        int outOfStockItems = (int) filteredList.stream()
                .filter(item -> item.getStatus().equals("Hết hàng"))
                .count();
        double totalValue = filteredList.stream()
                .mapToDouble(InventoryItem::getValue)
                .sum();

        lblTotalProducts.setText(String.valueOf(totalProducts));
        lblLowStockItems.setText(String.valueOf(lowStockItems));
        lblOutOfStockItems.setText(String.valueOf(outOfStockItems));
        lblTotalValue.setText(String.format("%,.0f VND", totalValue));
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        cbStatusFilter.setValue("Tất cả");
        cbBrandFilter.setValue("Tất cả");
        applyFilters();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for inventory items
    public static class InventoryItem {
        private int productID;
        private String productCode;
        private String productName;
        private String brand;
        private double price;
        private int totalReceived;
        private int totalLoss;
        private int currentStock;
        private String status;
        private double value;

        // Constructors
        public InventoryItem() {}

        // Getters and Setters
        public int getProductID() { return productID; }
        public void setProductID(int productID) { this.productID = productID; }

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getTotalReceived() { return totalReceived; }
        public void setTotalReceived(int totalReceived) { this.totalReceived = totalReceived; }

        public int getTotalLoss() { return totalLoss; }
        public void setTotalLoss(int totalLoss) { this.totalLoss = totalLoss; }

        public int getCurrentStock() { return currentStock; }
        public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}