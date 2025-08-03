package controller.cashier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDate;

public class CashierController {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button btnLogout;

    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Integer> idColumn;
    @FXML
    private TableColumn<Transaction, String> productColumn;
    @FXML
    private TableColumn<Transaction, Double> priceColumn;
    @FXML
    private TableColumn<Transaction, Integer> quantityColumn;
    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TextField productField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;
    @FXML
    private Button loadPromotionsButton;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (transactionTable != null) {
            idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTransactionID()).asObject());
            productColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName()));
            priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
            quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
            dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));

            transactionTable.setItems(transactionList);
        }
    }

    @FXML
    public void handleAddTransaction() {
        try {
            String productName = productField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            Transaction t = new Transaction(transactionList.size() + 1, productName, price, quantity, LocalDate.now());
            transactionList.add(t);

            productField.clear();
            priceField.clear();
            quantityField.clear();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully!");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values for price and quantity.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void loadTransactions() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/view/cashier/TransactionView.fxml"));
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadSalaryHistory() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/view/cashier/SalaryHistory.fxml"));
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadPromotion() {
        loadPage("/view/cashier/PromotionManagement.fxml");
    }

    @FXML
    public void loadReturnPolicy() {
        loadPage("/view/cashier/ReturnPolicy.fxml");
    }

    @FXML
    public void loadRevenueReports() {
        loadPage("/view/cashier/RevenueReports.fxml");
    }

    @FXML
    public void logout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button myButton; // Đặt fx:id cho nút trong FXML

    private void loadPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) myButton.getScene().getWindow(); // Sử dụng myButton thay cho someNode
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
