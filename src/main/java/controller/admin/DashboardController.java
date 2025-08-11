package controller.admin;

import dao.EmployeeDAO;
import dao.InventoryDAO;
import dao.InvoiceDAO;
import dao.ProductDAO;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Invoice;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    // KPI
    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalEmployees;
    @FXML private Label lblTodaysSales;
    @FXML private Label lblStockAlerts;

    // Recent Orders + search
    @FXML private TextField searchField;
    @FXML private Button clearButton;
    @FXML private TableView<Invoice> orderTable;
    @FXML private TableColumn<Invoice, String> colOrderId;
    @FXML private TableColumn<Invoice, String> colCustomer;
    @FXML private TableColumn<Invoice, String> colStatus;
    @FXML private TableColumn<Invoice, String> colAmount;
    @FXML private TableColumn<Invoice, String> colDate;

    private final ObservableList<Invoice> masterOrders   = FXCollections.observableArrayList();
    private final ObservableList<Invoice> filteredOrders = FXCollections.observableArrayList();

    private static final Locale VI_VN = new Locale("vi", "VN");
    private static final NumberFormat VND = NumberFormat.getNumberInstance(VI_VN);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // KPI
        loadDashboardStats();

        // Recent Orders table
        mapRecentOrderColumns();
        orderTable.setItems(filteredOrders);
        loadRecentOrders(10);
        orderTable.setPlaceholder(new Label("No recent orders"));
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        orderTable.setTableMenuButtonVisible(true);

        // Search
        searchField.textProperty().addListener((obs, o, n) -> applyFilter(n));
    }

    // ==== KPI ====
    private void loadDashboardStats() {
        lblTotalProducts.setText(String.valueOf(ProductDAO.getTotalProducts()));
        lblTotalEmployees.setText(String.valueOf(EmployeeDAO.getTotalEmployees()));

        double todaysSales = InvoiceDAO.getTodaysSales();
        lblTodaysSales.setText(VND.format(todaysSales) + " VND");

        int alerts = InventoryDAO.getStockAlerts(5);
        lblStockAlerts.setText(String.valueOf(alerts));
    }

    // ==== Recent Orders ====
    private void mapRecentOrderColumns() {
        colOrderId.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(String.valueOf(c.getValue().getInvoiceID())));

        colCustomer.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(nullToEmpty(c.getValue().getCustomerName())));

        colStatus.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(nullToEmpty(c.getValue().getStatus())));

        colAmount.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(VND.format(c.getValue().getTotalAmount()) + " VND"));

        colDate.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getDate() == null ? "" : DATE_FMT.format(c.getValue().getDate())));

        // align
        colOrderId.setStyle("-fx-alignment: CENTER;");
        colStatus.setStyle("-fx-alignment: CENTER;");
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT;");
        colDate.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    private void loadRecentOrders(int limit) {
        masterOrders.setAll(InvoiceDAO.getRecentOrders(limit));
        applyFilter(searchField.getText());
    }

    private void applyFilter(String keyword) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        if (kw.isEmpty()) {
            filteredOrders.setAll(masterOrders);
            return;
        }
        Predicate<Invoice> p = inv -> {
            String id = String.valueOf(inv.getInvoiceID());
            String customer = nullToEmpty(inv.getCustomerName()).toLowerCase();
            String status = nullToEmpty(inv.getStatus()).toLowerCase();
            String amount = VND.format(inv.getTotalAmount()).toLowerCase();
            String dateStr = inv.getDate() == null ? "" : DATE_FMT.format(inv.getDate()).toLowerCase();
            return id.contains(kw) || customer.contains(kw) || status.contains(kw)
                    || amount.contains(kw) || dateStr.contains(kw);
        };
        filteredOrders.setAll(masterOrders.stream().filter(p).collect(Collectors.toList()));
    }

    @FXML
    private void onClearSearch(ActionEvent e) {
        searchField.clear();
        applyFilter("");
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
