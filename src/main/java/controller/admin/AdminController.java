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

    private void loadUI(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/" + fxml + ".fxml"));
            Parent pane = loader.load();
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void loadEmployeeManagement(ActionEvent event) {
        loadUI("EmployeeManagement");
    }
    @FXML
    private void loadDashboard(ActionEvent event) {
        loadUI("Dashboard");
    }
    @FXML
    private void loadProductManagement(ActionEvent event) {
        loadUI("ProductManagement");
    }
    @FXML
    private void loadSupplierManagement(ActionEvent event) {loadUI("SupplierManagement");}

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
