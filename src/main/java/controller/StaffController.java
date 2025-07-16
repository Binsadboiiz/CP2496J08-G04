package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    @FXML
    private Button btnHome, btnProducts, btnOrders, btnCustomers, btnLogout;

    @FXML
    private GridPane productGrid;

    @FXML
    private TextField searchField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnHome.setOnAction(e -> loadHome());
        btnProducts.setOnAction(e -> loadProducts());
        btnOrders.setOnAction(e -> loadOrders());
        btnCustomers.setOnAction(e -> loadCustomers());
        btnLogout.setOnAction(e -> logout());

        loadHome(); // Mặc định load Home
    }

    private void loadHome() {
        productGrid.getChildren().clear();
        addSampleCard("Welcome to Staff Dashboard!", "You can manage everything here.");
    }

    private void loadProducts() {
        productGrid.getChildren().clear();
        for (int i = 1; i <= 6; i++) {
            addSampleCard("Sản phẩm " + i, "Giá: 100.000đ\nSL: 10");
        }
    }

    private void loadOrders() {
        productGrid.getChildren().clear();
        for (int i = 1; i <= 4; i++) {
            addSampleCard("Hóa đơn #" + i, "Khách: Nguyễn Văn A\nTổng: 500.000đ");
        }
    }

    private void loadCustomers() {
        productGrid.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            addSampleCard("Khách hàng " + i, "SĐT: 0123456789");
        }
    }

    private void addSampleCard(String title, String content) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: white;");
        card.setPrefSize(200, 100);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label lblContent = new Label(content);

        card.getChildren().addAll(lblTitle, lblContent);

        int col = productGrid.getChildren().size() % 3;
        int row = productGrid.getChildren().size() / 3;
        productGrid.add(card, col, row);
    }

    private void logout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
