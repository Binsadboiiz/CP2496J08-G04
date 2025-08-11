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

    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalStockEntriesLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHomeDataAsync();
        addFadeInEffect(totalProductsLabel);
        addFadeInEffect(lowStockLabel);
        addFadeInEffect(totalStockEntriesLabel);
    }

    private void loadHomeDataAsync() {
        Task<int[]> task = new Task<int[]>() {
            @Override
            protected int[] call() throws Exception {
                try {
                    int totalProducts = ProductDAO.getTotalProducts();
                    int lowStock = InventoryDAO.getStockAlerts(LOW_STOCK_THRESHOLD);
                    int totalEntries = StockEntryDAO.getAll().size();

                    return new int[]{totalProducts, lowStock, totalEntries};
                } catch (Exception e) {
                    e.printStackTrace();
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
                    showErrorAlert("Unable to load data. Displaying default values.");
                    updateLabels(0, 0, 0);
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

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

    private void addFadeInEffect(Node node) {
        if (node != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(800), node);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    @FXML
    private void handleInventoryManagement(MouseEvent event) {
        addClickEffect((Node) event.getSource());
        showInfoAlert("Inventory Management",
                "View product list in warehouse\n" +
                        "Check inventory quantities\n" +
                        "Monitor low stock products");
    }

    @FXML
    private void handleStockEntry(MouseEvent event) {
        addClickEffect((Node) event.getSource());
        showInfoAlert("Stock Entry",
                "Create new stock entries\n" +
                        "View stock entry history");
    }

    @FXML
    private void handleLossManagement(MouseEvent event) {
        addClickEffect((Node) event.getSource());
        showInfoAlert("Loss Report",
                "Record damaged or lost goods\n" +
                        "Track loss reasons\n" +
                        "Generate statistical reports");
    }

    private void addClickEffect(Node node) {
        if (node != null) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(120), node);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(0.96);
            scale.setToY(0.96);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.play();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Function Information");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(350);
        alert.showAndWait();
    }

    public void refreshData() {
        loadHomeDataAsync();
    }
}