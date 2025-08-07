package controller.admin;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;

public class AdminController {
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Button btnLogout;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;

    private void loadUI(String fxml) {
        ProgressIndicator pi = new ProgressIndicator();
        StackPane stack = new StackPane(pi);
        stack.setPrefSize(contentArea.getWidth(), contentArea.getHeight());
        contentArea.getChildren().setAll(stack);

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/" + fxml + ".fxml"));
                return loader.load();
            }
        };
        task.setOnSucceeded(e -> contentArea.getChildren().setAll(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    private void initialize() { loadUI("Dashboard"); }
    @FXML
    private void loadEmployeeManagement(ActionEvent event) { loadUI("EmployeeManagement"); }
    @FXML
    private void loadDashboard(ActionEvent event) { loadUI("Dashboard"); }
    @FXML
    private void loadProductManagement(ActionEvent event) { loadUI("ProductManagement"); }
    @FXML
    private void loadSupplierManagement(ActionEvent event) { loadUI("SupplierManagement"); }
    @FXML
    private void loadStockEntry(ActionEvent event) { loadUI("StockEntryList"); }
    @FXML
    private void loadPromotionManagement(ActionEvent event) { loadUI("PromotionManagement"); }

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
    @FXML
    public void setUserInfo(String name, String role) {
        usernameLabel.setText(name);
        roleLabel.setText(role);
    }
}
