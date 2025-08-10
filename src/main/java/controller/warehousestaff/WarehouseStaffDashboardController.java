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

    // Thêm các button để quản lý trạng thái active
    @FXML
    private Button btnHome;
    @FXML
    private Button btnStockEntryList;
    @FXML
    private Button btnLossManagement;
    @FXML
    private Button btnInventoryManagement;
    @FXML
    private Button btnWarehouseReport;

    // Biến lưu trạng thái trang hiện tại
    private String currentPage = "/view/warehousestaff/HomeView.fxml";
    private Button currentActiveButton;

    // Biến để ngăn chặn auto-navigation không mong muốn
    private boolean preventAutoNavigation = false;
    private long lastNavigationTime = 0;

    public void loadPage(String fxmlPath) {
        // Cập nhật trang hiện tại
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

                // Nếu có controller con, có thể truyền reference của dashboard controller
                Object controller = loader.getController();
                if (controller != null && hasSetDashboardMethod(controller)) {
                    // Sử dụng reflection để set dashboard controller nếu method tồn tại
                    try {
                        controller.getClass().getMethod("setDashboardController",
                                        WarehouseStaffDashboardController.class)
                                .invoke(controller, WarehouseStaffDashboardController.this);
                    } catch (Exception e) {
                        // Method không tồn tại, bỏ qua
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

    // Helper method để kiểm tra xem controller có method setDashboardController không
    private boolean hasSetDashboardMethod(Object controller) {
        try {
            controller.getClass().getMethod("setDashboardController",
                    WarehouseStaffDashboardController.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    // Method để cập nhật trạng thái active button
    private void setActiveButton(Button button) {
        // Xóa class active từ tất cả buttons
        if (btnHome != null) btnHome.getStyleClass().remove("active-button");
        if (btnStockEntryList != null) btnStockEntryList.getStyleClass().remove("active-button");
        if (btnLossManagement != null) btnLossManagement.getStyleClass().remove("active-button");
        if (btnInventoryManagement != null) btnInventoryManagement.getStyleClass().remove("active-button");
        if (btnWarehouseReport != null) btnWarehouseReport.getStyleClass().remove("active-button");

        // Thêm class active cho button được chọn
        if (button != null) {
            if (!button.getStyleClass().contains("active-button")) {
                button.getStyleClass().add("active-button");
            }
            currentActiveButton = button;
        }
    }

    // Method công khai để reload trang hiện tại (không thay đổi navigation)
    public void refreshCurrentPage() {
        loadPage(currentPage);
        // Giữ nguyên button active
        if (currentActiveButton != null) {
            setActiveButton(currentActiveButton);
        }
    }

    // Method để stay ở trang hiện tại sau khi thực hiện action
    public void stayCurrentPage() {
        // Chỉ cần giữ nguyên active button, không reload
        if (currentActiveButton != null) {
            setActiveButton(currentActiveButton);
        }
    }

    @FXML
    public void initialize() {
        loadHome();
    }

    public void loadHome() {
        // Kiểm tra xem có phải là auto-navigation không mong muốn không
        long currentTime = System.currentTimeMillis();
        if (preventAutoNavigation && (currentTime - lastNavigationTime) < 2000) {
            // Nếu vừa mới navigate (trong vòng 2 giây) thì bỏ qua
            System.out.println("Prevented auto-navigation to Home");
            setActiveButton(currentActiveButton); // Giữ nguyên button active
            return;
        }

        loadPage("/view/warehousestaff/HomeView.fxml");
        setActiveButton(btnHome);
        lastNavigationTime = currentTime;
    }

    public void loadStockEntryList() {
        loadPage("/view/warehousestaff/StockEntry.fxml");
        setActiveButton(btnStockEntryList);
        preventAutoNavigation = true; // Bật chế độ ngăn chặn auto-nav
        lastNavigationTime = System.currentTimeMillis();
    }

    public void loadLossManagement() {
        loadPage("/view/warehousestaff/LossManagementView.fxml");
        setActiveButton(btnLossManagement);
        preventAutoNavigation = true;
        lastNavigationTime = System.currentTimeMillis();
    }

    public void loadInventoryManagement() {
        loadPage("/view/warehousestaff/InventoryManagementView.fxml");
        setActiveButton(btnInventoryManagement);
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
        preventAutoNavigation = false; // Tắt chế độ ngăn chặn khi click thủ công
        loadHome();
    }

    @FXML
    private void handleStockEntryList(ActionEvent event) {
        preventAutoNavigation = false; // Reset trạng thái khi click thủ công
        loadStockEntryList();
    }

    @FXML
    private void handleLossManagement(ActionEvent event) {
        preventAutoNavigation = false;
        loadLossManagement();
    }

    @FXML
    private void handleInventoryManagement(ActionEvent event) {
        preventAutoNavigation = false;
        loadInventoryManagement();
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

    // Getter cho các controller con có thể sử dụng
    public String getCurrentPage() {
        return currentPage;
    }

    // Method để các controller con có thể gọi khi cần reload dashboard
    public void reloadDashboard() {
        // Chỉ reload khi thực sự cần thiết
        refreshCurrentPage();
    }
}