package controller.cashier;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Order;
import model.RevenueReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CashierController {

    @FXML private AnchorPane contentArea;
    @FXML private Button btnLogout;

    @FXML private TextField searchField;
    @FXML private Button clearButton;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colAmount;
    @FXML private TableColumn<Order, String> colDate;

    @FXML
    private void onClearSearch() {
        searchField.clear();
    }

    /**
     * Load UI into contentArea and return FXMLLoader for controller access
     */
    private FXMLLoader loadUI(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cashier/" + fxml + ".fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void loadControlPanel() {
        loadUI("ControlPanel");
    }

    @FXML
    private void loadPromotionManagement() {
        loadUI("PromotionManagement");
    }

    @FXML
    private void loadReturnPolicy() {
        loadUI("ReturnPolicy");
    }

    /**
     * Load Revenue Reports View & Pass Data to Controller
     */
    @FXML
    private void loadRevenueReports() {
        FXMLLoader loader = loadUI("RevenueReports");
        if (loader != null) {
            RevenueReportsController controller = loader.getController();
            List<RevenueReport> reports = fetchDetailedRevenueReports();
            controller.loadData(reports);
        }
    }

    private List<RevenueReport> fetchDetailedRevenueReports() {
        List<RevenueReport> reports = new ArrayList<>();
        reports.add(new RevenueReport("2025-07-25", "iPhone 15", 5000.0, "Credit Card"));
        reports.add(new RevenueReport("2025-07-26", "Samsung S24", 7500.0, "Cash"));
        return reports;
    }

    @FXML
    private void loadCalculateSalary() {
        loadUI("CalculatorSalary");
    }

    @FXML
    private void logout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}