package controller.cashier;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class RevenueReportsController {

    @FXML private ToggleButton todayButton;
    @FXML private ToggleButton weekButton;
    @FXML private ToggleButton monthButton;
    @FXML private ToggleButton yearButton;
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private StackPane chartContainer; // StackPane chung

    private ToggleGroup viewToggleGroup;

    @FXML
    public void initialize() {
        // Group Toggle Buttons
        viewToggleGroup = new ToggleGroup();
        todayButton.setToggleGroup(viewToggleGroup);
        weekButton.setToggleGroup(viewToggleGroup);
        monthButton.setToggleGroup(viewToggleGroup);
        yearButton.setToggleGroup(viewToggleGroup);

        // Default state: PieChart visible, BarChart hidden
        pieChart.setVisible(true);
        barChart.setVisible(false);
        loadRevenuePieChart("ThisMonth");

        // Toggle Listener
        viewToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String mode = newToggle.getUserData().toString();
                switchChartView(mode);

                if (mode.equals("ThisYear")) {
                    loadRevenueBarChart();
                } else {
                    loadRevenuePieChart(mode);
                }
            }
        });
    }

    private void switchChartView(String mode) {
        if (mode.equals("ThisYear")) {
            pieChart.setVisible(false);
            barChart.setVisible(true);
        } else {
            pieChart.setVisible(true);
            barChart.setVisible(false);
        }
    }

    private void loadRevenuePieChart(String mode) {
        pieChart.getData().clear();

        switch (mode) {
            case "Today":
                pieChart.setTitle("Revenue Today (2023)");
                pieChart.getData().addAll(
                        new PieChart.Data("January", 1000),
                        new PieChart.Data("February", 1500),
                        new PieChart.Data("March", 800)
                );
                break;

            case "ThisWeek":
                pieChart.setTitle("Revenue This Week (2024)");
                pieChart.getData().addAll(
                        new PieChart.Data("April", 2000),
                        new PieChart.Data("May", 2500),
                        new PieChart.Data("June", 1800)
                );
                break;

            case "ThisMonth":
                pieChart.setTitle("Revenue This Month (2025)");
                pieChart.getData().addAll(
                        new PieChart.Data("July", 3000),
                        new PieChart.Data("August", 3500),
                        new PieChart.Data("September", 2800)
                );
                break;
        }

        // Dummy Summary Info (You will fetch from DB later)"iPhone 15 Pro");
    }

    private void loadRevenueBarChart() {
        barChart.getData().clear();
        barChart.setTitle("Revenue by Year");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Annual Revenue");

        series.getData().add(new XYChart.Data<>("2022", 50000));
        series.getData().add(new XYChart.Data<>("2023", 60000));
        series.getData().add(new XYChart.Data<>("2024", 70000));
        series.getData().add(new XYChart.Data<>("2025", 80000));

        barChart.getData().add(series);
    }
}
