package controller.staff;

import dao.InvoiceDAO;
import model.Invoice;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;
import java.text.NumberFormat;
import java.util.Locale;

public class InvoiceHistoryController {
    @FXML private TableView<Invoice> invoiceTable;
    @FXML private DatePicker dateFilterPicker;
    @FXML private TextField customerFilterField, productFilterField;
    @FXML private TableColumn<Invoice, Integer> invoiceIdColumn;
    @FXML private TableColumn<Invoice, Integer> customerIdColumn;
    @FXML private TableColumn<Invoice, Integer> userIdColumn;
    @FXML private TableColumn<Invoice, String> dateColumn;
    @FXML private TableColumn<Invoice, String> totalAmountColumn;
    @FXML private TableColumn<Invoice, String> discountColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @FXML
    public void initialize() {
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate().toLocalDate().toString())
        );

        // Khởi tạo NumberFormat với Locale của Đức để sử dụng dấu chấm cho phần nghìn
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("de", "DE"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);

        totalAmountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(numberFormat.format(cellData.getValue().getTotalAmount()) + " VND")
        );
        discountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(numberFormat.format(cellData.getValue().getDiscount()) + " VND")
        );
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadInvoices();
    }

    private void loadInvoices() {
        invoiceTable.setItems(FXCollections.observableArrayList(invoiceDAO.getAllInvoices()));
    }

    @FXML
    private void handleFilter() {
        String customer = customerFilterField.getText();
        String product = productFilterField.getText();
        LocalDate date = dateFilterPicker.getValue();

        List<Invoice> invoices = invoiceDAO.filterInvoices(customer, product, date, date);
        invoiceTable.setItems(FXCollections.observableArrayList(invoices));
    }

    @FXML
    private void handleReset() {
        customerFilterField.clear();
        productFilterField.clear();
        dateFilterPicker.setValue(null);
        loadInvoices();
    }
}