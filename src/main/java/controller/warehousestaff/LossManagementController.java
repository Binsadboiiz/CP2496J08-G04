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

    // UI Components cho danh sách sản phẩm có thể tạo báo cáo tổn thất
    @FXML private TableView<ProductWithStock> tblProducts;
    @FXML private TableColumn<ProductWithStock, String> colProductCode;
    @FXML private TableColumn<ProductWithStock, String> colProductName;
    @FXML private TableColumn<ProductWithStock, String> colBrand;
    @FXML private TableColumn<ProductWithStock, Integer> colTotalStock;
    @FXML private TableColumn<ProductWithStock, Integer> colTotalLoss;
    @FXML private TableColumn<ProductWithStock, Integer> colAvailable;

    // UI Components cho danh sách báo cáo tổn thất đã tạo
    @FXML private TableView<LossReportDetailDAO.LossReportDetailExtended> tblLossReports;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Integer> colReportID;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colLossProduct;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Integer> colLossQuantity;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colLossReason;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, String> colEmployee;
    @FXML private TableColumn<LossReportDetailDAO.LossReportDetailExtended, Double> colLossValue;

    // UI Components cho form tạo báo cáo tổn thất
    @FXML private TextField txtLossQuantity;
    @FXML private TextArea txtLossReason;
    @FXML private DatePicker dpLossDate;
    @FXML private Button btnCreateLossReport;
    @FXML private Button btnRefresh;

    // UI Components cho hiển thị thông tin
    @FXML private Label lblSelectedProduct;
    @FXML private Label lblAvailableQuantity;
    @FXML private Label lblTotalLossReports;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblProductsWithLoss;

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

        // Format loss value column to show currency
        colLossValue.setCellFactory(col -> new TableCell<LossReportDetailDAO.LossReportDetailExtended, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f ₫", item));
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

                // Tính tổng số lượng nhập từ tất cả phiếu nhập
                int totalStock = getTotalStockForProduct(product.getProductID());
                pws.setTotalStock(totalStock);

                // Tính tổng số lượng tổn thất
                int totalLoss = LossReportDetailDAO.getTotalLossQuantityByProduct(product.getProductID());
                pws.setTotalLoss(totalLoss);

                // Tính số lượng khả dụng
                int available = Math.max(0, totalStock - totalLoss);
                pws.setAvailableQuantity(available);

                productList.add(pws);
            }
        } catch (Exception e) {
            showAlert("Lỗi khi tải danh sách sản phẩm: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void loadLossReports() {
        try {
            List<LossReportDetailDAO.LossReportDetailExtended> reports =
                    LossReportDetailDAO.getLossReportDetailsExtended();
            lossReportList.setAll(reports);
        } catch (Exception e) {
            showAlert("Lỗi khi tải danh sách báo cáo tổn thất: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatistics() {
        lblTotalLossReports.setText("Tổng số báo cáo: " + lossReportList.size());
        lblTotalProducts.setText(String.valueOf(productList.size()));

        long productsWithLoss = productList.stream()
                .filter(p -> p.getTotalLoss() > 0)
                .count();
        lblProductsWithLoss.setText(String.valueOf(productsWithLoss));
    }

    private void onProductSelected(ProductWithStock product) {
        selectedProduct = product;
        if (product != null) {
            lblSelectedProduct.setText("Sản phẩm: " + product.getProductName());
            lblAvailableQuantity.setText("Số lượng khả dụng: " + product.getAvailableQuantity());

            // Enable/disable tạo báo cáo dựa trên số lượng khả dụng
            btnCreateLossReport.setDisable(product.getAvailableQuantity() <= 0);
        } else {
            resetProductSelection();
        }
    }

    private void resetProductSelection() {
        selectedProduct = null;
        lblSelectedProduct.setText("Chưa chọn sản phẩm");
        lblAvailableQuantity.setText("Số lượng khả dụng: 0");
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

            // Tạo báo cáo tổn thất
            LossReport report = new LossReport();
            report.setEmployeeID(getCurrentEmployeeID());
            report.setReportDate(java.sql.Timestamp.valueOf(lossDate.atStartOfDay()));

            int reportID = LossReportDAO.insertLossReport(report);
            if (reportID > 0) {
                // Tạo chi tiết báo cáo tổn thất
                LossReportDetail detail = new LossReportDetail();
                detail.setReportID(reportID);
                detail.setProductID(selectedProduct.getProductID());
                detail.setLostQuantity(lossQuantity);
                detail.setReason(reason);

                boolean success = LossReportDetailDAO.insertLossReportDetail(detail);
                if (success) {
                    showAlert("Tạo báo cáo tổn thất thành công!", Alert.AlertType.INFORMATION);

                    // Làm mới dữ liệu
                    loadData();

                    // Xóa form
                    txtLossQuantity.clear();
                    txtLossReason.clear();
                    dpLossDate.setValue(LocalDate.now());
                    resetProductSelection();
                } else {
                    showAlert("Lỗi khi tạo chi tiết báo cáo tổn thất!", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Lỗi khi tạo báo cáo tổn thất!", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("Lỗi: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateLossReportInput() {
        if (selectedProduct == null) {
            showAlert("Vui lòng chọn sản phẩm!", Alert.AlertType.WARNING);
            return false;
        }

        String quantityText = txtLossQuantity.getText().trim();
        if (quantityText.isEmpty()) {
            showAlert("Vui lòng nhập số lượng tổn thất!", Alert.AlertType.WARNING);
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showAlert("Số lượng tổn thất phải lớn hơn 0!", Alert.AlertType.WARNING);
                return false;
            }

            if (quantity > selectedProduct.getAvailableQuantity()) {
                showAlert("Số lượng tổn thất không được vượt quá số lượng khả dụng (" +
                        selectedProduct.getAvailableQuantity() + ")!", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Số lượng tổn thất phải là số nguyên!", Alert.AlertType.WARNING);
            return false;
        }

        if (txtLossReason.getText().trim().isEmpty()) {
            showAlert("Vui lòng nhập lý do tổn thất!", Alert.AlertType.WARNING);
            return false;
        }

        if (dpLossDate.getValue() == null) {
            showAlert("Vui lòng chọn ngày tổn thất!", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private int getCurrentEmployeeID() {
        // Lấy từ session/login hiện tại
        return 1; // Placeholder - thay thế bằng session management thực tế
    }

    @FXML
    private void handleRefresh() {
        loadData();
        resetProductSelection();
        showAlert("Đã làm mới dữ liệu!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDeleteLossReport() {
        LossReportDetailDAO.LossReportDetailExtended selected = tblLossReports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn báo cáo tổn thất cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa báo cáo tổn thất này?");
        confirmAlert.setContentText("Sản phẩm: " + selected.getProductName() +
                "\nSố lượng: " + selected.getLostQuantity());

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = LossReportDetailDAO.deleteLossReportDetail(
                        selected.getReportID(), selected.getProductID());

                if (success) {
                    showAlert("Xóa báo cáo tổn thất thành công!", Alert.AlertType.INFORMATION);
                    loadData();
                } else {
                    showAlert("Lỗi khi xóa báo cáo tổn thất!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Lỗi: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSearchProduct() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tìm kiếm sản phẩm");
        dialog.setHeaderText("Nhập từ khóa tìm kiếm:");
        dialog.setContentText("Tên sản phẩm hoặc mã sản phẩm:");

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

                productList.add(pws);
            }
        } catch (Exception e) {
            showAlert("Lỗi khi tìm kiếm: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class cho sản phẩm với thông tin kho
    public static class ProductWithStock {
        private int productID;
        private String productCode;
        private String productName;
        private String brand;
        private double price;
        private int totalStock;
        private int totalLoss;
        private int availableQuantity;

        // Getters và Setters
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
    }
}