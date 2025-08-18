package controller.warehousestaff;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @FXML
    private Button btnHome;
    @FXML
    private Button btnStockEntryList;
    @FXML
    private Button btnLossReport;
    @FXML
    private Button btnInventoryReport;
    @FXML
    private Button btnWarehouseReport;

    private String currentPage = "/view/warehousestaff/HomeView.fxml";
    private Button currentActiveButton;

    private boolean preventAutoNavigation = false;
    private long lastNavigationTime = 0;

    public void loadPage(String fxmlPath) {
        currentPage = fxmlPath;

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

                Object controller = loader.getController();
                if (controller != null && hasSetDashboardMethod(controller)) {
                    try {
                        controller.getClass().getMethod("setDashboardController",
                                        WarehouseStaffDashboardController.class)
                                .invoke(controller, WarehouseStaffDashboardController.this);
                    } catch (Exception e) {
                        System.out.println("Controller " + controller.getClass().getSimpleName() +
                                " doesn't have setDashboardController method");
                    }
                }

                return fxml;
            }
        };
        task.setOnSucceeded(e -> contentArea.getChildren().setAll(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private boolean hasSetDashboardMethod(Object controller) {
        try {
            controller.getClass().getMethod("setDashboardController",
                    WarehouseStaffDashboardController.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void setActiveButton(Button button) {
        if (btnHome != null) btnHome.getStyleClass().remove("active-button");
        if (btnStockEntryList != null) btnStockEntryList.getStyleClass().remove("active-button");
        if (btnLossReport != null) btnLossReport.getStyleClass().remove("active-button");
        if (btnInventoryReport != null) btnInventoryReport.getStyleClass().remove("active-button");
        if (btnWarehouseReport != null) btnWarehouseReport.getStyleClass().remove("active-button");

        if (button != null) {
            if (!button.getStyleClass().contains("active-button")) {
                button.getStyleClass().add("active-button");
            }
            currentActiveButton = button;
        }
    }

    public void refreshCurrentPage() {
        loadPage(currentPage);
        if (currentActiveButton != null) {
            setActiveButton(currentActiveButton);
        }
    }

    public void stayCurrentPage() {
        if (currentActiveButton != null) {
            setActiveButton(currentActiveButton);
        }
    }

    @FXML
    public void initialize() {
        loadHome();
    }

    public void loadHome() {
        long currentTime = System.currentTimeMillis();
        if (preventAutoNavigation && (currentTime - lastNavigationTime) < 2000) {
            System.out.println("Prevented auto-navigation to Home");
            setActiveButton(currentActiveButton);
            return;
        }

        loadPage("/view/warehousestaff/HomeView.fxml");
        setActiveButton(btnHome);
        lastNavigationTime = currentTime;
    }

    public void loadStockEntryList() {
        loadPage("/view/warehousestaff/StockEntry.fxml");
        setActiveButton(btnStockEntryList);
        preventAutoNavigation = true;
        lastNavigationTime = System.currentTimeMillis();
    }

    public void loadLossReport() {
        loadPage("/view/warehousestaff/LossReport.fxml");
        setActiveButton(btnLossReport);
        preventAutoNavigation = true;
        lastNavigationTime = System.currentTimeMillis();
    }

    public void loadInventoryReport() {
        loadPage("/view/warehousestaff/InventoryReport.fxml");
        setActiveButton(btnInventoryReport);
        preventAutoNavigation = true;
        lastNavigationTime = System.currentTimeMillis();
    }

    public void loadWarehouseReport() {
        loadPage("/view/warehousestaff/WarehouseReport.fxml");
        setActiveButton(btnWarehouseReport);
        preventAutoNavigation = true;
        lastNavigationTime = System.currentTimeMillis();
    }

    @FXML
    private void handleHome(ActionEvent event) {
        preventAutoNavigation = false;
        loadHome();
    }

    @FXML
    private void handleStockEntryList(ActionEvent event) {
        preventAutoNavigation = false;
        loadStockEntryList();
    }

    @FXML
    private void handleLossReport(ActionEvent event) {
        preventAutoNavigation = false;
        loadLossReport();
    }

    @FXML
    private void handleInventoryReport(ActionEvent event) {
        preventAutoNavigation = false;
        loadInventoryReport();
    }

    @FXML
    private void handleWarehouseReport(ActionEvent event) {
        preventAutoNavigation = false;
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
        if (usernameLabel != null) {
            usernameLabel.setText(name);
        }
        if (roleLabel != null) {
            roleLabel.setText(role);
        }
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void reloadDashboard() {
        refreshCurrentPage();
    }
}