package controller.staff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceHistoryController {

    @FXML private TextField customerFilterField;
    @FXML private TextField productFilterField;
    @FXML private DatePicker dateFilterPicker;

    @FXML private TableView<InvoiceRecord> invoiceTable;
    @FXML private TableColumn<InvoiceRecord, String> invoiceIdColumn;
    @FXML private TableColumn<InvoiceRecord, String> customerNameColumn;
    @FXML private TableColumn<InvoiceRecord, String> productListColumn;
    @FXML private TableColumn<InvoiceRecord, String> dateColumn;
    @FXML private TableColumn<InvoiceRecord, Double> totalColumn;

    private ObservableList<InvoiceRecord> allInvoices = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        invoiceIdColumn.setCellValueFactory(data -> data.getValue().invoiceIdProperty());
        customerNameColumn.setCellValueFactory(data -> data.getValue().customerNameProperty());
        productListColumn.setCellValueFactory(data -> data.getValue().productListProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        totalColumn.setCellValueFactory(data -> data.getValue().totalAmountProperty().asObject());

        // Giả lập dữ liệu hóa đơn
        allInvoices.addAll(InvoiceRecord.sampleData());
        invoiceTable.setItems(allInvoices);
    }

    @FXML
    private void handleFilter() {
        String customer = customerFilterField.getText().trim().toLowerCase();
        String product = productFilterField.getText().trim().toLowerCase();
        LocalDate date = dateFilterPicker.getValue();

        List<InvoiceRecord> filtered = allInvoices.stream()
                .filter(i -> i.getCustomerName().toLowerCase().contains(customer))
                .filter(i -> i.getProductList().toLowerCase().contains(product))
                .filter(i -> date == null || i.getDate().equals(date.toString()))
                .collect(Collectors.toList());

        invoiceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {
        customerFilterField.clear();
        productFilterField.clear();
        dateFilterPicker.setValue(null);
        invoiceTable.setItems(allInvoices);
    }
}
