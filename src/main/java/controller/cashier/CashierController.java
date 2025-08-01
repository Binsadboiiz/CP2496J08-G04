package controller.cashier;

import dao.TransactionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Transaction;

import java.io.IOException;
import java.net.URL;

public class CashierController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> idColumn;
    @FXML private TableColumn<Transaction, String> productColumn;
    @FXML private TableColumn<Transaction, Double> priceColumn;
    @FXML private TableColumn<Transaction, Integer> quantityColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;

    @FXML private TextField productField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;

    @FXML private AnchorPane contentArea; // Add fx:id="contentArea" in FXML BorderPane center

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTransactions();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadTransactions() {
        ObservableList<Transaction> list = FXCollections.observableArrayList(transactionDAO.getAllTransactions());
        transactionTable.setItems(list);
    }



    @FXML
    private void handleAddTransaction() {
        try {
            String product = productField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            if (transactionDAO.addTransaction(product, price, quantity)) {
                showAlert("Success", "Transaction added successfully", Alert.AlertType.INFORMATION);
                loadTransactions();
                clearFields();
            } else {
                showAlert("Error", "Failed to add transaction", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for price and quantity", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) transactionTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        productField.clear();
        priceField.clear();
        quantityField.clear();
    }

    // Load SalaryHistory.fxml
    @FXML
    private void loadSalaryHistory(javafx.event.ActionEvent event) {
        loadPage("/view/cashier/SalaryHistory.fxml");
    }

    // Load ReturnPolicy.fxml
    @FXML
    private void loadReturnPolicy(javafx.event.ActionEvent event) {
        loadPage("/view/cashier/ReturnPolicy.fxml");
    }

    // Load RevenueReports.fxml
    @FXML
    private void loadRevenueReports(javafx.event.ActionEvent event) {
        loadPage("/view/cashier/RevenueReport.fxml");
    }

    // Load ControlPanelConfig.fxml
    @FXML
    private void loadControlPanelConfig(javafx.event.ActionEvent event) {
        loadPage("/view/cashier/ControlPanelConfig.fxml");
    }

    // Dynamic Load Page Method
    private void loadPage(String page) {
        try {
            URL fileUrl = getClass().getResource(page);
            if (fileUrl != null) {
                AnchorPane pane = FXMLLoader.load(fileUrl);
                contentArea.getChildren().setAll(pane);
            } else {
                System.out.println("Cannot load page: " + page);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
