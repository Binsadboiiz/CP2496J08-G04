package controller.warehousestaff;

import dao.InventoryDAO;
import dao.ProductDAO;
import dao.StockEntryDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {

    private static final int LOW_STOCK_THRESHOLD = 10;

    // FXML Elements
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalStockEntriesLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load dữ liệu thống kê
        loadHomeDataAsync();

        // Thêm hiệu ứng fade in
        addFadeInEffect(totalProductsLabel);
        addFadeInEffect(lowStockLabel);
        addFadeInEffect(totalStockEntriesLabel);
    }

    /**
     * Load dữ liệu trang chủ bất đồng bộ
     */
    private void loadHomeDataAsync() {
        Task<int[]> task = new Task<int[]>() {
            @Override
            protected int[] call() throws Exception {
                try {
                    // Lấy tổng số sản phẩm
                    int totalProducts = ProductDAO.getTotalProducts();

                    // Lấy số sản phẩm sắp hết hàng (≤ 10)
                    int lowStock = InventoryDAO.getStockAlerts(LOW_STOCK_THRESHOLD);

                    // Lấy tổng số phiếu nhập hàng
                    int totalEntries = StockEntryDAO.getAll().size();

                    return new int[]{totalProducts, lowStock, totalEntries};

                } catch (Exception e) {
                    e.printStackTrace();
                    // Trả về giá trị mặc định nếu có lỗi
                    return new int[]{0, 0, 0};
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    int[] data = getValue();
                    updateLabels(data[0], data[1], data[2]);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showErrorAlert("Không thể tải dữ liệu trang chủ. Hiển thị giá trị mặc định.");
                    updateLabels(0, 0, 0);
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Cập nhật các label với dữ liệu mới
     */
    private void updateLabels(int totalProducts, int lowStock, int totalEntries) {
        if (totalProductsLabel != null) {
            totalProductsLabel.setText(String.valueOf(totalProducts));
        }
        if (lowStockLabel != null) {
            lowStockLabel.setText(String.valueOf(lowStock));
        }
        if (totalStockEntriesLabel != null) {
            totalStockEntriesLabel.setText(String.valueOf(totalEntries));
        }
    }

    /**
     * Thêm hiệu ứng fade in cho node
     */
    private void addFadeInEffect(Node node) {
        if (node != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(1000), node);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    // ==================== 4 CHỨC NĂNG CHÍNH ====================

    /**
     * Xử lý chức năng Quản lý tồn kho
     */
    @FXML
    private void handleInventoryManagement(MouseEvent event) {
        addClickEffect((Node) event.getSource());

        showInfoAlert("📦 Quản lý tồn kho",
                "Chức năng này sẽ cho phép bạn:\n" +
                        "• Xem danh sách sản phẩm trong kho\n" +
                        "• Kiểm tra số lượng tồn kho\n" +
                        "• Theo dõi sản phẩm sắp hết hàng\n" +
                        "• Cập nhật thông tin tồn kho");
    }

    /**
     * Xử lý chức năng Cập nhật sản phẩm
     */
    @FXML
    private void handleProductUpdate(MouseEvent event) {
        addClickEffect((Node) event.getSource());

        showInfoAlert("✏️ Cập nhật sản phẩm",
                "Chức năng này sẽ cho phép bạn:\n" +
                        "• Chỉnh sửa thông tin sản phẩm\n" +
                        "• Cập nhật giá và mô tả\n" +
                        "• Quản lý hình ảnh sản phẩm");
    }

    /**
     * Xử lý chức năng Phiếu nhập hàng
     */
    @FXML
    private void handleStockEntry(MouseEvent event) {
        addClickEffect((Node) event.getSource());

        showInfoAlert("📋 Phiếu nhập hàng",
                "Chức năng này sẽ cho phép bạn:\n" +
                        "• Tạo phiếu nhập hàng mới\n" +
                        "• Xem lịch sử các phiếu nhập\n" +
                        "• Quản lý thông tin nhà cung cấp ");
    }

    /**
     * Xử lý chức năng Báo cáo hao hụt
     */
    @FXML
    private void handleLossManagement(MouseEvent event) {
        addClickEffect((Node) event.getSource());

        showInfoAlert("⚠️ Báo cáo hao hụt",
                "Chức năng này sẽ cho phép bạn:\n" +
                        "• Ghi nhận hàng hóa bị hao hụt\n" +
                        "• Theo dõi nguyên nhân hao hụt\n" +
                        "• Tạo báo cáo thống kê\n" +
                        "• Phân tích xu hướng hao hụt");
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Thêm hiệu ứng click cho node
     */
    private void addClickEffect(Node node) {
        if (node != null) {
            // Hiệu ứng scale khi click
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.play();
        }
    }

    /**
     * Hiển thị thông báo lỗi
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo thông tin với tiêu đề tùy chỉnh
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin chức năng");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }

    /**
     * Refresh dữ liệu trang chủ
     */
    public void refreshData() {
        loadHomeDataAsync();
    }
}