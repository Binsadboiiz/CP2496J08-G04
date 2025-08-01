package controller.staff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CreateInvoiceController {

    @FXML private TextField productNameField;
    @FXML private TextField quantityField;
    @FXML private TextField discountField;
    @FXML private Label totalLabel;

    @FXML private TableView<InvoiceItem> invoiceTable;
    @FXML private TableColumn<InvoiceItem, String> productNameColumn;
    @FXML private TableColumn<InvoiceItem, Integer> quantityColumn;
    @FXML private TableColumn<InvoiceItem, Double> priceColumn;
    @FXML private TableColumn<InvoiceItem, Double> totalColumn;

    private ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        invoiceTable.setItems(invoiceItems);
    }

    @FXML
    private void handleAddProduct() {
        String name = productNameField.getText();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Số lượng phải là số nguyên!");
            return;
        }

        double unitPrice = getProductPrice(name); // tạm thời fix cứng
        double total = unitPrice * quantity;
        invoiceItems.add(new InvoiceItem(name, quantity, unitPrice, total));

        productNameField.clear();
        quantityField.clear();
    }

    private double getProductPrice(String name) {
        return 100000; // Giả định tạm sản phẩm nào cũng 100,000 VND
    }

    @FXML
    private void handleCalculateTotal() {
        double sum = invoiceItems.stream().mapToDouble(InvoiceItem::getTotalPrice).sum();

        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException ignored) {}

        double finalTotal = sum * (1 - discount / 100);
        totalLabel.setText("Tổng tiền: " + String.format("%,.0f VND", finalTotal));
    }

    @FXML
    private void handleSaveInvoice() {
        showAlert("Đã lưu hóa đơn thành công!");
        // TODO: Lưu vào database hoặc file
    }

    @FXML
    private void handleReset() {
        productNameField.clear();
        quantityField.clear();
        discountField.clear();
        invoiceItems.clear();
        totalLabel.setText("Tổng tiền: 0 VND");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
