package controller.staff;

import dao.CustomerDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import model.Product;
import dao.InvoiceDAO;
import dao.InvoiceDetailDAO;
import dao.ProductDAO; // Import ProductDAO
import model.Invoice;
import model.InvoiceDetail;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import dao.PromotionDAO;
import model.Promotion;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

import java.sql.Connection;


public class CreateInvoiceController {

    @FXML private TextField productNameField;
    @FXML private TextField quantityField;
    @FXML private TextField discountField;
    @FXML private TableView<InvoiceItem> invoiceTable;
    @FXML private TableColumn<InvoiceItem, String> productNameColumn;
    @FXML private TableColumn<InvoiceItem, Integer> quantityColumn;
    @FXML private TableColumn<InvoiceItem, Double> priceColumn;
    @FXML private TableColumn<InvoiceItem, Double> totalColumn;
    @FXML private Label totalLabel;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Promotion> comboDiscount;

    private double originalTotal = 0;


    private ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO invoiceDetailDAO = new InvoiceDetailDAO();
//    private final ProductDAO productDAO = new ProductDAO(); // Khai báo ProductDAO

    private StaffController staffController;

    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    private static ObservableList<Product> selectedProducts = FXCollections.observableArrayList();

    public static void addProductToInvoice(Product product) {
        selectedProducts.add(product);
    }

    @FXML
    public void initialize() {
        loadCustomers();

        // Mapping data cho table
        productNameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));

        // SỬA ĐỔI: Định dạng các cột "Đơn giá" và "Thành tiền"
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setGroupingUsed(true);

        priceColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(tc -> new TableCell<InvoiceItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(numberFormat.format(price) + " VND");
                }
            }
        });

        totalColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));
        totalColumn.setCellFactory(tc -> new TableCell<InvoiceItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(numberFormat.format(total) + " VND");
                }
            }
        });

        if (!selectedProducts.isEmpty()) {
            for (Product product : selectedProducts) {
                invoiceItems.add(new InvoiceItem(product.getProductName(), 1, product.getPrice(), product.getProductID()));
            }
            selectedProducts.clear();
        }

        invoiceTable.setItems(invoiceItems);
        calculateAndDisplayTotal();

        comboDiscount.setItems(FXCollections.observableArrayList(PromotionDAO.getAll()));

        comboDiscount.setConverter(new StringConverter<Promotion>() {
            @Override
            public String toString(Promotion promo) {
                return promo != null ? promo.getPromotionName() + " (" + (promo.getDiscountPercentage()) + "%)" : "";
            }

            @Override
            public Promotion fromString(String string) {
                return null;
            }
        });

        comboDiscount.setOnAction(event -> applyDiscount());

    }

    private void loadCustomers() {
        List<Customer> customerList = customerDAO.getAllCustomers();
        ObservableList<Customer> customers = FXCollections.observableArrayList(customerList);
        customerComboBox.setItems(customers);
        customerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                setText(empty ? "" : customer.getFullName());
            }
        });
        customerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                setText(empty ? "" : customer.getFullName());
            }
        });
    }

    @FXML
    private void handleAddProduct() {
        String name = productNameField.getText();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng phải là số nguyên.");
            return;
        }

        if (name.isEmpty() || quantity <= 0) {
            showAlert("Lỗi", "Vui lòng nhập thông tin hợp lệ.");
            return;
        }

        Product product = ProductDAO.getProductByName(name);
        if (product == null) {
            showAlert("Lỗi", "Sản phẩm không tồn tại.");
            return;
        }

        invoiceItems.add(new InvoiceItem(product.getProductName(), quantity, product.getPrice(), product.getProductID()));
        productNameField.clear();
        quantityField.clear();
        calculateAndDisplayTotal();
    }

    @FXML
    private void handleCalculateTotal() {
        calculateAndDisplayTotal();
    }

    private void calculateAndDisplayTotal() {
        BigDecimal subTotal = invoiceItems.stream()
                .map(item -> BigDecimal.valueOf(item.getTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double discountPercent = 0.0;
        if (!discountField.getText().isEmpty()) {
            try {
                discountPercent = Double.parseDouble(discountField.getText());
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "Giảm giá phải là một số hợp lệ!");
                discountPercent = 0.0;
            }
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        BigDecimal total = subTotal.multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP);

        // SỬA ĐỔI: Định dạng tổng tiền trên Label
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setGroupingUsed(true);
        String formattedTotal = numberFormat.format(total.doubleValue());
        totalLabel.setText("Tổng tiền: " + formattedTotal + " VND");
    }

    @FXML
    private void handleSaveInvoice() {
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            showAlert("Lỗi", "Vui lòng chọn một khách hàng.");
            return;
        }

        calculateAndDisplayTotal();

        Invoice newInvoice = new Invoice();
        newInvoice.setCustomerID(selectedCustomer.getCustomerID());
        newInvoice.setUserID(1); // TODO: Lấy ID nhân viên thực tế từ phiên đăng nhập
        newInvoice.setDate(LocalDateTime.now());
        newInvoice.setTotalAmount(BigDecimal.valueOf(calculateTotalAmount()));
        newInvoice.setDiscount(BigDecimal.valueOf(calculateDiscount()));
        newInvoice.setStatus("Hoàn thành");

        int newInvoiceID = invoiceDAO.insertInvoice(newInvoice);

        if (newInvoiceID > 0) {
            for (InvoiceItem item : invoiceItems) {
                InvoiceDetail detail = new InvoiceDetail(
                        newInvoiceID,
                        item.getProductID(),
                        item.getQuantity(),
                        BigDecimal.valueOf(item.getPrice()),
                        BigDecimal.ZERO
                );
                invoiceDetailDAO.insertInvoiceDetail(detail);
            }

            showAlert("Thành công", "Hóa đơn đã được tạo và lưu thành công!");
            if (staffController != null) {
                try {
                    staffController.loadInvoiceHistory();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Lỗi", "Không thể chuyển trang Lịch sử hóa đơn.");
                }
            }
        } else {
            showAlert("Lỗi", "Không thể lưu hóa đơn. Vui lòng kiểm tra lại.");
        }
    }

    @FXML
    private void handleReset() {
        invoiceItems.clear();
        discountField.clear();
        totalLabel.setText("Tổng tiền: 0 VND");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private double calculateTotalAmount() {
        double subTotal = invoiceItems.stream().mapToDouble(InvoiceItem::getTotal).sum();
        double discount = calculateDiscount();
        return subTotal - subTotal * (discount / 100);
    }
    private void updateTotal() {
        originalTotal = invoiceItems.stream().mapToDouble(InvoiceItem::getTotal).sum();
        applyDiscount();
    }


    private double calculateDiscount() {
        double discount = 0;
        try {
            if (!discountField.getText().isEmpty()) {
                discount = Double.parseDouble(discountField.getText());
            }
        } catch (NumberFormatException ignored) {}
        return discount;
    }

    // Inner class đại diện cho 1 sản phẩm trong hóa đơn
    public static class InvoiceItem {
        private final String productName;
        private final int quantity;
        private final double price;
        private final int productID; // Thêm trường productID

        public InvoiceItem(String productName, int quantity, double price, int productID) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.productID = productID;
        }

        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getTotal() { return quantity * price; }
        public int getProductID() { return productID; }
    }

    private void applyDiscount() {
        Promotion selected = comboDiscount.getValue();
        if (selected != null) {
            discountField.setText(String.valueOf(selected.getDiscountPercentage()));
        } else {
            discountField.clear();
        }

        calculateAndDisplayTotal(); // Cập nhật lại tổng tiền
    }


}