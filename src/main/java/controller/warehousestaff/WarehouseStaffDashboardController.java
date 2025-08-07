package controller.warehousestaff;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import dao.UserDAO;

import java.io.IOException;

public class WarehouseStaffDashboardController {

    @FXML
    private StackPane contentArea;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;

    // ===== TỐI ƯU LOADVIEW VỚI LOADING =====
    private void loadView(String fxmlFile) {
        ProgressIndicator pi = new ProgressIndicator();
        StackPane loadingPane = new StackPane(pi);
        loadingPane.setPrefSize(contentArea.getWidth(), contentArea.getHeight());
        contentArea.getChildren().setAll(loadingPane);

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                return FXMLLoader.load(getClass().getResource(fxmlFile));
            }
        };
        task.setOnSucceeded(e -> contentArea.getChildren().setAll(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    public void initialize() {
        loadView("/view/warehousestaff/HomeView.fxml");
    }

    @FXML
    private void handleHome(ActionEvent event) {
        loadView("/view/warehousestaff/HomeView.fxml");
    }

    @FXML
    private void handleStockEntryList(ActionEvent event) {
        loadView("/view/warehousestaff/StockEntryList.fxml");
    }

    @FXML
    private void handleLossManagement(ActionEvent event) {
        loadView("/view/warehousestaff/LossManagementView.fxml");
    }

    @FXML
    private void handleInventoryManagement(ActionEvent event) {
        loadView("/view/warehousestaff/InventoryManagementView.fxml");
    }

    @FXML
    private void handleWarehouseReport(ActionEvent event) {
        loadView("/view/warehousestaff/WarehouseReportView.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Parent loginView = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            source.getScene().setRoot(loginView);
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
