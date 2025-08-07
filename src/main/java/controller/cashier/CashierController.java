package controller.cashier;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CashierController {

    @FXML
    private AnchorPane contentArea;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {
        loadPage("/view/cashier/ControlPanelConfig.fxml");
    }

    @FXML
    void loadControlPanel(ActionEvent event) { loadPage("/view/cashier/ControlPanelConfig.fxml"); }

    @FXML
    void loadReturnPolicy(ActionEvent event) { loadPage("/view/cashier/ReturnPolicyManagement.fxml"); }

    @FXML
    void loadPromotionManagement(ActionEvent event) { loadPage("/view/cashier/PromotionManagement.fxml"); }

    @FXML
    void loadRevenueReports(ActionEvent event) { loadPage("/view/cashier/RevenueReport.fxml"); }

    @FXML
    void loadSalaryCalculation(ActionEvent event) { loadPage("/view/cashier/SalaryHistory.fxml"); }

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

    // ===== TỐI ƯU LOADPAGE VỚI LOADING GIỮA CONTENTAREA =====
    public void loadPage(String fxmlPath) {
        // ProgressIndicator giữa contentArea
        ProgressIndicator pi = new ProgressIndicator();
        StackPane stack = new StackPane(pi);
        stack.setPrefSize(contentArea.getWidth(), contentArea.getHeight());
        AnchorPane.setTopAnchor(stack, 0.0);
        AnchorPane.setBottomAnchor(stack, 0.0);
        AnchorPane.setLeftAnchor(stack, 0.0);
        AnchorPane.setRightAnchor(stack, 0.0);
        contentArea.getChildren().setAll(stack);

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                return loader.load();
            }
        };
        task.setOnSucceeded(e -> contentArea.getChildren().setAll(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    public void setUserInfo(String name, String role) {
        usernameLabel.setText(name);
        roleLabel.setText(role);
    }
}
