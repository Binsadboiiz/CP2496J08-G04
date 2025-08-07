package controller.warehousestaff;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.time.LocalDate;
import java.util.List;

public class LossManagementController {

    // UI Components for product list that can create loss reports
    @FXML private TableView<ProductWithStock> tblProducts;
    @FXML private TableColumn<ProductWithStock, String> colProductCode;
    @FXML private TableColumn<ProductWithStock, String> colProductName;
    @FXML private TableColumn<ProductWithStock, String> colBrand;
    @FXML private TableColumn<ProductWithStock, Integer> colTotalStock;
    @FXML private TableColumn<ProductWithStock, Integer> colTotalLoss;
    @FXML private TableColumn<ProductWithStock, Integer> colAvailable;

    // UI Components for created loss reports list
    @FXML private TableView<LossReportDetailDAO.LossReportDetailExtended> tblLossReports;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Integer> colReportID;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colLossProduct;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Integer> colLossQuantity;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colLossReason;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colEmployee;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Double> colLossValue;
    // THÊM: Cột hiển thị giá nhập trung bình
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Double> colAvgUnitCost;

    // UI Components for creating loss report form
    @FXML private TextField txtLossQuantity;
    @FXML private TextArea txtLossReason;
    @FXML private DatePicker dpLossDate;
    @FXML private Button btnCreateLossReport;
    @FXML private Button btnRefresh;

    // UI Components for displaying information
    @FXML private Label lblSelectedProduct;
    @FXML private Label lblAvailableQuantity;
    @FXML private Label lblTotalLossReports;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblProductsWithLoss;
    // THÊM: Hiển thị giá nhập trung bình của sản phẩm được chọn
    @FXML private Label lblAvgUnitCost;

    // Data
    private ObservableList<ProductWithStock> productList = FXCollections.observableArrayList();
    private ObservableList<LossReportDetailDAO.LossReportDetailExtended> lossReportList = FXCollections.observableArrayList();
    private ProductWithStock selectedProduct;

    @FXML
    public void initialize() {
        setupTables();
        loadData();
        dpLossDate.setValue(LocalDate.now());

        // Event handlers
        tblProducts.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onProductSelected(newVal)
        );
    }

    private void setupTables() {
        // Setup Products table
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colTotalStock.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        colTotalLoss.setCellValueFactory(new PropertyValueFactory<>("totalLoss"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        // Setup Loss Reports table
        colReportID.setCellValueFactory(new PropertyValueFactory<>("reportID"));
        colLossProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colLossQuantity.setCellValueFactory(new PropertyValueFactory<>("lostQuantity"));
        colLossReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colLossValue.setCellValueFactory(new PropertyValueFactory<>("lossValue"));

        // THÊM: Setup cột giá nhập trung bình
        if (colAvgUnitCost != null) {
            colAvgUnitCost.setCellValueFactory(new PropertyValueFactory<>("avgUnitCost"));
            // Format avg unit cost column to show currency
            colAvgUnitCost.setCellFactory(col -> new TableCell<LossReportDetailDAO.LossReportDetailExtended, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%,.0f VND", item));
                    }
                }
            });
        }

        // Format loss value column to show currency
        colLossValue.setCellFactory(col -> new TableCell<LossReportDetailDAO.LossReportDetailExtended, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VND", item));
                }
            }
        });

        tblProducts.setItems(productList);
        tblLossReports.setItems(lossReportList);
    }

    private void loadData() {
        loadProductsWithStock();
        loadLossReports();
        updateStatistics();
    }

    private void loadProductsWithStock() {
        try {
            productList.clear();
            List<Product> products = ProductDAO.getAll();

            for (Product product : products) {
                ProductWithStock pws = new ProductWithStock();
                pws.setProductID(product.getProductID());
                pws.setProductCode(product.getProductCode());
                pws.setProductName(product.getProductName());
                pws.setBrand(product.getBrand());
                pws.setPrice(product.getPrice());

                // Calculate total stock from all stock entries
                int totalStock = getTotalStockForProduct(product.getProductID());
                pws.setTotalStock(totalStock);

                // Calculate total loss quantity
                int totalLoss = LossReportDetailDAO.getTotalLossQuantityByProduct(product.getProductID());
                pws.setTotalLoss(totalLoss);

                // Calculate available quantity
                int available = Math.max(0, totalStock - totalLoss);
                pws.setAvailableQuantity(available);

                // THÊM: Tính giá nhập trung bình
                double avgUnitCost = getAverageUnitCostForProduct(product.getProductID());
                pws.setAvgUnitCost(avgUnitCost);

                productList.add(pws);
            }
        } catch (Exception e) {
            showAlert("Error loading product list: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getTotalStockForProduct(int productID) {
        String sql = """
            SELECT ISNULL(SUM(sed.Quantity), 0) as TotalStock
            FROM StockEntryDetail sed
            WHERE sed.ProductID = ?
        """;

        try (java.sql.Connection conn = dao.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalStock");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // THÊM: Phương thức tính giá nhập trung bình
    private double getAverageUnitCostForProduct(int productID) {
        String sql = """
            SELECT CASE 
                       WHEN SUM(sed.Quantity) > 0 
                       THEN SUM(sed.Quantity * sed.UnitCost) / SUM(sed.Quantity)
                       ELSE 0 
                   END as AvgUnitCost
            FROM StockEntryDetail sed
            WHERE sed.ProductID = ?
        """;

        try (java.sql.Connection conn = dao.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("AvgUnitCost");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void loadLossReports() {
        try {
            List<LossReportDetailDAO.LossReportDetailExtended> reports =
                    LossReportDetailDAO.getLossReportDetailsExtended();
            lossReportList.setAll(reports);
        } catch (Exception e) {
            showAlert("Error loading loss reports: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatistics() {
        lblTotalLossReports.setText("Total Reports: " + lossReportList.size());
        lblTotalProducts.setText(String.valueOf(productList.size()));

        long productsWithLoss = productList.stream()
                .filter(p -> p.getTotalLoss() > 0)
                .count();
        lblProductsWithLoss.setText(String.valueOf(productsWithLoss));
    }

    private void onProductSelected(ProductWithStock product) {
        selectedProduct = product;
        if (product != null) {
            lblSelectedProduct.setText("Product: " + product.getProductName());
            lblAvailableQuantity.setText("Available Quantity: " + product.getAvailableQuantity());

            // THÊM: Hiển thị giá nhập trung bình
            if (lblAvgUnitCost != null) {
                lblAvgUnitCost.setText("Avg Unit Cost: " + String.format("%,.0f VND", product.getAvgUnitCost()));
            }

            // Enable/disable create report based on available quantity
            btnCreateLossReport.setDisable(product.getAvailableQuantity() <= 0);
        } else {
            resetProductSelection();
        }
    }

    private void resetProductSelection() {
        selectedProduct = null;
        lblSelectedProduct.setText("No product selected");
        lblAvailableQuantity.setText("Available Quantity: 0");
        if (lblAvgUnitCost != null) {
            lblAvgUnitCost.setText("Avg Unit Cost: 0 VND");
        }
        btnCreateLossReport.setDisable(true);
        txtLossQuantity.clear();
        txtLossReason.clear();
    }

    @FXML
    private void handleCreateLossReport() {
        if (!validateLossReportInput()) {
            return;
        }

        try {
            int lossQuantity = Integer.parseInt(txtLossQuantity.getText().trim());
            String reason = txtLossReason.getText().trim();
            LocalDate lossDate = dpLossDate.getValue();

            // Create loss report
            LossReport report = new LossReport();
            report.setEmployeeID(getCurrentEmployeeID());
            report.setReportDate(java.sql.Timestamp.valueOf(lossDate.atStartOfDay()));

            int reportID = LossReportDAO.insertLossReport(report);
            if (reportID > 0) {
                // Create loss report detail
                LossReportDetail detail = new LossReportDetail();
                detail.setReportID(reportID);
                detail.setProductID(selectedProduct.getProductID());
                detail.setLostQuantity(lossQuantity);
                detail.setReason(reason);

                boolean success = LossReportDetailDAO.insertLossReportDetail(detail);
                if (success) {
                    // THÊM: Hiển thị thông tin giá trị tổn thất dựa trên giá nhập
                    double lossValue = lossQuantity * selectedProduct.getAvgUnitCost();
                    String message = String.format(
                            "Loss report created successfully!\n" +
                                    "Product: %s\n" +
                                    "Quantity Lost: %d\n" +
                                    "Avg Unit Cost: %,.0f VND\n" +
                                    "Total Loss Value: %,.0f VND",
                            selectedProduct.getProductName(),
                            lossQuantity,
                            selectedProduct.getAvgUnitCost(),
                            lossValue
                    );
                    showAlert(message, Alert.AlertType.INFORMATION);

                    // Refresh data
                    loadData();

                    // Clear form
                    txtLossQuantity.clear();
                    txtLossReason.clear();
                    dpLossDate.setValue(LocalDate.now());
                    resetProductSelection();
                } else {
                    showAlert("Error creating loss report detail!", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error creating loss report!", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("Error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateLossReportInput() {
        if (selectedProduct == null) {
            showAlert("Please select a product!", Alert.AlertType.WARNING);
            return false;
        }

        String quantityText = txtLossQuantity.getText().trim();
        if (quantityText.isEmpty()) {
            showAlert("Please enter loss quantity!", Alert.AlertType.WARNING);
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showAlert("Loss quantity must be greater than 0!", Alert.AlertType.WARNING);
                return false;
            }

            if (quantity > selectedProduct.getAvailableQuantity()) {
                showAlert("Loss quantity cannot exceed available quantity (" +
                        selectedProduct.getAvailableQuantity() + ")!", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Loss quantity must be an integer!", Alert.AlertType.WARNING);
            return false;
        }

        if (txtLossReason.getText().trim().isEmpty()) {
            showAlert("Please enter loss reason!", Alert.AlertType.WARNING);
            return false;
        }

        if (dpLossDate.getValue() == null) {
            showAlert("Please select loss date!", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private int getCurrentEmployeeID() {
        // Get from current session/login
        return 1; // Placeholder - replace with actual session management
    }

    @FXML
    private void handleRefresh() {
        loadData();
        resetProductSelection();
        showAlert("Data refreshed!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDeleteLossReport() {
        LossReportDetailDAO.LossReportDetailExtended selected = tblLossReports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a loss report to delete!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this loss report?");
        // THÊM: Hiển thị thông tin giá trị tổn thất dựa trên giá nhập
        confirmAlert.setContentText(String.format(
                "Product: %s\nQuantity: %d\nAvg Unit Cost: %,.0f VND\nLoss Value: %,.0f VND",
                selected.getProductName(),
                selected.getLostQuantity(),
                selected.getAvgUnitCost(),
                selected.getLossValue()
        ));

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = LossReportDetailDAO.deleteLossReportDetail(
                        selected.getReportID(), selected.getProductID());

                if (success) {
                    showAlert("Loss report deleted successfully!", Alert.AlertType.INFORMATION);
                    loadData();
                } else {
                    showAlert("Error deleting loss report!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSearchProduct() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Product");
        dialog.setHeaderText("Enter search keyword:");
        dialog.setContentText("Product name or product code:");

        dialog.showAndWait().ifPresent(keyword -> {
            if (!keyword.trim().isEmpty()) {
                filterProducts(keyword.trim());
            } else {
                loadProductsWithStock();
            }
        });
    }

    private void filterProducts(String keyword) {
        try {
            productList.clear();
            List<Product> products = ProductDAO.search(keyword);

            for (Product product : products) {
                ProductWithStock pws = new ProductWithStock();
                pws.setProductID(product.getProductID());
                pws.setProductCode(product.getProductCode());
                pws.setProductName(product.getProductName());
                pws.setBrand(product.getBrand());
                pws.setPrice(product.getPrice());

                int totalStock = getTotalStockForProduct(product.getProductID());
                pws.setTotalStock(totalStock);

                int totalLoss = LossReportDetailDAO.getTotalLossQuantityByProduct(product.getProductID());
                pws.setTotalLoss(totalLoss);

                int available = Math.max(0, totalStock - totalLoss);
                pws.setAvailableQuantity(available);

                // THÊM: Tính giá nhập trung bình cho tìm kiếm
                double avgUnitCost = getAverageUnitCostForProduct(product.getProductID());
                pws.setAvgUnitCost(avgUnitCost);

                productList.add(pws);
            }
        } catch (Exception e) {
            showAlert("Search error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for product with stock information
    public static class ProductWithStock {
        private int productID;
        private String productCode;
        private String productName;
        private String brand;
        private double price;
        private int totalStock;
        private int totalLoss;
        private int availableQuantity;
        private double avgUnitCost; // THÊM: Giá nhập trung bình

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

        public int getTotalStock() { return totalStock; }
        public void setTotalStock(int totalStock) { this.totalStock = totalStock; }

        public int getTotalLoss() { return totalLoss; }
        public void setTotalLoss(int totalLoss) { this.totalLoss = totalLoss; }

        public int getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

        // THÊM: Getter và Setter cho giá nhập trung bình
        public double getAvgUnitCost() { return avgUnitCost; }
        public void setAvgUnitCost(double avgUnitCost) { this.avgUnitCost = avgUnitCost; }
    }
}