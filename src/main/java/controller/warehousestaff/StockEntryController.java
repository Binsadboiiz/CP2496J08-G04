package controller.warehousestaff;

import dao.ProductDAO;
import dao.StockEntryDAO;
import dao.StockEntryDetailDAO;
import dao.SupplierDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Product;
import model.StockEntry;
import model.StockEntryDetail;
import model.Supplier;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class StockEntryController {

    // ===== STOCK ENTRY LIST COMPONENTS =====
    @FXML private VBox listView;
    @FXML private TextField txtSearch;
    @FXML private TableView<StockEntry> table;
    @FXML private TableColumn<StockEntry, Integer> colEntryID;
    @FXML private TableColumn<StockEntry, String> colDate;
    @FXML private TableColumn<StockEntry, String> colSupplier;
    @FXML private TableColumn<StockEntry, String> colUser;
    @FXML private TableColumn<StockEntry, Integer> colTotalQuantity;
    @FXML private TableColumn<StockEntry, Double> colTotalValue;
    @FXML private TableColumn<StockEntry, Void> colDetail;
    @FXML private Button btnAdd, btnRefresh;
    @FXML private Label lblRecordCount;

    // ===== STOCK ENTRY FORM COMPONENTS =====
    @FXML private VBox formView;
    @FXML private ComboBox<Supplier> cbSupplier;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Product> cbProduct;
    @FXML private TextField txtQty;
    @FXML private TextField txtUnitCost;
    @FXML private Button btnAddProduct;
    @FXML private TableView<StockEntryDetail> tableDetails;
    @FXML private TableColumn<StockEntryDetail, Integer> colProductIDForm;
    @FXML private TableColumn<StockEntryDetail, String> colProductForm;
    @FXML private TableColumn<StockEntryDetail, String> colSupplierForm;
    @FXML private TableColumn<StockEntryDetail, String> colDateForm;
    @FXML private TableColumn<StockEntryDetail, Integer> colQtyForm;
    @FXML private TableColumn<StockEntryDetail, Double> colUnitCostForm;
    @FXML private TableColumn<StockEntryDetail, Void> colAction;
    @FXML private Label lblTotalForm;

    // ===== STOCK ENTRY DETAIL COMPONENTS =====
    @FXML private VBox detailView;
    @FXML private TableView<StockEntryDetail> tableDetail;
    @FXML private TableColumn<StockEntryDetail, Integer> colProductIDDetail;
    @FXML private TableColumn<StockEntryDetail, String> colProductDetail;
    @FXML private TableColumn<StockEntryDetail, String> colSupplierDetail;
    @FXML private TableColumn<StockEntryDetail, String> colDateDetail;
    @FXML private TableColumn<StockEntryDetail, Integer> colQtyDetail;
    @FXML private TableColumn<StockEntryDetail, Double> colUnitCostDetail;
    @FXML private TableColumn<StockEntryDetail, Double> colTotalDetail;
    @FXML private Label lblTitle;
    @FXML private Label lblTotal;

    // ===== SHARED VARIABLES =====
    private ObservableList<StockEntry> list = FXCollections.observableArrayList();
    private FilteredList<StockEntry> filteredList;
    private SortedList<StockEntry> sortedList; // THÊM SORTED LIST
    private ObservableList<StockEntryDetail> detailList = FXCollections.observableArrayList();
    private int entryID;

    // Cache cho data - tránh load lại nhiều lần
    private static List<Supplier> suppliersCache = null;
    private static List<Product> productsCache = null;
    private static long lastCacheTime = 0;
    private static final long CACHE_DURATION = 30000; // 30 seconds

    // Maps to store additional info
    private Map<StockEntryDetail, String> supplierNames = new HashMap<>();
    private Map<StockEntryDetail, String> entryDates = new HashMap<>();
    private Map<StockEntryDetail, String> supplierNamesDetail = new HashMap<>();
    private Map<StockEntryDetail, String> entryDatesDetail = new HashMap<>();

    // Cache for stock entry details
    private Map<Integer, List<StockEntryDetail>> stockEntryDetailsCache = new HashMap<>();

    // Dashboard controller reference
    private WarehouseStaffDashboardController dashboardController;

    // Debounce cho search
    private javafx.animation.Timeline searchTimeline;

    // THÊM BIẾN ĐỂ KIỂM SOÁT VIỆC REFRESH
    private boolean isRefreshing = false;

    @FXML
    public void initialize() {
        showListView();
        initializeListView();
        initializeFormView();
        initializeDetailView();
        setupSearchFilter();
    }

    // Method để nhận dashboard controller reference
    public void setDashboardController(WarehouseStaffDashboardController controller) {
        this.dashboardController = controller;
    }

    // ===== CACHE METHODS =====
    private List<Supplier> getSuppliers() {
        long currentTime = System.currentTimeMillis();
        if (suppliersCache == null || (currentTime - lastCacheTime) > CACHE_DURATION) {
            suppliersCache = SupplierDAO.getAll();
            lastCacheTime = currentTime;
        }
        return suppliersCache;
    }

    private List<Product> getProducts() {
        long currentTime = System.currentTimeMillis();
        if (productsCache == null || (currentTime - lastCacheTime) > CACHE_DURATION) {
            productsCache = ProductDAO.getAll();
            lastCacheTime = currentTime;
        }
        return productsCache;
    }

    // ===== SEARCH FUNCTIONALITY - OPTIMIZED =====
    private void setupSearchFilter() {
        // Tạo FilteredList và SortedList để hỗ trợ sorting
        filteredList = new FilteredList<>(list, p -> true);
        sortedList = new SortedList<>(filteredList);

        // Bind comparator của sortedList với table để enable sorting
        sortedList.comparatorProperty().bind(table.comparatorProperty());

        // Set items cho table
        table.setItems(sortedList);

        // Debounce search để tránh lag
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            // CHỈ SEARCH KHI KHÔNG ĐANG REFRESH
            if (isRefreshing) return;

            if (searchTimeline != null) {
                searchTimeline.stop();
            }

            searchTimeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.millis(300), // Đợi 300ms sau khi user ngừng gõ
                            e -> performSearch(newValue)
                    )
            );
            searchTimeline.play();
        });
    }

    private void performSearch(String searchText) {
        // TRÁNH SEARCH KHI ĐANG REFRESH
        if (isRefreshing) return;

        filteredList.setPredicate(entry -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            // Tìm kiếm nhanh hơn
            return String.valueOf(entry.getEntryID()).contains(lowerCaseFilter) ||
                    (entry.getSupplierName() != null && entry.getSupplierName().toLowerCase().contains(lowerCaseFilter)) ||
                    (entry.getUserName() != null && entry.getUserName().toLowerCase().contains(lowerCaseFilter)) ||
                    formatDate(entry.getDate()).contains(lowerCaseFilter);
        });

        updateRecordCount();
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private void updateRecordCount() {
        if (lblRecordCount != null && sortedList != null) {
            lblRecordCount.setText("Total: " + sortedList.size() + " records");
        }
    }

    // ===== VIEW SWITCHING METHODS =====
    private void showListView() {
        listView.setVisible(true);
        listView.setManaged(true);
        formView.setVisible(false);
        formView.setManaged(false);
        detailView.setVisible(false);
        detailView.setManaged(false);
    }

    private void showFormView() {
        listView.setVisible(false);
        listView.setManaged(false);
        formView.setVisible(true);
        formView.setManaged(true);
        detailView.setVisible(false);
        detailView.setManaged(false);
    }

    private void showDetailView() {
        listView.setVisible(false);
        listView.setManaged(false);
        formView.setVisible(false);
        formView.setManaged(false);
        detailView.setVisible(true);
        detailView.setManaged(true);
    }

    // ===== LIST VIEW METHODS - OPTIMIZED =====
    private void initializeListView() {
        // ENABLE SORTING CHO TỪNG CỘT
        enableColumnSorting();

        loadTableAsync(); // Load async để tránh freeze UI
        btnRefresh.setOnAction(e -> loadTableAsync());

        // Double click to show detail
        table.setRowFactory(tv -> {
            TableRow<StockEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onShowDetail(row.getItem());
                }
            });
            return row;
        });

        setupListTableColumns();
    }

    // THÊM PHƯƠNG THỨC ENABLE SORTING
    private void enableColumnSorting() {
        // Enable sorting cho table
        table.setSortPolicy(table -> {
            return true; // Cho phép sorting
        });

        // Đặt sortable = true cho từng cột
        colEntryID.setSortable(true);
        colDate.setSortable(true);
        colSupplier.setSortable(true);
        colUser.setSortable(true);
        colTotalQuantity.setSortable(true);
        colTotalValue.setSortable(true);
        // colDetail không cần sort vì là action column
        colDetail.setSortable(false);

        // DISABLE AUTO RESIZE để tránh jump
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Mặc định sắp xếp theo Entry ID giảm dần
        Platform.runLater(() -> {
            table.getSortOrder().clear();
            table.getSortOrder().add(colEntryID);
            colEntryID.setSortType(TableColumn.SortType.DESCENDING);
        });
    }

    private void setupListTableColumns() {
        colEntryID.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getEntryID()).asObject());

        colDate.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(formatDateDisplay(cell.getValue().getDate())));

        colSupplier.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getSupplierName()));

        colUser.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty("Warehouse Staff"));

        // Optimize total calculations
        colTotalQuantity.setCellValueFactory(cell -> {
            int totalQuantity = calculateTotalQuantityForEntry(cell.getValue().getEntryID());
            return new javafx.beans.property.SimpleIntegerProperty(totalQuantity).asObject();
        });

        colTotalValue.setCellValueFactory(cell -> {
            double totalValue = calculateTotalValueForEntry(cell.getValue().getEntryID());
            return new javafx.beans.property.SimpleDoubleProperty(totalValue).asObject();
        });

        colTotalValue.setCellFactory(col -> new TableCell<StockEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VND", item));
            }
        });

        colDetail.setCellFactory(param -> new TableCell<StockEntry, Void>() {
            private final Button viewBtn = new Button("View");

            {
                viewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6;");
                viewBtn.setOnAction(event -> {
                    StockEntry item = getTableView().getItems().get(getIndex());
                    onShowDetail(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
    }

    private String formatDateDisplay(Date date) {
        return date != null ? new SimpleDateFormat("dd/MM/yyyy").format(date) : "";
    }

    private int calculateTotalQuantityForEntry(int entryID) {
        return getStockEntryDetails(entryID).stream().mapToInt(StockEntryDetail::getQuantity).sum();
    }

    private double calculateTotalValueForEntry(int entryID) {
        return getStockEntryDetails(entryID).stream()
                .mapToDouble(detail -> detail.getQuantity() * detail.getUnitCost()).sum();
    }

    private List<StockEntryDetail> getStockEntryDetails(int entryID) {
        return stockEntryDetailsCache.computeIfAbsent(entryID, id -> StockEntryDetailDAO.getByEntryID(id));
    }

    // ASYNC loading được tối ưu để tránh scroll tự động
    private void loadTableAsync() {
        isRefreshing = true; // BẮT ĐẦU REFRESH

        // LƯU LẠI SELECTION
        int selectedIndex = table.getSelectionModel().getSelectedIndex();
        StockEntry selectedItem = table.getSelectionModel().getSelectedItem();

        // Hiển thị loading indicator
        table.setPlaceholder(new ProgressIndicator());

        Task<List<StockEntry>> task = new Task<List<StockEntry>>() {
            @Override
            protected List<StockEntry> call() throws Exception {
                stockEntryDetailsCache.clear();
                return StockEntryDAO.getAll();
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                // UPDATE DATA KHÔNG CLEAR SELECTION
                List<StockEntry> newData = task.getValue();

                // Cập nhật data mà không làm mất focus
                list.clear();
                list.addAll(newData);

                table.setPlaceholder(new Label("No data available"));

                // KHÔI PHỤC SELECTION SAU KHI DATA LOAD XONG
                Platform.runLater(() -> {
                    try {
                        // Tìm và select lại item cũ nếu còn tồn tại
                        if (selectedItem != null) {
                            for (int i = 0; i < sortedList.size(); i++) {
                                if (sortedList.get(i).getEntryID() == selectedItem.getEntryID()) {
                                    table.getSelectionModel().select(i);
                                    table.scrollTo(i);
                                    break;
                                }
                            }
                        } else if (selectedIndex >= 0 && selectedIndex < sortedList.size()) {
                            table.getSelectionModel().select(selectedIndex);
                            table.scrollTo(selectedIndex);
                        }
                    } catch (Exception ex) {
                        // Ignore restore errors
                    }

                    updateRecordCount();
                    isRefreshing = false; // KẾT THÚC REFRESH
                });
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                table.setPlaceholder(new Label("Error loading data"));
                task.getException().printStackTrace();
                isRefreshing = false; // KẾT THÚC REFRESH
            });
        });

        new Thread(task).start();
    }

    private void onShowDetail(StockEntry entry) {
        setEntryID(entry.getEntryID());
        showDetailView();
    }

    @FXML
    private void onAdd() {
        clearForm();
        showFormView();
    }

    @FXML
    private void onRefresh() {
        loadTableAsync();
    }

    // ===== FORM VIEW METHODS - OPTIMIZED =====
    private void initializeFormView() {
        loadComboBoxData();
        setupFormTableColumns();
        setupOptimizedSearch();

        tableDetails.setItems(detailList);
        lblTotalForm.setText("0 VND");

        // Set default date
        dpDate.setValue(LocalDate.now());
    }

    private void loadComboBoxData() {
        // Load suppliers
        List<Supplier> suppliers = getSuppliers();
        cbSupplier.setItems(FXCollections.observableArrayList(suppliers));

        // Load products
        List<Product> products = getProducts();
        cbProduct.setItems(FXCollections.observableArrayList(products));
    }

    private void setupFormTableColumns() {
        colProductIDForm.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getProductID()).asObject());
        colProductForm.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getProductName()));
        colSupplierForm.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(supplierNames.getOrDefault(cell.getValue(), "N/A")));
        colDateForm.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(entryDates.getOrDefault(cell.getValue(), "N/A")));
        colQtyForm.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colUnitCostForm.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getUnitCost()).asObject());

        colUnitCostForm.setCellFactory(col -> new TableCell<StockEntryDetail, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f", item));
            }
        });

        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnDelete = new Button("Delete");
            {
                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6;");
                btnDelete.setOnAction(e -> {
                    int index = getIndex();
                    if (index >= 0 && index < detailList.size()) {
                        StockEntryDetail itemToRemove = detailList.get(index);
                        supplierNames.remove(itemToRemove);
                        entryDates.remove(itemToRemove);
                        detailList.remove(index);
                        updateTotal();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    // SIMPLIFIED SEARCH - Tránh recursive updates
    private void setupOptimizedSearch() {
        // Supplier search
        cbSupplier.setEditable(true);
        cbSupplier.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                cbSupplier.show();
            }
        });

        // Product search
        cbProduct.setEditable(true);
        cbProduct.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                cbProduct.show();
            }
        });
    }

    private void clearForm() {
        cbSupplier.getSelectionModel().clearSelection();
        cbSupplier.getEditor().clear();
        dpDate.setValue(LocalDate.now());
        cbProduct.getSelectionModel().clearSelection();
        cbProduct.getEditor().clear();
        txtQty.clear();
        txtUnitCost.clear();
        detailList.clear();
        supplierNames.clear();
        entryDates.clear();
        lblTotalForm.setText("0 VND");
    }

    @FXML
    private void onAddProduct() {
        // Validation
        Supplier supplier = cbSupplier.getValue();
        Product product = cbProduct.getValue();
        LocalDate selectedDate = dpDate.getValue();
        String qtyStr = txtQty.getText().trim();
        String priceStr = txtUnitCost.getText().trim();

        if (supplier == null || product == null || selectedDate == null || qtyStr.isEmpty() || priceStr.isEmpty()) {
            showAlert("Please fill all fields.");
            return;
        }

        int qty;
        double price;
        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
            if (qty <= 0 || price < 0) throw new Exception();
        } catch (Exception ex) {
            showAlert("Invalid quantity or price.");
            return;
        }

        // Check duplicate
        String dateStr = selectedDate.toString();
        String supplierName = supplier.getName();

        boolean isDuplicate = detailList.stream().anyMatch(d ->
                d.getProductID() == product.getProductID() &&
                        supplierNames.get(d).equals(supplierName) &&
                        entryDates.get(d).equals(dateStr)
        );

        if (isDuplicate) {
            showAlert("This product from the same supplier on the same date is already added.");
            return;
        }

        // Add product
        StockEntryDetail detail = new StockEntryDetail();
        detail.setProductID(product.getProductID());
        detail.setProductName(product.getProductName());
        detail.setQuantity(qty);
        detail.setUnitCost(price);

        supplierNames.put(detail, supplierName);
        entryDates.put(detail, dateStr);
        detailList.add(detail);
        updateTotal();

        // Clear fields
        cbProduct.getSelectionModel().clearSelection();
        cbProduct.getEditor().clear();
        txtQty.clear();
        txtUnitCost.clear();
    }

    private void updateTotal() {
        double sum = detailList.stream().mapToDouble(d -> d.getQuantity() * d.getUnitCost()).sum();
        lblTotalForm.setText(String.format("%,.0f VND", sum));
    }

    @FXML
    private void onSave() {
        if (detailList.isEmpty()) {
            showAlert("Please add at least one product.");
            return;
        }

        // Save async để tránh freeze UI
        Task<Void> saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                saveStockEntries();
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> Platform.runLater(() -> {
            showAlert("Stock entries saved successfully!");
            clearForm(); // Clear form trước khi load lại
            loadTableAsync();
            showListView();
        }));

        saveTask.setOnFailed(e -> Platform.runLater(() -> {
            showAlert("Failed to save stock entries!");
            saveTask.getException().printStackTrace();
        }));

        new Thread(saveTask).start();
    }

    private void saveStockEntries() {
        Map<String, java.util.List<StockEntryDetail>> groupedDetails = new HashMap<>();

        for (StockEntryDetail detail : detailList) {
            String supplierName = supplierNames.get(detail);
            String dateStr = entryDates.get(detail);
            String key = supplierName + "|" + dateStr;
            groupedDetails.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(detail);
        }

        for (Map.Entry<String, java.util.List<StockEntryDetail>> entry : groupedDetails.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            String supplierName = keyParts[0];
            String dateStr = keyParts[1];

            Supplier supplier = getSuppliers().stream()
                    .filter(s -> s.getName().equals(supplierName))
                    .findFirst().orElse(null);

            if (supplier == null) continue;

            StockEntry stockEntry = new StockEntry();
            stockEntry.setSupplierID(supplier.getSupplierID());
            stockEntry.setDate(java.sql.Date.valueOf(LocalDate.parse(dateStr)));
            stockEntry.setUserID(1);

            int entryID = StockEntryDAO.insert(stockEntry);
            if (entryID <= 0) continue;

            for (StockEntryDetail detail : entry.getValue()) {
                detail.setEntryID(entryID);
                StockEntryDetailDAO.insert(detail);
            }
        }
    }

    @FXML
    private void onCancel() {
        showListView();
    }

    // ===== DETAIL VIEW METHODS =====
    private void initializeDetailView() {
        setupDetailTableColumns();
    }

    private void setupDetailTableColumns() {
        colProductIDDetail.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getProductID()).asObject());
        colProductDetail.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getProductName()));
        colSupplierDetail.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(supplierNamesDetail.getOrDefault(cell.getValue(), "N/A")));
        colDateDetail.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(entryDatesDetail.getOrDefault(cell.getValue(), "N/A")));
        colQtyDetail.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colUnitCostDetail.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getUnitCost()).asObject());

        colUnitCostDetail.setCellFactory(col -> new TableCell<StockEntryDetail, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VND", item));
            }
        });

        if (colTotalDetail != null) {
            colTotalDetail.setCellValueFactory(cell -> {
                StockEntryDetail detail = cell.getValue();
                double total = detail.getQuantity() * detail.getUnitCost();
                return new javafx.beans.property.SimpleDoubleProperty(total).asObject();
            });

            colTotalDetail.setCellFactory(col -> new TableCell<StockEntryDetail, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.format("%,.0f VND", item));
                }
            });
        }
    }

    public void setEntryID(int entryID) {
        this.entryID = entryID;
        loadDetails();
    }

    private void loadDetails() {
        List<StockEntryDetail> list = StockEntryDetailDAO.getByEntryID(entryID);

        if (!list.isEmpty()) {
            StockEntry entry = StockEntryDAO.getById(entryID);
            if (entry != null) {
                String supplierName = entry.getSupplierName();
                String dateStr = formatDateDisplay(entry.getDate());

                lblTitle.setText("📋 Entry Details - ID: " + entryID + " | " + supplierName + " | " + dateStr);

                for (StockEntryDetail detail : list) {
                    supplierNamesDetail.put(detail, supplierName);
                    entryDatesDetail.put(detail, dateStr);
                }
            }
        }

        tableDetail.setItems(FXCollections.observableArrayList(list));
        double sum = list.stream().mapToDouble(d -> d.getQuantity() * d.getUnitCost()).sum();
        lblTotal.setText(String.format("%,.0f VND", sum));
    }

    @FXML
    private void onClose() {
        supplierNamesDetail.clear();
        entryDatesDetail.clear();
        showListView();
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }
}