package controller.cashier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CashierController {

    @FXML
    private AnchorPane contentArea;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {loadPage("/view/cashier/ControlPanelConfig.fxml");}

    @FXML
    void loadControlPanel(ActionEvent event) throws IOException {
        loadPage("/view/cashier/ControlPanelConfig.fxml");
    }

    @FXML
    void loadReturnPolicy(ActionEvent event) throws IOException {
        loadPage("/view/cashier/ReturnPolicyManagement.fxml");
    }

    @FXML
    void loadPromotionManagement(ActionEvent event) throws IOException {
        loadPage("/view/cashier/PromotionManagement.fxml");
    }

    @FXML
    void loadRevenueReports(ActionEvent event) throws IOException {
        loadPage("/view/cashier/RevenueReport.fxml");
    }

    @FXML
    void loadSalaryCalculation(ActionEvent event) throws IOException {
        loadPage("/view/cashier/SalaryHistory.fxml");
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

    public void loadPage(String fxmlPath) {
        try {
            Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void setUserInfo(String name, String role) {
        usernameLabel.setText(name);
        roleLabel.setText(role);
    }

    public void loadControlPanel() {
        try {
            Parent pane = FXMLLoader.load(getClass().getResource("/view/cashier/ControlPanelConfig.fxml"));
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
