package controller.warehousestaff;

import dao.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.*;

import java.time.LocalDate;
import java.util.List;

public class LossManagementController {

    @FXML private TableView<ProductWithStock> tblProducts;
    @FXML private TableColumn<ProductWithStock, String> colProductCode;
    @FXML private TableColumn<ProductWithStock, String> colProductName;
    @FXML private TableColumn<ProductWithStock, String> colBrand;
    @FXML private TableColumn<ProductWithStock, Integer> colTotalStock;
    @FXML private TableColumn<ProductWithStock, Integer> colTotalLoss;
    @FXML private TableColumn<ProductWithStock, Integer> colAvailable;

    @FXML private TableView<LossReportDetailDAO.LossReportDetailExtended> tblLossReports;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Integer> colReportID;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colLossProduct;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Integer> colLossQuantity;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colLossReason;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colEmployee;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Double> colLossValue;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Double> colAvgUnitCost;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colReportDate;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Void> colActions;

    @FXML private TextField txtLossQuantity;
    @FXML private TextArea txtLossReason;
    @FXML private DatePicker dpLossDate;
    @FXML private Button btnAddLossReport;
    @FXML private Button btnRefreshProducts;
    @FXML private Button btnRefreshReports;

    @FXML private TextField txtSearchProduct;
    @FXML private TextField txtSearchLossReport;

    @FXML private Label lblSelectedProduct;
    @FXML private Label lblAvailableQuantity;
    @FXML private Label lblTotalLossReports;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblProductsWithLoss;
    @FXML private Label lblAvgUnitCost;

    private ObservableList<ProductWithStock> productList = FXCollections.observableArrayList();
    private ObservableList<ProductWithStock> filteredProductList = FXCollections.observableArrayList();
    private ObservableList<LossReportDetailDAO.LossReportDetailExtended> lossReportList = FXCollections.observableArrayList();
    private ObservableList<LossReportDetailDAO.LossReportDetailExtended> filteredLossReportList = FXCollections.observableArrayList();
    private ProductWithStock selectedProduct;

    @FXML
    public void initialize() {
        setupTables();
        setupSearchBars();
        loadData();
        dpLossDate.setValue(LocalDate.now());
        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onProductSelected(newVal));
    }

    private void setupTables() {
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colTotalStock.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        colTotalLoss.setCellValueFactory(new PropertyValueFactory<>("totalLoss"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        colReportID.setCellValueFactory(new PropertyValueFactory<>("reportID"));
        colLossProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colLossQuantity.setCellValueFactory(new PropertyValueFactory<>("lostQuantity"));
        colLossReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colLossValue.setCellValueFactory(new PropertyValueFactory<>("lossValue"));

        if (colReportDate != null) {
            colReportDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReportDateFormatted()));
        }

        if (colAvgUnitCost != null) {
            colAvgUnitCost.setCellValueFactory(new PropertyValueFactory<>("avgUnitCost"));
            colAvgUnitCost.setCellFactory(col -> new TableCell<LossReportDetailDAO.LossReportDetailExtended, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.format("%,.0f VND", item));
                }
            });
        }

        colLossValue.setCellFactory(col -> new TableCell<LossReportDetailDAO.LossReportDetailExtended, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VND", item));
            }
        });

        if (colActions != null) {
            colActions.setCellFactory(param -> new TableCell<LossReportDetailDAO.LossReportDetailExtended, Void>() {
                private final Button deleteBtn = new Button("Delete");

                {
                    deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6");
                    deleteBtn.setTooltip(new Tooltip("Delete this loss report"));
                    deleteBtn.setOnAction(event -> {
                        LossReportDetailDAO.LossReportDetailExtended item = getTableView().getItems().get(getIndex());
                        handleDeleteSpecificLossReport(item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteBtn);
                }
            });
        }

        tblProducts.setItems(filteredProductList);
        tblLossReports.setItems(filteredLossReportList);
    }

    private void setupSearchBars() {
        if (txtSearchProduct != null) {
            txtSearchProduct.textProperty().addListener((observable, oldValue, newValue) -> filterProducts(newValue));
        }

        if (txtSearchLossReport != null) {
            txtSearchLossReport.textProperty().addListener((observable, oldValue, newValue) -> filterLossReports(newValue));
        }
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
                pws.setTotalStock(getTotalStockForProduct(product.getProductID()));
                pws.setTotalLoss(LossReportDetailDAO.getTotalLossQuantityByProduct(product.getProductID()));
                pws.setAvailableQuantity(Math.max(0, pws.getTotalStock() - pws.getTotalLoss()));
                pws.setAvgUnitCost(getAverageUnitCostForProduct(product.getProductID()));
                productList.add(pws);
            }

            filteredProductList.setAll(productList);
            if (txtSearchProduct != null && !txtSearchProduct.getText().trim().isEmpty()) {
                filterProducts(txtSearchProduct.getText().trim());
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
        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalStock");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

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
        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (var rs = ps.executeQuery()) {
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
            List<LossReportDetailDAO.LossReportDetailExtended> reports = LossReportDetailDAO.getLossReportDetailsExtended();
            lossReportList.setAll(reports);
            filteredLossReportList.setAll(lossReportList);
            if (txtSearchLossReport != null && !txtSearchLossReport.getText().trim().isEmpty()) {
                filterLossReports(txtSearchLossReport.getText().trim());
            }
        } catch (Exception e) {
            showAlert("Error loading loss reports: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatistics() {
        lblTotalProducts.setText(String.valueOf(filteredProductList.size()));
        long productsWithLoss = filteredProductList.stream()
                .filter(p -> p.getTotalLoss() > 0)
                .count();
        lblProductsWithLoss.setText(String.valueOf(productsWithLoss));
    }

    private void onProductSelected(ProductWithStock product) {
        selectedProduct = product;
        if (product != null) {
            lblSelectedProduct.setText("Product: " + product.getProductName());
            lblAvailableQuantity.setText("Available Quantity: " + product.getAvailableQuantity());
            if (lblAvgUnitCost != null) {
                lblAvgUnitCost.setText("Avg Unit Cost: " + String.format("%,.0f VND", product.getAvgUnitCost()));
            }
            btnAddLossReport.setDisable(product.getAvailableQuantity() <= 0);
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
        btnAddLossReport.setDisable(true);
        txtLossQuantity.clear();
        txtLossReason.clear();
    }

    @FXML
    private void handleAddLossReport() {
        if (!validateLossReportInput()) {
            return;
        }

        try {
            int lossQuantity = Integer.parseInt(txtLossQuantity.getText().trim());
            String reason = txtLossReason.getText().trim();
            LocalDate lossDate = dpLossDate.getValue();

            LossReport report = new LossReport();
            report.setEmployeeID(getCurrentEmployeeID());
            report.setReportDate(java.sql.Timestamp.valueOf(lossDate.atStartOfDay()));

            int reportID = LossReportDAO.insertLossReport(report);
            if (reportID > 0) {
                LossReportDetail detail = new LossReportDetail();
                detail.setReportID(reportID);
                detail.setProductID(selectedProduct.getProductID());
                detail.setLostQuantity(lossQuantity);
                detail.setReason(reason);

                boolean success = LossReportDetailDAO.insertLossReportDetail(detail);
                if (success) {
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
                            lossValue);
                    showAlert(message, Alert.AlertType.INFORMATION);
                    loadData();
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
        return 1;
    }

    @FXML
    private void handleRefreshProducts() {
        loadProductsWithStock();
        resetProductSelection();
        updateStatistics();
        showAlert("Product list refreshed!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRefreshReports() {
        loadLossReports();
        updateStatistics();
        showAlert("Loss reports refreshed!", Alert.AlertType.INFORMATION);
    }

    private void handleDeleteSpecificLossReport(LossReportDetailDAO.LossReportDetailExtended selected) {
        if (selected == null) {
            showAlert("No loss report selected!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this loss report?");
        confirmAlert.setContentText(String.format(
                "Product: %s\nQuantity: %d\nAvg Unit Cost: %,.0f VND\nLoss Value: %,.0f VND\nReport Date: %s",
                selected.getProductName(),
                selected.getLostQuantity(),
                selected.getAvgUnitCost(),
                selected.getLossValue(),
                selected.getReportDateFormatted()));

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = LossReportDetailDAO.deleteLossReportDetail(selected.getReportID(), selected.getProductID());
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

    private void filterProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredProductList.setAll(productList);
        } else {
            String lowerCaseKeyword = keyword.toLowerCase().trim();
            List<ProductWithStock> filtered = productList.stream()
                    .filter(product -> product.getProductName().toLowerCase().contains(lowerCaseKeyword) ||
                            product.getProductCode().toLowerCase().contains(lowerCaseKeyword) ||
                            product.getBrand().toLowerCase().contains(lowerCaseKeyword))
                    .toList();
            filteredProductList.setAll(filtered);
        }
        updateStatistics();
    }

    private void filterLossReports(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredLossReportList.setAll(lossReportList);
        } else {
            String lowerCaseKeyword = keyword.toLowerCase().trim();
            List<LossReportDetailDAO.LossReportDetailExtended> filtered = lossReportList.stream()
                    .filter(report -> report.getProductName().toLowerCase().contains(lowerCaseKeyword) ||
                            report.getReason().toLowerCase().contains(lowerCaseKeyword) ||
                            String.valueOf(report.getReportID()).contains(keyword))
                    .toList();
            filteredLossReportList.setAll(filtered);
        }
        updateStatistics();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ProductWithStock {
        private int productID;
        private String productCode;
        private String productName;
        private String brand;
        private double price;
        private int totalStock;
        private int totalLoss;
        private int availableQuantity;
        private double avgUnitCost;

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

        public int getTotalStock() {
            return totalStock;
        }

        public void setTotalStock(int totalStock) {
            this.totalStock = totalStock;
        }

        public int getTotalLoss() {
            return totalLoss;
        }

        public void setTotalLoss(int totalLoss) {
            this.totalLoss = totalLoss;
        }

        public int getAvailableQuantity() {
            return availableQuantity;
        }

        public void setAvailableQuantity(int availableQuantity) {
            this.availableQuantity = availableQuantity;
        }

        public double getAvgUnitCost() {
            return avgUnitCost;
        }

        public void setAvgUnitCost(double avgUnitCost) {
            this.avgUnitCost = avgUnitCost;
        }
    }
}