package controller.warehousestaff;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class WarehouseStaffDashboardController {

    @FXML
    private AnchorPane contentArea;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;

    // ===== LOAD FXML VỚI LOADING =====
    public void loadPage(String fxmlPath) {
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
                Parent fxml = loader.load();

                // Truyền tham chiếu WarehouseStaffDashboardController nếu cần
                if (fxmlPath.endsWith("HomeView.fxml")) {
                    // Nếu có HomeViewController thì truyền reference
                    // HomeViewController homeController = loader.getController();
                    // homeController.setWarehouseStaffDashboardController(WarehouseStaffDashboardController.this);
                }
                return fxml;
            }
        };
        task.setOnSucceeded(e -> contentArea.getChildren().setAll(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    // ======= Các hàm chuyển tab UI =======
    @FXML
    public void initialize() {
        // Load Home khi khởi động warehouse staff UI
        loadHome();
    }

    public void loadHome() {
        loadPage("/view/warehousestaff/HomeView.fxml");
    }

    public void loadStockEntryList() {
        loadPage("/view/warehousestaff/StockEntryList.fxml");
    }

    public void loadLossManagement() {
        loadPage("/view/warehousestaff/LossManagementView.fxml");
    }

    public void loadInventoryManagement() {
        loadPage("/view/warehousestaff/InventoryManagementView.fxml");
    }

    public void loadWarehouseReport() {
        loadPage("/view/warehousestaff/WarehouseReportView.fxml");
    }

    // ======= Event Handlers =======
    @FXML
    private void handleHome(ActionEvent event) {
        loadHome();
    }

    @FXML
    private void handleStockEntryList(ActionEvent event) {
        loadStockEntryList();
    }

    @FXML
    private void handleLossManagement(ActionEvent event) {
        loadLossManagement();
    }

    @FXML
    private void handleInventoryManagement(ActionEvent event) {
        loadInventoryManagement();
    }

    @FXML
    private void handleWarehouseReport(ActionEvent event) {
        loadWarehouseReport();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setUserInfo(String name, String role) {
        usernameLabel.setText(name);
        roleLabel.setText(role);
    }
}