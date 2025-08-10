package controller.warehousestaff;

import dao.InventorySummaryDAO;
import dao.InventorySummaryDAO.InventorySummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagementController {

    @FXML private TableView<InventoryItem> tblInventory;
    @FXML private TableColumn<InventoryItem, String> colProductCode;
    @FXML private TableColumn<InventoryItem, String> colProductName;
    @FXML private TableColumn<InventoryItem, String> colBrand;
    @FXML private TableColumn<InventoryItem, Integer> colTotalReceived;
    @FXML private TableColumn<InventoryItem, Integer> colTotalLoss;
    @FXML private TableColumn<InventoryItem, Integer> colCurrentStock;
    @FXML private TableColumn<InventoryItem, String> colStatus;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbStatusFilter;

    @FXML private PieChart pieStockStatus;
    @FXML private BarChart<String, Number> barTopProducts;

    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStockItems;
    @FXML private Label lblOutOfStockItems;
    @FXML private Label lblTotalValue;

    private ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();
    private ObservableList<InventoryItem> filteredList = FXCollections.observableArrayList();

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
                        case "Out of Stock" -> setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                        case "Critical" -> setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00;");
                        case "In Stock" -> setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32;");
                        default -> setStyle("");
                    }
                }
            }
        });

        tblInventory.setItems(filteredList);
    }

    private void setupFilters() {
        cbStatusFilter.setItems(FXCollections.observableArrayList("All", "Out of Stock", "Critical", "In Stock"));
        cbStatusFilter.setValue("All");

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        cbStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void loadInventoryData() {
        try {
            inventoryList.clear();
            List<InventorySummary> summaries = InventorySummaryDAO.getAllInventorySummary();

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
                item.setStatus(translateStatusToEnglish(summary.status));
                item.setValue(summary.value);
                inventoryList.add(item);
            }

            applyFilters();
        } catch (Exception e) {
            showAlert("Error loading inventory data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String translateStatusToEnglish(String vietnameseStatus) {
        return switch (vietnameseStatus) {
            case "Out of Stock" -> "Out of Stock";
            case "Critical" -> "Critical";
            case "In Stock" -> "In Stock";
            default -> vietnameseStatus;
        };
    }

    private void applyFilters() {
        ObservableList<InventoryItem> filtered = FXCollections.observableArrayList();

        for (InventoryItem item : inventoryList) {
            boolean matchesSearch = txtSearch.getText().isEmpty() ||
                    item.getProductName().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    item.getProductCode().toLowerCase().contains(txtSearch.getText().toLowerCase());

            boolean matchesStatus = cbStatusFilter.getValue().equals("All") ||
                    item.getStatus().equals(cbStatusFilter.getValue());

            if (matchesSearch && matchesStatus) {
                filtered.add(item);
            }
        }

        filteredList.setAll(filtered);
        updateStatistics();
        updateCharts();
    }

    private void setupCharts() {
        pieStockStatus.setTitle("Stock Status Distribution");
        barTopProducts.setTitle("Inventory Quantity");
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
        statusCount.forEach((status, count) -> pieData.add(new PieChart.Data(status + ": " + count + " items", count)));
        pieStockStatus.setData(pieData);
    }

    private void updateBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stock Quantity");

        filteredList.stream()
                .sorted((a, b) -> Integer.compare(b.getCurrentStock(), a.getCurrentStock()))
                .limit(10)
                .forEach(item -> series.getData().add(new XYChart.Data<>(
                        item.getProductName().length() > 12 ? item.getProductName().substring(0, 12) + "..." : item.getProductName(),
                        item.getCurrentStock())));

        barTopProducts.getData().clear();
        barTopProducts.getData().add(series);

        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #3498db;");
        }
    }

    private void updateStatistics() {
        int totalProducts = filteredList.size();
        int outOfStockItems = (int) filteredList.stream()
                .filter(item -> item.getStatus().equals("Out of Stock"))
                .count();
        double totalValue = filteredList.stream()
                .mapToDouble(InventoryItem::getValue)
                .sum();

        lblTotalProducts.setText(String.valueOf(totalProducts));
        lblOutOfStockItems.setText(String.valueOf(outOfStockItems));
        lblTotalValue.setText(String.format("%,.0f VND", totalValue));
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cbStatusFilter.setValue("All");
        loadInventoryData();
    }

    @FXML
    private void handleExportToTXT() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Inventory Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("Inventory_Report_" + timestamp + ".txt");

            Stage stage = (Stage) tblInventory.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                exportInventoryToTXT(file);
                showAlert("Export completed successfully!\nFile saved to: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Error during export: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void exportInventoryToTXT(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write("=".repeat(80) + "\n");
            writer.write("                    INVENTORY MANAGEMENT REPORT\n");
            writer.write("=".repeat(80) + "\n");
            writer.write("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Total Records: " + filteredList.size() + "\n");

            if (!txtSearch.getText().isEmpty() || !cbStatusFilter.getValue().equals("All")) {
                writer.write("\nApplied Filters:\n");
                if (!txtSearch.getText().isEmpty()) {
                    writer.write("- Search: " + txtSearch.getText() + "\n");
                }
                if (!cbStatusFilter.getValue().equals("All")) {
                    writer.write("- Status: " + cbStatusFilter.getValue() + "\n");
                }
            }

            writer.write("\n" + "=".repeat(80) + "\n");
            writer.write("                           SUMMARY STATISTICS\n");
            writer.write("=".repeat(80) + "\n");

            int totalProducts = filteredList.size();
            int outOfStockItems = (int) filteredList.stream()
                    .filter(item -> item.getStatus().equals("Out of Stock"))
                    .count();
            int criticalItems = (int) filteredList.stream()
                    .filter(item -> item.getStatus().equals("Critical"))
                    .count();
            int inStockItems = (int) filteredList.stream()
                    .filter(item -> item.getStatus().equals("In Stock"))
                    .count();
            double totalValue = filteredList.stream()
                    .mapToDouble(InventoryItem::getValue)
                    .sum();
            int totalStock = filteredList.stream()
                    .mapToInt(InventoryItem::getCurrentStock)
                    .sum();

            writer.write(String.format("Total Products:      %6d\n", totalProducts));
            writer.write(String.format("In Stock Items:      %6d\n", inStockItems));
            writer.write(String.format("Critical Items:      %6d\n", criticalItems));
            writer.write(String.format("Out of Stock Items:  %6d\n", outOfStockItems));
            writer.write(String.format("Total Stock Qty:     %6d\n", totalStock));
            writer.write(String.format("Total Stock Value:   %,.0f VND\n", totalValue));

            writer.write("\n" + "=".repeat(80) + "\n");
            writer.write("                         DETAILED INVENTORY LIST\n");
            writer.write("=".repeat(80) + "\n");
            writer.write(String.format("%-8s %-25s %-12s %8s %6s %8s %-12s\n",
                    "Code", "Product Name", "Brand", "Received", "Loss", "Stock", "Status"));
            writer.write("-".repeat(80) + "\n");

            for (InventoryItem item : filteredList) {
                String productName = item.getProductName().length() > 25 ?
                        item.getProductName().substring(0, 22) + "..." : item.getProductName();
                String brand = item.getBrand().length() > 12 ? item.getBrand().substring(0, 9) + "..." : item.getBrand();

                writer.write(String.format("%-8s %-25s %-12s %8d %6d %8d %-12s\n",
                        item.getProductCode(), productName, brand, item.getTotalReceived(),
                        item.getTotalLoss(), item.getCurrentStock(), item.getStatus()));
            }

            if (!filteredList.isEmpty()) {
                List<InventoryItem> outOfStockList = filteredList.stream()
                        .filter(item -> item.getStatus().equals("Out of Stock"))
                        .toList();
                if (!outOfStockList.isEmpty()) {
                    writer.write("\n" + "=".repeat(80) + "\n");
                    writer.write("                        OUT OF Stock ITEMS (" + outOfStockList.size() + ")\n");
                    writer.write("=".repeat(80) + "\n");
                    for (InventoryItem item : outOfStockList) {
                        writer.write("• " + item.getProductCode() + " - " + item.getProductName() + "\n");
                    }
                }

                List<InventoryItem> criticalList = filteredList.stream()
                        .filter(item -> item.getStatus().equals("Critical"))
                        .toList();
                if (!criticalList.isEmpty()) {
                    writer.write("\n" + "=".repeat(80) + "\n");
                    writer.write("                         CRITICAL ITEMS (" + criticalList.size() + ")\n");
                    writer.write("=".repeat(80) + "\n");
                    for (InventoryItem item : criticalList) {
                        writer.write("• " + item.getProductCode() + " - " + item.getProductName() +
                                " (Stock: " + item.getCurrentStock() + ")\n");
                    }
                }
            }

            writer.write("\n" + "=".repeat(80) + "\n");
            writer.write("                          END OF REPORT\n");
            writer.write("=".repeat(80) + "\n");
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getTotalReceived() {
            return totalReceived;
        }

        public void setTotalReceived(int totalReceived) {
            this.totalReceived = totalReceived;
        }

        public int getTotalLoss() {
            return totalLoss;
        }

        public void setTotalLoss(int totalLoss) {
            this.totalLoss = totalLoss;
        }

        public int getCurrentStock() {
            return currentStock;
        }

        public void setCurrentStock(int currentStock) {
            this.currentStock = currentStock;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}