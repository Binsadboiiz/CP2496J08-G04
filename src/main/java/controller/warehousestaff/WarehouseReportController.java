package controller.warehousestaff;

import dao.*;
import dao.InventorySummaryDAO.InventorySummary;
import dao.InventorySummaryDAO.InventoryStatistics;
import dao.LossReportDAO.MonthlyLossReport;
import dao.LossReportDetailDAO.LossReportDetailExtended;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.StockEntry;
import model.StockEntryDetail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WarehouseReportController {

    @FXML private TextField txtSearchEntries;
    @FXML private TextField txtSearchLoss;

    @FXML private Label lblEntriesCount;
    @FXML private Label lblLossCount;

    @FXML private TableView<StockEntry> tblRecentEntries;
    @FXML private TableColumn<StockEntry, Integer> colEntryID;
    @FXML private TableColumn<StockEntry, String> colEntryDate;
    @FXML private TableColumn<StockEntry, String> colSupplier;
    @FXML private TableColumn<StockEntry, Integer> colTotalQuantity;
    @FXML private TableColumn<StockEntry, Double> colTotalValue;

    @FXML private TableView<LossReportDetailExtended> tblRecentLoss;
    @FXML private TableColumn<LossReportDetailExtended, Integer> colLossReportID;
    @FXML private TableColumn<LossReportDetailExtended, String> colLossProduct;
    @FXML private TableColumn<LossReportDetailExtended, Integer> colLossQuantity;
    @FXML private TableColumn<LossReportDetailExtended, String> colLossReason;
    @FXML private TableColumn<LossReportDetailExtended, Double> colLossValue;
    @FXML private TableColumn<LossReportDetailExtended, Double> colAvgUnitCost;
    @FXML private TableColumn<LossReportDetailExtended, String> colLossReportDate;

    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private TabPane tabPane;

    private ObservableList<StockEntry> recentEntriesList = FXCollections.observableArrayList();
    private ObservableList<LossReportDetailExtended> recentLossList = FXCollections.observableArrayList();

    private FilteredList<StockEntry> filteredEntriesList;
    private FilteredList<LossReportDetailExtended> filteredLossList;

    private Map<Integer, List<StockEntryDetail>> stockEntryDetailsCache = new HashMap<>();

    @FXML
    public void initialize() {
        setupTables();
        setupControls();
        setupSearchFilters();
        loadAllData();
    }

    private void setupTables() {
        setupStockEntriesTable();
        setupLossReportsTable();

        filteredEntriesList = new FilteredList<>(recentEntriesList, p -> true);
        filteredLossList = new FilteredList<>(recentLossList, p -> true);

        tblRecentEntries.setItems(filteredEntriesList);
        tblRecentLoss.setItems(filteredLossList);
    }

    private void setupStockEntriesTable() {
        colEntryID.setCellValueFactory(new PropertyValueFactory<>("entryID"));

        colEntryDate.setCellValueFactory(cell -> {
            Date date = cell.getValue().getDate();
            if (date != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                return new javafx.beans.property.SimpleStringProperty(dateFormat.format(date));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        tblRecentEntries.setRowFactory(tv -> {
            TableRow<StockEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showStockEntryDetails(row.getItem());
                }
            });
            return row;
        });

        colTotalQuantity.setCellValueFactory(cell -> {
            int entryID = cell.getValue().getEntryID();
            int totalQuantity = calculateTotalQuantityForEntry(entryID);
            return new javafx.beans.property.SimpleIntegerProperty(totalQuantity).asObject();
        });

        colTotalValue.setCellValueFactory(cell -> {
            int entryID = cell.getValue().getEntryID();
            double totalValue = calculateTotalValueForEntry(entryID);
            return new javafx.beans.property.SimpleDoubleProperty(totalValue).asObject();
        });

        colTotalValue.setCellFactory(col -> new TableCell<StockEntry, Double>() {
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

    private void setupLossReportsTable() {
        colLossReportID.setCellValueFactory(new PropertyValueFactory<>("reportID"));
        colLossProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colLossQuantity.setCellValueFactory(new PropertyValueFactory<>("lostQuantity"));
        colLossReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colLossValue.setCellValueFactory(new PropertyValueFactory<>("lossValue"));

        if (colLossReportDate != null) {
            colLossReportDate.setCellValueFactory(cellData -> {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getReportDateFormatted()
                );
            });
        }

        if (colAvgUnitCost != null) {
            colAvgUnitCost.setCellValueFactory(new PropertyValueFactory<>("avgUnitCost"));
            colAvgUnitCost.setCellFactory(col -> new TableCell<LossReportDetailExtended, Double>() {
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

        colLossValue.setCellFactory(col -> new TableCell<LossReportDetailExtended, Double>() {
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

        // Add double-click handler for loss report details
        tblRecentLoss.setRowFactory(tv -> {
            TableRow<LossReportDetailExtended> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleViewLossReportDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupSearchFilters() {
        if (txtSearchEntries != null) {
            txtSearchEntries.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredEntriesList.setPredicate(entry -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (String.valueOf(entry.getEntryID()).contains(lowerCaseFilter)) {
                        return true;
                    }

                    if (entry.getSupplierName() != null &&
                            entry.getSupplierName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    if (entry.getDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String dateStr = dateFormat.format(entry.getDate());
                        if (dateStr.contains(lowerCaseFilter)) {
                            return true;
                        }
                    }

                    return false;
                });

                updateEntriesCount();
            });
        }

        if (txtSearchLoss != null) {
            txtSearchLoss.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredLossList.setPredicate(lossReport -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (String.valueOf(lossReport.getReportID()).contains(lowerCaseFilter)) {
                        return true;
                    }

                    if (lossReport.getProductName() != null &&
                            lossReport.getProductName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    if (lossReport.getReason() != null &&
                            lossReport.getReason().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    return false;
                });

                updateLossCount();
            });
        }
    }

    private void updateEntriesCount() {
        if (lblEntriesCount != null && filteredEntriesList != null) {
            lblEntriesCount.setText("Total: " + filteredEntriesList.size() + " records");
        }
    }

    private void updateLossCount() {
        if (lblLossCount != null && filteredLossList != null) {
            lblLossCount.setText("Total: " + filteredLossList.size() + " records");
        }
    }

    private int calculateTotalQuantityForEntry(int entryID) {
        List<StockEntryDetail> details = getStockEntryDetails(entryID);
        return details.stream().mapToInt(StockEntryDetail::getQuantity).sum();
    }

    private double calculateTotalValueForEntry(int entryID) {
        List<StockEntryDetail> details = getStockEntryDetails(entryID);
        return details.stream().mapToDouble(detail -> detail.getQuantity() * detail.getUnitCost()).sum();
    }

    private List<StockEntryDetail> getStockEntryDetails(int entryID) {
        if (!stockEntryDetailsCache.containsKey(entryID)) {
            List<StockEntryDetail> details = StockEntryDetailDAO.getByEntryID(entryID);
            stockEntryDetailsCache.put(entryID, details);
        }
        return stockEntryDetailsCache.get(entryID);
    }

    private void setupControls() {
        dpFromDate.setValue(LocalDate.now().minusMonths(1));
        dpToDate.setValue(LocalDate.now());

        dpFromDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpToDate.getValue() != null) {
                loadAllData();
            }
        });

        dpToDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpFromDate.getValue() != null) {
                loadAllData();
            }
        });
    }

    private void loadAllData() {
        try {
            stockEntryDetailsCache.clear();

            loadRecentStockEntries();
            loadRecentLossReports();

            updateEntriesCount();
            updateLossCount();
        } catch (Exception e) {
            showAlert("Error loading data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadRecentStockEntries() {
        try {
            List<StockEntry> allEntries = StockEntryDAO.getAll();
            recentEntriesList.setAll(allEntries);
        } catch (Exception e) {
            showAlert("Error loading recent stock entries: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadRecentLossReports() {
        try {
            List<LossReportDetailExtended> allLossReports = LossReportDetailDAO.getLossReportDetailsExtended();
            recentLossList.setAll(allLossReports);
        } catch (Exception e) {
            showAlert("Error loading recent loss reports: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showStockEntryDetails(StockEntry entry) {
        if (entry == null) {
            showAlert("No stock entry selected!", Alert.AlertType.WARNING);
            return;
        }

        List<StockEntryDetail> details = StockEntryDetailDAO.getByEntryID(entry.getEntryID());

        if (details.isEmpty()) {
            showAlert("No details found for this entry!", Alert.AlertType.INFORMATION);
            return;
        }

        Dialog<Void> detailDialog = new Dialog<>();
        detailDialog.setTitle("Stock Entry Details");
        detailDialog.setHeaderText("Entry ID: " + entry.getEntryID() +
                " | Date: " + new SimpleDateFormat("dd/MM/yyyy").format(entry.getDate()) +
                " | Supplier: " + entry.getSupplierName());

        TableView<StockEntryDetail> detailTable = new TableView<>();
        detailTable.setPrefWidth(700);
        detailTable.setPrefHeight(400);

        TableColumn<StockEntryDetail, Integer> colProductID = new TableColumn<>("Product ID");
        colProductID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colProductID.setPrefWidth(80);

        TableColumn<StockEntryDetail, String> colProductName = new TableColumn<>("Product Name");
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductName.setPrefWidth(200);

        TableColumn<StockEntryDetail, Integer> colQuantity = new TableColumn<>("Quantity");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setPrefWidth(80);

        TableColumn<StockEntryDetail, Double> colUnitCost = new TableColumn<>("Unit Cost");
        colUnitCost.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        colUnitCost.setPrefWidth(100);

        colUnitCost.setCellFactory(col -> new TableCell<StockEntryDetail, Double>() {
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

        TableColumn<StockEntryDetail, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(cell -> {
            StockEntryDetail detail = cell.getValue();
            double total = detail.getQuantity() * detail.getUnitCost();
            return new javafx.beans.property.SimpleDoubleProperty(total).asObject();
        });
        colTotal.setPrefWidth(120);

        colTotal.setCellFactory(col -> new TableCell<StockEntryDetail, Double>() {
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

        detailTable.getColumns().addAll(colProductID, colProductName, colQuantity, colUnitCost, colTotal);

        detailTable.setItems(FXCollections.observableArrayList(details));

        double grandTotal = details.stream().mapToDouble(d -> d.getQuantity() * d.getUnitCost()).sum();

        VBox content = new VBox(10);
        content.getChildren().add(detailTable);

        Label totalLabel = new Label("Grand Total: " + String.format("%,.0f VND", grandTotal));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        content.getChildren().add(totalLabel);

        detailDialog.getDialogPane().setContent(content);
        detailDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        detailDialog.showAndWait();
    }

    private void handleViewLossReportDetail(LossReportDetailExtended selected) {
        if (selected == null) {
            showAlert("No loss report selected!", Alert.AlertType.WARNING);
            return;
        }

        Alert detailAlert = new Alert(Alert.AlertType.INFORMATION);
        detailAlert.setTitle("Loss Report Details");
        detailAlert.setHeaderText("Loss Report ID: " + selected.getReportID());
        detailAlert.setContentText(String.format(
                "Product: %s\n" +
                        "Product Code: %s\n" +
                        "Brand: %s\n" +
                        "Quantity Lost: %d\n" +
                        "Average Unit Cost: %,.0f VND\n" +
                        "Loss Value: %,.0f VND\n" +
                        "Reason: %s\n" +
                        "Report Date: %s",
                selected.getProductName(),
                selected.getProductCode() != null ? selected.getProductCode() : "N/A",
                selected.getBrand() != null ? selected.getBrand() : "N/A",
                selected.getLostQuantity(),
                selected.getAvgUnitCost(),
                selected.getLossValue(),
                selected.getReason(),
                selected.getReportDateFormatted()
        ));

        detailAlert.showAndWait();
    }

    @FXML
    private void handleExportStockEntries() {
        try {
            exportStockEntriesToTxt();
        } catch (Exception e) {
            showAlert("Error exporting stock entries: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExportLossReports() {
        try {
            exportLossReportsToTxt();
        } catch (Exception e) {
            showAlert("Error exporting loss reports: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exportStockEntriesToTxt() {
        try {
            List<StockEntry> entriesToExport = filteredEntriesList.stream().collect(Collectors.toList());

            if (entriesToExport.isEmpty()) {
                showAlert("No stock entries to export!", Alert.AlertType.WARNING);
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Stock Entries Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("StockEntries_" + timestamp + ".txt");

            File file = fileChooser.showSaveDialog(tblRecentEntries.getScene().getWindow());

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("=====================================\n");
                    writer.write("        STOCK ENTRIES REPORT\n");
                    writer.write("=====================================\n");
                    writer.write("Export Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
                    writer.write("Total Records: " + entriesToExport.size() + "\n");
                    writer.write("=====================================\n\n");

                    writer.write(String.format("%-10s %-15s %-30s %-15s %-15s\n",
                            "Entry ID", "Date", "Supplier", "Total Qty", "Total Value"));
                    writer.write("-----------------------------------------------------\n");

                    double grandTotal = 0;
                    int totalQuantity = 0;

                    for (StockEntry entry : entriesToExport) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String dateStr = entry.getDate() != null ? dateFormat.format(entry.getDate()) : "N/A";

                        int entryTotalQty = calculateTotalQuantityForEntry(entry.getEntryID());
                        double entryTotalValue = calculateTotalValueForEntry(entry.getEntryID());

                        totalQuantity += entryTotalQty;
                        grandTotal += entryTotalValue;

                        writer.write(String.format("%-10d %-15s %-30s %-15d %15.0f\n",
                                entry.getEntryID(),
                                dateStr,
                                entry.getSupplierName() != null ? entry.getSupplierName() : "N/A",
                                entryTotalQty,
                                entryTotalValue));
                    }

                    writer.write("-----------------------------------------------------\n");
                    writer.write(String.format("%-56s %-15d %15.0f\n", "TOTAL:", totalQuantity, grandTotal));
                    writer.write("============================================================\n");

                    showAlert("Stock entries exported successfully to: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            }
        } catch (IOException e) {
            showAlert("Error writing to file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exportLossReportsToTxt() {
        try {
            List<LossReportDetailExtended> lossReportsToExport = filteredLossList.stream().collect(Collectors.toList());

            if (lossReportsToExport.isEmpty()) {
                showAlert("No loss reports to export!", Alert.AlertType.WARNING);
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Loss Reports");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("LossReports_" + timestamp + ".txt");

            File file = fileChooser.showSaveDialog(tblRecentLoss.getScene().getWindow());

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("=====================================\n");
                    writer.write("        LOSS REPORTS SUMMARY\n");
                    writer.write("=====================================\n");
                    writer.write("Export Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
                    writer.write("Total Records: " + lossReportsToExport.size() + "\n");
                    writer.write("=====================================\n\n");

                    writer.write(String.format("%-8s %-25s %-10s %-20s %-12s %-15s %-12s\n",
                            "ID", "Product", "Loss Qty", "Reason", "Loss Value", "Avg Unit Cost", "Report Date"));
                    writer.write("------------------------------------------------------------------------------------\n");

                    double totalLossValue = 0;
                    int totalLossQuantity = 0;

                    for (LossReportDetailExtended report : lossReportsToExport) {
                        totalLossQuantity += report.getLostQuantity();
                        totalLossValue += report.getLossValue();

                        String productName = report.getProductName();
                        if (productName.length() > 23) {
                            productName = productName.substring(0, 20) + "...";
                        }

                        String reason = report.getReason();
                        if (reason.length() > 18) {
                            reason = reason.substring(0, 15) + "...";
                        }

                        writer.write(String.format("%-8d %-25s %-10d %-20s %12.0f %15.0f %-12s\n",
                                report.getReportID(),
                                productName,
                                report.getLostQuantity(),
                                reason,
                                report.getLossValue(),
                                report.getAvgUnitCost(),
                                report.getReportDateFormatted()));
                    }

                    writer.write("------------------------------------------------------------------------------------\n");
                    writer.write(String.format("%-44s %-10d %12.0f\n", "TOTAL LOSSES:", totalLossQuantity, totalLossValue));
                    writer.write("====================================================================================\n");

                    showAlert("Loss reports exported successfully to: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            }
        } catch (IOException e) {
            showAlert("Error writing to file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Warehouse Report");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}