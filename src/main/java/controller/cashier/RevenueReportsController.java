package controller.cashier;

import dao.RevenueReportDAO;
import dao.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import model.RevenueReport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RevenueReportsController {

    @FXML private ToggleButton todayButton;
    @FXML private ToggleButton weekButton;
    @FXML private ToggleButton monthButton;
    @FXML private ToggleButton yearButton;

    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private StackPane chartContainer;

    private RevenueReportDAO revenueReportDAO;
    private ToggleGroup timeToggleGroup; // Tạo ToggleGroup bằng code

    @FXML
    public void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            revenueReportDAO = new RevenueReportDAO(conn);

            // Tạo ToggleGroup và add các ToggleButton vào
            timeToggleGroup = new ToggleGroup();
            todayButton.setToggleGroup(timeToggleGroup);
            weekButton.setToggleGroup(timeToggleGroup);
            monthButton.setToggleGroup(timeToggleGroup);
            yearButton.setToggleGroup(timeToggleGroup);

            setupToggleGroup();

            // Mặc định chọn Today
            todayButton.setSelected(true);
            loadTodayData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupToggleGroup() {
        timeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) return;
            ToggleButton selected = (ToggleButton) newToggle;
            String label = selected.getText();
            switch (label) {
                case "Today" -> loadTodayData();
                case "This Week" -> loadWeekData();
                case "This Month" -> loadMonthData();
                case "This Year" -> loadYearData();
            }
        });
    }

    private void loadTodayData() {
        try {
            List<RevenueReport> data = revenueReportDAO.getTodayRevenueByPaymentMethod();
            showPieChart(data, "Today's Payment Methods");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadWeekData() {
        try {
            List<RevenueReport> data = revenueReportDAO.getWeekRevenueByPaymentMethod();
            showPieChart(data, "This Week's Payment Methods");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMonthData() {
        try {
            List<RevenueReport> data = revenueReportDAO.getMonthRevenueByPaymentMethod();
            showPieChart(data, "This Month's Payment Methods");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadYearData() {
        try {
            List<RevenueReport> data = revenueReportDAO.getYearRevenueByMonth();
            showBarChart(data, "Revenue by Month (This Year)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showPieChart(List<RevenueReport> data, String title) {
        pieChart.getData().clear();
        for (RevenueReport r : data) {
            PieChart.Data slice = new PieChart.Data(r.getLabel(), r.getTotal());
            pieChart.getData().add(slice);
        }
        pieChart.setTitle(title);
        pieChart.setVisible(true);
        barChart.setVisible(false);
    }

    private void showBarChart(List<RevenueReport> data, String title) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (RevenueReport r : data) {
            series.getData().add(new XYChart.Data<>(r.getLabel(), r.getTotal()));
        }
        barChart.getData().add(series);
        barChart.setTitle(title);
        barChart.setVisible(true);
        pieChart.setVisible(false);
    }
}
