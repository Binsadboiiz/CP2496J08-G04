package controller.admin;

import dao.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Order;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label lblTotalProducts;

    @FXML
    private Label lblTotalEmployees;

    @FXML
    private Label lblTodaysSales;

    @FXML
    private Label lblStockAlerts;
    @FXML private TextField searchField;
    @FXML private Button clearButton;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colAmount;
    @FXML private TableColumn<Order, String> colDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardStats();
    }
    @FXML
    private void onClearSearch(ActionEvent e) {
        searchField.clear();
    }

    private void loadDashboardStats() {
        // 1. Tổng số sản phẩm
        int totalProducts = ProductDAO.getTotalProducts();
        lblTotalProducts.setText(String.valueOf(totalProducts));

        // 2. Tổng số nhân viên
        int totalEmployees = EmployeeDAO.getTotalEmployees();
        lblTotalEmployees.setText(String.valueOf(totalEmployees));

        // 3. Doanh thu hôm nay
        double todaysSales = InvoiceDAO.getTodaysSales();
        // format có dấu ngăn cách hàng nghìn
        DecimalFormat df = new DecimalFormat("#,###");
        lblTodaysSales.setText(df.format(todaysSales));

        // 4. Stock alerts (threshold = 5)
        int alerts = InventoryDAO.getStockAlerts(5);
        lblStockAlerts.setText(String.valueOf(alerts));
    }
}
