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
    @FXML private Button filterButton, resetButton;

    @FXML private TableColumn<Invoice, Integer> invoiceIdColumn;
    @FXML private TableColumn<Invoice, String> customerIdColumn; // Sẽ hiển thị tên khách hàng
    @FXML private TableColumn<Invoice, String> productNameColumn; // Thêm cột tên sản phẩm
    @FXML private TableColumn<Invoice, Integer> userIdColumn;
    @FXML private TableColumn<Invoice, String> dateColumn;
    @FXML private TableColumn<Invoice, String> totalAmountColumn;
    @FXML private TableColumn<Invoice, String> discountColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @FXML
    public void initialize() {
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));

        // Thay đổi: hiển thị tên khách hàng thay vì CustomerID
        customerIdColumn.setText("Customer Name"); // Đổi tên cột
        customerIdColumn.setCellValueFactory(cellData -> {
            String customerName = cellData.getValue().getCustomerName();
            return new SimpleStringProperty(customerName != null ? customerName : "Khách vãng lai");
        });

        // Thêm cột hiển thị tên sản phẩm đầu tiên
        productNameColumn.setCellValueFactory(cellData -> {
            String productName = cellData.getValue().getFirstProductName();
            return new SimpleStringProperty(productName != null ? productName : "N/A");
        });

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getDate().toLocalDate().toString());
            }
            return new SimpleStringProperty("");
        });

        // Khởi tạo NumberFormat với Locale của Đức để sử dụng dấu chấm cho phần nghìn
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("de", "DE"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);

        totalAmountColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTotalAmount() != null) {
                return new SimpleStringProperty(numberFormat.format(cellData.getValue().getTotalAmount()) + " VND");
            }
            return new SimpleStringProperty("0 VND");
        });

        discountColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDiscount() != null) {
                return new SimpleStringProperty(numberFormat.format(cellData.getValue().getDiscount()) + " %");
            }
            return new SimpleStringProperty("0 %");
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Thiết lập màu sắc cho các nút
        setupButtonStyles();

        loadInvoices();
    }

    private void setupButtonStyles() {
        // Filter button - #2584f8 (xanh dương như Add)
        if (filterButton != null) {
            filterButton.setStyle("-fx-background-color: #2584f8; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        // Reset button - #3498db (xanh nhạt như Refresh)
        if (resetButton != null) {
            resetButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    private void loadInvoices() {
        // Sử dụng method mới để lấy hóa đơn kèm tên khách hàng và sản phẩm
        List<Invoice> invoices = invoiceDAO.getAllInvoicesWithDetails();
        invoiceTable.setItems(FXCollections.observableArrayList(invoices));
    }

    @FXML
    private void handleFilter() {
        String customer = customerFilterField.getText();
        String product = productFilterField.getText();
        LocalDate date = dateFilterPicker.getValue();

        // Sử dụng method mới để lọc
        List<Invoice> invoices = invoiceDAO.filterInvoicesWithDetails(customer, product, date, date);
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