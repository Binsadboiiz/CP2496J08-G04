package controller.cashier;

import dao.TransactionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Transaction;
import java.time.LocalDate;
import java.util.List;

public class TransactionsController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> idColumn;
    @FXML private TableColumn<Transaction, String> customerColumn;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, Double> totalAmountColumn;

    @FXML private TextField productField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        transactionTable.setItems(transactionList);
        loadTransactions();
    }

    private void loadTransactions() {
        transactionList.clear();  // Clear dữ liệu cũ
        List<Transaction> list = TransactionDAO.getAllTransactions();
        transactionList.addAll(list);  // Add lại từ DB
    }

    private void clearInputFields() {
        productField.clear();
        priceField.clear();
        quantityField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAddTransaction(ActionEvent actionEvent) {
        String productName = productField.getText();
        String priceText = priceField.getText();
        String quantityText = quantityField.getText();

        if (productName.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            Transaction newTransaction = new Transaction(
                    transactionTable.getItems().size() + 1,
                    productName,
                    price,
                    quantity,
                    LocalDate.now()
            );

            transactionTable.getItems().add(newTransaction);
            clearInputFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully!");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price and Quantity must be numbers.");
        }
    }

    // Đây là hàm sẽ gọi từ CashierController.loadPage()
    public void reloadData() {
        loadTransactions();  // Load lại dữ liệu mỗi khi được load vào contentArea
    }
}
