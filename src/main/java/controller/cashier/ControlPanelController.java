package controller.cashier;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ControlPanelController {

    public void CashierPaneController() {
        // Constructor rỗng, JavaFX cần cái này để load
    }
    @FXML
    private Button btnPromotionManagement;
    @FXML
    private Button btnReturnPolicy;
    @FXML
    private Button btnRevenueReports;
    @FXML
    private Button btnCalculateSalary;

    // Các phương thức xử lý sự kiện cho các nút
    @FXML
    private void loadPromotionManagement() {
        // Logic để tải giao diện quản lý khuyến mãi
    }

    @FXML
    private void loadReturnPolicy() {
        // Logic để tải giao diện đổi trả sản phẩm
    }

    @FXML
    private void loadRevenueReports() {
        // Logic để tải giao diện báo cáo doanh thu
    }

    @FXML
    private void calculateSalary() {
        // Logic để tải giao diện tính lương
    }
}
