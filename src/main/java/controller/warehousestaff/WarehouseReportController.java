package controller.warehousestaff;

import dao.WarehouseReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class WarehouseReportController implements Initializable {

    // ==================== FXML COMPONENTS ====================

    @FXML private TabPane tabPane;

    @FXML private TableView<WarehouseReportDAO.InventoryReport> inventoryTable;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, String> invProductCodeCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, String> invProductNameCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, String> invBrandCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, Double> invPriceCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, Integer> invReceivedCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, Integer> invLossCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, Integer> invCurrentStockCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, String> invStatusCol;
    @FXML private TableColumn<WarehouseReportDAO.InventoryReport, Double> invValueCol;

    @FXML private TextField lowStockThresholdField;
    @FXML private Button refreshInventoryBtn;
    @FXML private Button showLowStockBtn;

    // Entry History Tab
    @FXML private TableView<WarehouseReportDAO.StockEntryReport> stockEntryTable;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, Integer> entryIdCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, Date> entryDateCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, String> supplierCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, String> employeeCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, Integer> totalQtyCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, Double> totalValueCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryReport, Integer> productCountCol;

    @FXML private DatePicker entryFromDate;
    @FXML private DatePicker entryToDate;
    @FXML private Button searchEntriesBtn;

    // Entry Details Tab
    @FXML private TableView<WarehouseReportDAO.StockEntryDetailReport> stockDetailTable;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, String> detailProductCodeCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, String> detailProductNameCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, String> detailBrandCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, Integer> detailReceivedCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, Double> detailAvgCostCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, Double> detailTotalCostCol;
    @FXML private TableColumn<WarehouseReportDAO.StockEntryDetailReport, Integer> detailEntryCountCol;

    @FXML private DatePicker detailFromDate;
    @FXML private DatePicker detailToDate;
    @FXML private Button searchDetailsBtn;

    // Loss Report Tab
    @FXML private TableView<WarehouseReportDAO.LossReport> lossTable;
    @FXML private TableColumn<WarehouseReportDAO.LossReport, Integer> lossReportIdCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReport, Date> lossDateCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReport, String> lossEmployeeCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReport, Integer> lossQuantityCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReport, Double> lossValueCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReport, Integer> lossProductCountCol;

    @FXML private DatePicker lossFromDate;
    @FXML private DatePicker lossToDate;
    @FXML private Button searchLossBtn;

    // Top Loss Products Tab
    @FXML private TableView<WarehouseReportDAO.ProductLossReport> topLossTable;
    @FXML private TableColumn<WarehouseReportDAO.ProductLossReport, String> topLossProductCodeCol;
    @FXML private TableColumn<WarehouseReportDAO.ProductLossReport, String> topLossProductNameCol;
    @FXML private TableColumn<WarehouseReportDAO.ProductLossReport, String> topLossBrandCol;
    @FXML private TableColumn<WarehouseReportDAO.ProductLossReport, Integer> topLossQuantityCol;
    @FXML private TableColumn<WarehouseReportDAO.ProductLossReport, Double> topLossValueCol;
    @FXML private TableColumn<WarehouseReportDAO.ProductLossReport, Double> topLossPercentageCol;

    @FXML private TextField topLossLimitField;
    @FXML private Button showTopLossBtn;

    // Loss Detail Tab
    @FXML private TableView<WarehouseReportDAO.LossDetailReport> lossDetailTable;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, Integer> lossDetailReportIdCol;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, String> lossDetailProductCodeCol;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, String> lossDetailProductNameCol;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, Integer> lossDetailQuantityCol;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, String> lossDetailReasonCol;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, Double> lossDetailValueCol;
    @FXML private TableColumn<WarehouseReportDAO.LossDetailReport, Date> lossDetailDateCol;

    @FXML private DatePicker lossDetailFromDate;
    @FXML private DatePicker lossDetailToDate;
    @FXML private Button searchLossDetailBtn;

    // Loss Reason Report Tab
    @FXML private TableView<WarehouseReportDAO.LossReasonReport> lossReasonTable;
    @FXML private TableColumn<WarehouseReportDAO.LossReasonReport, String> reasonCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReasonReport, Integer> reasonProductCountCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReasonReport, Integer> reasonQuantityCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReasonReport, Double> reasonValueCol;
    @FXML private TableColumn<WarehouseReportDAO.LossReasonReport, Integer> reasonReportCountCol;

    @FXML private DatePicker reasonFromDate;
    @FXML private DatePicker reasonToDate;
    @FXML private Button searchReasonBtn;

    // Loss Value Comparison Tab
    @FXML private TableView<WarehouseReportDAO.LossValueComparisonReport> valueComparisonTable;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, String> compProductCodeCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, String> compProductNameCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, Integer> compQuantityCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, Double> compSellingPriceCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, Double> compUnitCostCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, Double> compLossBySellingCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, Double> compLossByUnitCostCol;
    @FXML private TableColumn<WarehouseReportDAO.LossValueComparisonReport, Double> compValueDifferenceCol;

    @FXML private DatePicker compFromDate;
    @FXML private DatePicker compToDate;
    @FXML private Button searchComparisonBtn;

    // Overview Statistics
    @FXML private VBox summaryBox;
    @FXML private Label totalProductsLabel;
    @FXML private Label totalReceivedLabel;
    @FXML private Label totalLossLabel;
    @FXML private Label currentStockLabel;
    @FXML private Label totalStockValueLabel;
    @FXML private Label outOfStockLabel;
    @FXML private Label lowStockLabel;
    @FXML private Button refreshSummaryBtn;

    // ==================== INITIALIZE ====================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupDatePickers();
        setupDefaultValues();
        loadInitialData();
    }

    private void setupTableColumns() {
        // Inventory Table
        invProductCodeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        invProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        invBrandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        invPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        invReceivedCol.setCellValueFactory(new PropertyValueFactory<>("totalReceived"));
        invLossCol.setCellValueFactory(new PropertyValueFactory<>("totalLoss"));
        invCurrentStockCol.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        invStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        invValueCol.setCellValueFactory(new PropertyValueFactory<>("stockValue"));

        // Stock Entry Table
        entryIdCol.setCellValueFactory(new PropertyValueFactory<>("entryID"));
        entryDateCol.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        employeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        totalQtyCol.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        totalValueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        productCountCol.setCellValueFactory(new PropertyValueFactory<>("productCount"));

        // Stock Detail Table
        detailProductCodeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        detailProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        detailBrandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        detailReceivedCol.setCellValueFactory(new PropertyValueFactory<>("totalReceived"));
        detailAvgCostCol.setCellValueFactory(new PropertyValueFactory<>("avgUnitCost"));
        detailTotalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        detailEntryCountCol.setCellValueFactory(new PropertyValueFactory<>("entryCount"));

        // Loss Table
        lossReportIdCol.setCellValueFactory(new PropertyValueFactory<>("reportID"));
        lossDateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        lossEmployeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        lossQuantityCol.setCellValueFactory(new PropertyValueFactory<>("totalLossQuantity"));
        lossValueCol.setCellValueFactory(new PropertyValueFactory<>("totalLossValue"));
        lossProductCountCol.setCellValueFactory(new PropertyValueFactory<>("productCount"));

        // Top Loss Table
        topLossProductCodeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        topLossProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        topLossBrandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        topLossQuantityCol.setCellValueFactory(new PropertyValueFactory<>("totalLossQuantity"));
        topLossValueCol.setCellValueFactory(new PropertyValueFactory<>("totalLossValue"));
        topLossPercentageCol.setCellValueFactory(new PropertyValueFactory<>("lossPercentage"));

        // Setup Loss Detail Table - THÊM MỚI
        if (lossDetailTable != null) {
            lossDetailReportIdCol.setCellValueFactory(new PropertyValueFactory<>("reportID"));
            lossDetailProductCodeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
            lossDetailProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            lossDetailQuantityCol.setCellValueFactory(new PropertyValueFactory<>("lostQuantity"));
            lossDetailReasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
            lossDetailValueCol.setCellValueFactory(new PropertyValueFactory<>("lossValue"));
            lossDetailDateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        }

        // Setup Loss Reason Table - THÊM MỚI
        if (lossReasonTable != null) {
            reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
            reasonProductCountCol.setCellValueFactory(new PropertyValueFactory<>("productCount"));
            reasonQuantityCol.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
            reasonValueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
            reasonReportCountCol.setCellValueFactory(new PropertyValueFactory<>("reportCount"));
        }

        // Setup Value Comparison Table - THÊM MỚI
        if (valueComparisonTable != null) {
            compProductCodeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
            compProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            compQuantityCol.setCellValueFactory(new PropertyValueFactory<>("totalLossQuantity"));
            compSellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
            compUnitCostCol.setCellValueFactory(new PropertyValueFactory<>("avgUnitCost"));
            compLossBySellingCol.setCellValueFactory(new PropertyValueFactory<>("lossValueBySellingPrice"));
            compLossByUnitCostCol.setCellValueFactory(new PropertyValueFactory<>("lossValueByUnitCost"));
            compValueDifferenceCol.setCellValueFactory(new PropertyValueFactory<>("valueDifference"));
        }

        // Format price columns
        formatPriceColumn(invPriceCol);
        formatPriceColumn(invValueCol);
        formatPriceColumn(totalValueCol);
        formatPriceColumn(detailAvgCostCol);
        formatPriceColumn(detailTotalCostCol);
        formatPriceColumn(lossValueCol);
        formatPriceColumn(topLossValueCol);

        if (lossDetailValueCol != null) formatPriceColumn(lossDetailValueCol);
        if (reasonValueCol != null) formatPriceColumn(reasonValueCol);
        if (compSellingPriceCol != null) formatPriceColumn(compSellingPriceCol);
        if (compUnitCostCol != null) formatPriceColumn(compUnitCostCol);
        if (compLossBySellingCol != null) formatPriceColumn(compLossBySellingCol);
        if (compLossByUnitCostCol != null) formatPriceColumn(compLossByUnitCostCol);
        if (compValueDifferenceCol != null) formatPriceColumn(compValueDifferenceCol);

        // Format percentage column
        formatPercentageColumn(topLossPercentageCol);
    }

    private void setupDatePickers() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);

        // Set default dates for existing date pickers
        entryFromDate.setValue(startOfMonth);
        entryToDate.setValue(now);
        detailFromDate.setValue(startOfMonth);
        detailToDate.setValue(now);
        lossFromDate.setValue(startOfMonth);
        lossToDate.setValue(now);

        // Set default dates for new date pickers - THÊM MỚI
        if (lossDetailFromDate != null) lossDetailFromDate.setValue(startOfMonth);
        if (lossDetailToDate != null) lossDetailToDate.setValue(now);
        if (reasonFromDate != null) reasonFromDate.setValue(startOfMonth);
        if (reasonToDate != null) reasonToDate.setValue(now);
        if (compFromDate != null) compFromDate.setValue(startOfMonth);
        if (compToDate != null) compToDate.setValue(now);
    }

    private void setupDefaultValues() {
        lowStockThresholdField.setText("10");
        topLossLimitField.setText("20");
    }

    private void loadInitialData() {
        loadInventoryReport();
        loadStockEntryReport();
        loadStockDetailReport();
        loadLossReport();
        loadTopLossReport();
        loadSummary();

        // Load new reports
        loadLossDetailReport();
        loadLossReasonReport();
        loadLossValueComparisonReport();
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void handleRefreshInventory() {
        loadInventoryReport();
    }

    @FXML
    private void handleShowLowStock() {
        try {
            int threshold = Integer.parseInt(lowStockThresholdField.getText());
            List<WarehouseReportDAO.InventoryReport> lowStockList =
                    WarehouseReportDAO.getLowStockReport(threshold);

            ObservableList<WarehouseReportDAO.InventoryReport> data =
                    FXCollections.observableArrayList(lowStockList);
            inventoryTable.setItems(data);

            showAlert("Notification", "Showing " + lowStockList.size() +
                    " products with stock ≤ " + threshold, Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid integer for warning threshold", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearchEntries() {
        loadStockEntryReport();
    }

    @FXML
    private void handleSearchDetails() {
        loadStockDetailReport();
    }

    @FXML
    private void handleSearchLoss() {
        loadLossReport();
    }

    @FXML
    private void handleShowTopLoss() {
        try {
            int limit = Integer.parseInt(topLossLimitField.getText());
            List<WarehouseReportDAO.ProductLossReport> topLossList =
                    WarehouseReportDAO.getTopLossProductsReport(limit);

            ObservableList<WarehouseReportDAO.ProductLossReport> data =
                    FXCollections.observableArrayList(topLossList);
            topLossTable.setItems(data);

            showAlert("Notification", "Showing top " + topLossList.size() +
                    " products with highest losses", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid integer", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefreshSummary() {
        loadSummary();
    }

    // THÊM CÁC EVENT HANDLER MỚI
    @FXML
    private void handleSearchLossDetail() {
        loadLossDetailReport();
    }

    @FXML
    private void handleSearchReason() {
        loadLossReasonReport();
    }

    @FXML
    private void handleSearchComparison() {
        loadLossValueComparisonReport();
    }

    // ==================== DATA LOADING METHODS ====================

    private void loadInventoryReport() {
        try {
            List<WarehouseReportDAO.InventoryReport> inventoryList =
                    WarehouseReportDAO.getInventoryReport();

            // Translate Vietnamese status to English
            for (WarehouseReportDAO.InventoryReport report : inventoryList) {
                report.setStatus(translateStatusToEnglish(report.getStatus()));
            }

            ObservableList<WarehouseReportDAO.InventoryReport> data =
                    FXCollections.observableArrayList(inventoryList);
            inventoryTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load inventory report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String translateStatusToEnglish(String vietnameseStatus) {
        if (vietnameseStatus == null) return "";
        switch (vietnameseStatus) {
            case "Hết hàng": return "Out of Stock";
            case "Sắp hết": return "Critical";
            case "Ít hàng": return "Low Stock";
            case "Đủ hàng": return "In Stock";
            default: return vietnameseStatus;
        }
    }

    private void loadStockEntryReport() {
        try {
            Date fromDate = Date.valueOf(entryFromDate.getValue());
            Date toDate = Date.valueOf(entryToDate.getValue());

            List<WarehouseReportDAO.StockEntryReport> entryList =
                    WarehouseReportDAO.getStockEntryReport(fromDate, toDate);

            ObservableList<WarehouseReportDAO.StockEntryReport> data =
                    FXCollections.observableArrayList(entryList);
            stockEntryTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load stock entry report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadStockDetailReport() {
        try {
            Date fromDate = Date.valueOf(detailFromDate.getValue());
            Date toDate = Date.valueOf(detailToDate.getValue());

            List<WarehouseReportDAO.StockEntryDetailReport> detailList =
                    WarehouseReportDAO.getStockEntryDetailReport(fromDate, toDate);

            ObservableList<WarehouseReportDAO.StockEntryDetailReport> data =
                    FXCollections.observableArrayList(detailList);
            stockDetailTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load entry details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadLossReport() {
        try {
            Date fromDate = Date.valueOf(lossFromDate.getValue());
            Date toDate = Date.valueOf(lossToDate.getValue());

            List<WarehouseReportDAO.LossReport> lossList =
                    WarehouseReportDAO.getLossReport(fromDate, toDate);

            ObservableList<WarehouseReportDAO.LossReport> data =
                    FXCollections.observableArrayList(lossList);
            lossTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load loss report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadTopLossReport() {
        try {
            int limit = Integer.parseInt(topLossLimitField.getText());
            List<WarehouseReportDAO.ProductLossReport> topLossList =
                    WarehouseReportDAO.getTopLossProductsReport(limit);

            ObservableList<WarehouseReportDAO.ProductLossReport> data =
                    FXCollections.observableArrayList(topLossList);
            topLossTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load top loss products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // THÊM CÁC PHƯƠNG THỨC LOAD REPORT MỚI
    private void loadLossDetailReport() {
        if (lossDetailTable == null || lossDetailFromDate == null || lossDetailToDate == null) return;

        try {
            Date fromDate = Date.valueOf(lossDetailFromDate.getValue());
            Date toDate = Date.valueOf(lossDetailToDate.getValue());

            List<WarehouseReportDAO.LossDetailReport> detailList =
                    WarehouseReportDAO.getLossDetailReport(fromDate, toDate);

            ObservableList<WarehouseReportDAO.LossDetailReport> data =
                    FXCollections.observableArrayList(detailList);
            lossDetailTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load loss detail report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadLossReasonReport() {
        if (lossReasonTable == null || reasonFromDate == null || reasonToDate == null) return;

        try {
            Date fromDate = Date.valueOf(reasonFromDate.getValue());
            Date toDate = Date.valueOf(reasonToDate.getValue());

            List<WarehouseReportDAO.LossReasonReport> reasonList =
                    WarehouseReportDAO.getLossReasonReport(fromDate, toDate);

            ObservableList<WarehouseReportDAO.LossReasonReport> data =
                    FXCollections.observableArrayList(reasonList);
            lossReasonTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load loss reason report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadLossValueComparisonReport() {
        if (valueComparisonTable == null || compFromDate == null || compToDate == null) return;

        try {
            Date fromDate = Date.valueOf(compFromDate.getValue());
            Date toDate = Date.valueOf(compToDate.getValue());

            List<WarehouseReportDAO.LossValueComparisonReport> comparisonList =
                    WarehouseReportDAO.getLossValueComparisonReport(fromDate, toDate);

            ObservableList<WarehouseReportDAO.LossValueComparisonReport> data =
                    FXCollections.observableArrayList(comparisonList);
            valueComparisonTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load loss value comparison report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadSummary() {
        try {
            WarehouseReportDAO.WarehouseSummary summary = WarehouseReportDAO.getWarehouseSummary();

            totalProductsLabel.setText(String.valueOf(summary.getTotalProducts()));
            totalReceivedLabel.setText(String.valueOf(summary.getTotalReceived()));
            totalLossLabel.setText(String.valueOf(summary.getTotalLoss()));
            currentStockLabel.setText(String.valueOf(summary.getCurrentStock()));
            totalStockValueLabel.setText(String.format("%,.0f USD", summary.getTotalStockValue()));
            outOfStockLabel.setText(String.valueOf(summary.getOutOfStockCount()));
            lowStockLabel.setText(String.valueOf(summary.getLowStockCount()));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load overview statistics: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ==================== UTILITY METHODS ====================

    private <T> void formatPriceColumn(TableColumn<T, Double> column) {
        if (column == null) return;
        column.setCellFactory(col -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f USD", item));
                }
            }
        });
    }

    private <T> void formatPercentageColumn(TableColumn<T, Double> column) {
        if (column == null) return;
        column.setCellFactory(col -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", item));
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}