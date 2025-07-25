package controller.admin;

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


public class AdminController {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/" + fxml + ".fxml"));
            AnchorPane pane = loader.load();
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void loadRoleManagement() {
        loadUI("RoleManagement");
    }
    @FXML
    private void loadControlPanel() {
        loadUI("ControlPanel");
    }
    @FXML
    private void loadProductManagement() {
        loadUI("ProductManagement");
    }

    @FXML
    private void logout() {

    }

    public void loadSalaryCalculation(ActionEvent actionEvent) {
    }
}
