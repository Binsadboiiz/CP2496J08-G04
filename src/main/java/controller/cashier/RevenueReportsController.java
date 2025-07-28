package controller.cashier;

import dao.ProductDAO;
import dao.RevenueReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.RevenueReport;

import java.time.LocalDate;
import java.util.List;

public class RevenueReportsController {

    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTransactions;
    @FXML private Label lblAvgProductPrice;
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private PieChart bestSellingChart;
    @FXML private PieChart paymentMethodsChart;
    @FXML private TableView<RevenueReport> revenueTableView;
    @FXML private TableColumn<RevenueReport, String> colDate;
    @FXML private TableColumn<RevenueReport, String> colProduct;
    @FXML private TableColumn<RevenueReport, Double> colAmount;
    @FXML private TableColumn<RevenueReport, String> colPayment;


    private ObservableList<RevenueReport> revenueTableData = FXCollections.observableArrayList();
    private RevenueReportDAO revenueReportDAO;

    @FXML
    public void initialize() {
        revenueReportDAO = new RevenueReportDAO(ProductDAO.getConnection());

        setupTableView();
        loadBarChartData();
        loadBestSellingProducts();
        loadPaymentMethods();
        loadTodayData(); // Default is today
        updateSummary();
    }

    private void setupTableView() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        revenueTableView.setItems(revenueTableData);
    }

    private void loadBarChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");
        series.getData().addAll(
                new XYChart.Data<>("Jan", 35000),
                new XYChart.Data<>("Feb", 42000),
                new XYChart.Data<>("Mar", 38000),
                new XYChart.Data<>("Apr", 45000),
                new XYChart.Data<>("May", 51000),
                new XYChart.Data<>("Jun", 49000)
        );
        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    private void loadBestSellingProducts() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("iPhone 15 Pro Max", 40),
                new PieChart.Data("Samsung Galaxy S24 Ultra", 30),
                new PieChart.Data("Xiaomi 14", 15),
                new PieChart.Data("Oppo Find X7", 10),
                new PieChart.Data("Others", 5)
        );
        bestSellingChart.setData(pieData);
    }

    private void loadPaymentMethods() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Cash", 45),
                new PieChart.Data("Credit/Debit Card", 40),
                new PieChart.Data("E-Wallet", 15)
        );
        paymentMethodsChart.setData(pieData);
    }

    @FXML
    private void handleToday(ActionEvent event) {
        dpFrom.setValue(LocalDate.now());
        dpTo.setValue(LocalDate.now());
        loadTodayData();
        updateSummary();
    }

    @FXML
    private void handleThisWeek(ActionEvent event) {
        dpFrom.setValue(LocalDate.now().minusDays(6));  // 7 ngày
        dpTo.setValue(LocalDate.now());
        loadThisWeekData();
        updateSummary();
    }

    @FXML
    private void handleThisMonth(ActionEvent event) {
        dpFrom.setValue(LocalDate.now().withDayOfMonth(1));
        dpTo.setValue(LocalDate.now());
        loadThisMonthData();
        updateSummary();
    }

    @FXML
    private void handleThisYear(ActionEvent event) {
        dpFrom.setValue(LocalDate.now().withDayOfYear(1));
        dpTo.setValue(LocalDate.now());
        loadThisYearData();
        updateSummary();
    }

    private void loadTodayData() {
        ObservableList<RevenueReport> todayData = FXCollections.observableArrayList(
                new RevenueReport(LocalDate.now().toString(), "iPhone 15 Pro Max", 1199.99, "Credit Card"),
                new RevenueReport(LocalDate.now().toString(), "AirPods Pro", 249.99, "Cash")
        );
        revenueTableData.setAll(todayData);
    }

    private void loadThisWeekData() {
        ObservableList<RevenueReport> weekData = FXCollections.observableArrayList(
                new RevenueReport(LocalDate.now().minusDays(6).toString(), "Xiaomi 14", 799.99, "Cash"),
                new RevenueReport(LocalDate.now().minusDays(5).toString(), "Oppo Find X7", 899.99, "Card"),
                new RevenueReport(LocalDate.now().minusDays(2).toString(), "iPhone 15 Pro Max", 1199.99, "E-Wallet"),
                new RevenueReport(LocalDate.now().toString(), "AirPods Pro", 249.99, "Cash")
        );
        revenueTableData.setAll(weekData);
    }

    private void loadThisMonthData() {
        ObservableList<RevenueReport> monthData = FXCollections.observableArrayList(
                new RevenueReport(LocalDate.now().withDayOfMonth(1).toString(), "Samsung S24 Ultra", 1099.99, "Credit Card"),
                new RevenueReport(LocalDate.now().withDayOfMonth(5).toString(), "Oppo Find X7", 899.99, "Cash"),
                new RevenueReport(LocalDate.now().withDayOfMonth(10).toString(), "Xiaomi 14", 799.99, "Card"),
                new RevenueReport(LocalDate.now().toString(), "iPhone 15 Pro Max", 1199.99, "E-Wallet")
        );
        revenueTableData.setAll(monthData);
    }

    private void loadThisYearData() {
        ObservableList<RevenueReport> yearData = FXCollections.observableArrayList(
                new RevenueReport("2025-01-15", "Samsung S24 Ultra", 1099.99, "Card"),
                new RevenueReport("2025-03-20", "iPhone 15 Pro Max", 1199.99, "Credit Card"),
                new RevenueReport("2025-06-12", "Oppo Find X7", 899.99, "Cash"),
                new RevenueReport(LocalDate.now().toString(), "AirPods Pro", 249.99, "Cash")
        );
        revenueTableData.setAll(yearData);
    }

    private void updateSummary() {
        double totalRevenue = 0;
        int transactions = revenueTableData.size();

        for (RevenueReport report : revenueTableData) {
            totalRevenue += report.getAmount();
        }

        String avgProductPrice = transactions > 0 ? String.format("$%.2f", totalRevenue / transactions) : "$0.00";

        lblTotalRevenue.setText(String.format("$%.2f", totalRevenue));
        lblTransactions.setText(String.valueOf(transactions));
        lblAvgProductPrice.setText(avgProductPrice);
    }

    @FXML
    private void exportPDF(ActionEvent event) {
        showAlert("Export", "PDF export functionality would be implemented here");
    }

    @FXML
    private void exportCSV(ActionEvent event) {
        showAlert("Export", "CSV export functionality would be implemented here");
    }

    @FXML
    private void printReport(ActionEvent event) {
        showAlert("Print", "Print functionality would be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method for dynamic data passing from parent controller if needed
    public void loadData(List<RevenueReport> data) {
        revenueTableData.setAll(data);
        updateSummary();
    }
    @FXML
    private void loadRevenueReports(ActionEvent event) {
        // Lấy ngày từ dpFrom và dpTo
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();

        if (from != null && to != null) {
            ObservableList<RevenueReport> filteredData = FXCollections.observableArrayList(
                    new RevenueReport(from.toString(), "Filter Product 1", 999.99, "Card"),
                    new RevenueReport(to.toString(), "Filter Product 2", 499.99, "Cash")
            );
            revenueTableData.setAll(filteredData);
            updateSummary();
            showAlert("Filter Applied", "Filtered data from " + from + " to " + to);
        } else {
            showAlert("Error", "Please select both From and To dates.");
        }
    }

}
