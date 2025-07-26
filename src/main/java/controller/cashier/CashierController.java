package controller.cashier;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.event.ActionEvent;
import model.Order;

import java.io.IOException;

public class CashierController {
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Button btnLogout;

    @FXML private TextField searchField;
    @FXML private Button clearButton;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colAmount;
    @FXML private TableColumn<Order, String> colDate;

    @FXML
    private void onClearSearch(ActionEvent e) {
        searchField.clear();
    }

    private void loadUI(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cashier/" + fxml + ".fxml"));
            AnchorPane pane = loader.load();
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    private void loadRevenueReports() {
        loadUI("RevenueReports");
    }

    @FXML
    private void loadCalculateSalary() {
        loadUI("CalculateSalary");
    }

    @FXML
    private void logout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
