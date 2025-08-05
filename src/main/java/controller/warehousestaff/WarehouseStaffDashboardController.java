package controller.warehousestaff;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class WarehouseStaffDashboardController {

    @FXML
    private StackPane contentArea;

    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void handleRole(ActionEvent event) {
        loadView("/view/warehousestaff/RoleView.fxml");
    }

    @FXML
    private void handleStockEntryList(ActionEvent event) {loadView("/view/warehousestaff/StockEntryList.fxml");}

    @FXML
    private void handleLossManagement(ActionEvent event) {
        loadView("/view/warehousestaff/LossManagementView.fxml");
    }

    @FXML
    private void handleInventoryManagement(ActionEvent event) {
        loadView("/view/warehousestaff/InventoryManagementView.fxml");
    }

    @FXML
    private void handleProductUpdate(ActionEvent event) {
        loadView("/view/warehousestaff/ProductUpdateView.fxml");
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
}
