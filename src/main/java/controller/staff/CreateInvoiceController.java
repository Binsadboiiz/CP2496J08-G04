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
import dao.ProductDAO;
import model.Invoice;
import model.InvoiceDetail;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import dao.PromotionDAO;
import model.Promotion;
import javafx.util.StringConverter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.cell.CheckBoxListCell;

public class CreateInvoiceController {

    @FXML private ListView<Promotion> promotionListView;
    private final ObservableList<Promotion> selectedPromotions = FXCollections.observableArrayList();

    // Thay đổi từ TextField thành ComboBox cho sản phẩm
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField discountField;
    @FXML private TableView<InvoiceItem> invoiceTable;
    @FXML private TableColumn<InvoiceItem, String> productNameColumn;
    @FXML private TableColumn<InvoiceItem, Integer> quantityColumn;
    @FXML private TableColumn<InvoiceItem, Double> priceColumn;
    @FXML private TableColumn<InvoiceItem, Double> totalColumn;
    @FXML private Label totalLabel;

    // ComboBox khách hàng với autocomplete
    @FXML private ComboBox<Customer> customerComboBox;

    // THÊM Label hiển thị tồn kho
    @FXML private Label stockLabel;

    private double originalTotal = 0;

    private ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO invoiceDetailDAO = new InvoiceDetailDAO();

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
        loadProducts();
        setupCustomerFeatures();
        setupProductFeatures();
        setupTableColumns();
        setupPromotionList();

        // Load pre-selected products
        if (!selectedProducts.isEmpty()) {
            for (Product product : selectedProducts) {
                invoiceItems.add(new InvoiceItem(product.getProductName(), 1, product.getPrice(), product.getProductID()));
            }
            selectedProducts.clear();
        }

        invoiceTable.setItems(invoiceItems);
        calculateAndDisplayTotal();
    }

    private void setupTableColumns() {
        // Mapping data cho table
        productNameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));

        // Định dạng các cột "Đơn giá" và "Thành tiền"
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
    }

    private void setupPromotionList() {
        ObservableList<Promotion> allPromotions = FXCollections.observableArrayList(PromotionDAO.getAll());
        promotionListView.setItems(allPromotions);

        // Hiển thị checkbox cho mỗi khuyến mãi
        promotionListView.setCellFactory(CheckBoxListCell.forListView(promo -> {
            BooleanProperty selected = new SimpleBooleanProperty();
            selected.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    selectedPromotions.add(promo);
                } else {
                    selectedPromotions.remove(promo);
                }
                applyMultipleDiscounts();
            });
            return selected;
        }, new StringConverter<Promotion>() {
            @Override
            public String toString(Promotion promo) {
                return promo.getPromotionName() + " (" + promo.getDiscountPercentage() + "%)";
            }

            @Override
            public Promotion fromString(String string) {
                return null;
            }
        }));
    }

    private void loadCustomers() {
        List<Customer> customerList = customerDAO.getAllCustomers();
        allCustomers.setAll(customerList);
    }

    private void loadProducts() {
        List<Product> productList = ProductDAO.getAll();
        // Lọc ra những sản phẩm có tồn kho > 0
        List<Product> productsInStock = productList.stream()
                .filter(product -> ProductDAO.getRealTimeStock(product.getProductID()) > 0)
                .collect(Collectors.toList());
        allProducts.setAll(productsInStock);
    }

    // Setup autocomplete cho customer ComboBox
    private void setupCustomerFeatures() {
        customerComboBox.setEditable(true);
        customerComboBox.setItems(allCustomers);

        // Converter để hiển thị tên khách hàng
        customerComboBox.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer != null ? customer.getFullName() : "";
            }

            @Override
            public Customer fromString(String string) {
                return allCustomers.stream()
                        .filter(customer -> customer.getFullName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Thêm listener để filter danh sách khi user nhập
        customerComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                customerComboBox.setItems(allCustomers);
            } else {
                ObservableList<Customer> filteredCustomers = allCustomers.stream()
                        .filter(customer -> customer.getFullName().toLowerCase()
                                .contains(newText.toLowerCase()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                customerComboBox.setItems(filteredCustomers);
            }

            if (!customerComboBox.isShowing() && !newText.isEmpty()) {
                customerComboBox.show();
            }
        });
    }

    // Setup product ComboBox với hiển thị tồn kho
    private void setupProductFeatures() {
        productComboBox.setItems(allProducts);

        productComboBox.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product != null) {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    int stock = ProductDAO.getRealTimeStock(product.getProductID());
                    return product.getProductName() + " - " + numberFormat.format(product.getPrice()) + " VND (Tồn: " + stock + ")";
                }
                return "";
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });

        productComboBox.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    int stock = ProductDAO.getRealTimeStock(product.getProductID());
                    setText(product.getProductName() + " - " + numberFormat.format(product.getPrice()) + " VND (Tồn: " + stock + ")");

                    // Vô hiệu hóa nếu hết hàng
                    if (stock <= 0) {
                        setDisable(true);
                        setStyle("-fx-text-fill: gray;");
                    } else {
                        setDisable(false);
                        setStyle("");
                    }
                }
            }
        });

        // Listener khi chọn sản phẩm - cập nhật stockLabel
        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldProduct, newProduct) -> {
            if (newProduct != null) {
                int currentStock = ProductDAO.getRealTimeStock(newProduct.getProductID());
                NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

                if (currentStock > 0) {
                    stockLabel.setText("Tồn kho: " + numberFormat.format(currentStock) + " sản phẩm");
                    stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: green;");
                } else {
                    stockLabel.setText("Hết hàng");
                    stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: red;");
                }
            } else {
                stockLabel.setText("Chọn sản phẩm để xem tồn kho");
                stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
            }
        });
    }

    @FXML
    private void handleAddProduct() {
        Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert("Lỗi", "Vui lòng chọn sản phẩm.");
            return;
        }

        // Kiểm tra tồn kho thực tế
        int currentStock = ProductDAO.getRealTimeStock(selectedProduct.getProductID());

        if (currentStock <= 0) {
            showAlert("Lỗi", "Sản phẩm này đã hết hàng!");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng phải là số nguyên.");
            return;
        }

        if (quantity <= 0) {
            showAlert("Lỗi", "Số lượng phải lớn hơn 0.");
            return;
        }

        // Kiểm tra số lượng yêu cầu có vượt quá tồn kho không
        if (quantity > currentStock) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            showAlert("Lỗi", "Số lượng yêu cầu (" + numberFormat.format(quantity) + ") vượt quá tồn kho hiện tại (" + numberFormat.format(currentStock) + ").\nVui lòng nhập số lượng nhỏ hơn hoặc bằng " + numberFormat.format(currentStock) + ".");
            return;
        }

        // Kiểm tra sản phẩm đã có trong hóa đơn chưa
        boolean productExists = false;
        for (InvoiceItem existingItem : invoiceItems) {
            if (existingItem.getProductID() == selectedProduct.getProductID()) {
                // Kiểm tra tổng số lượng sau khi cộng thêm
                int totalQuantityAfterAdd = existingItem.getQuantity() + quantity;
                if (totalQuantityAfterAdd > currentStock) {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    showAlert("Lỗi", "Tổng số lượng sản phẩm '" + selectedProduct.getProductName() + "' sẽ là " +
                            numberFormat.format(totalQuantityAfterAdd) + ", vượt quá tồn kho (" + numberFormat.format(currentStock) + ").\n" +
                            "Hiện tại trong hóa đơn đã có " + numberFormat.format(existingItem.getQuantity()) + " sản phẩm này.");
                    return;
                }

                // Cập nhật số lượng cho sản phẩm đã có
                InvoiceItem updatedItem = new InvoiceItem(
                        existingItem.getProductName(),
                        totalQuantityAfterAdd,
                        existingItem.getPrice(),
                        existingItem.getProductID()
                );

                int index = invoiceItems.indexOf(existingItem);
                invoiceItems.set(index, updatedItem);
                productExists = true;
                break;
            }
        }

        // Nếu sản phẩm chưa có trong hóa đơn, thêm mới
        if (!productExists) {
            invoiceItems.add(new InvoiceItem(selectedProduct.getProductName(), quantity,
                    selectedProduct.getPrice(), selectedProduct.getProductID()));
        }

        // Clear fields after adding
        productComboBox.getSelectionModel().clearSelection();
        quantityField.clear();

        // Cập nhật stockLabel về trạng thái mặc định
        stockLabel.setText("Chọn sản phẩm để xem tồn kho");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        calculateAndDisplayTotal();

        // Reload products để cập nhật danh sách (loại bỏ sản phẩm hết hàng)
        loadProducts();
    }

    @FXML
    private void handleRemoveProduct() {
        InvoiceItem selectedItem = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            invoiceItems.remove(selectedItem);
            calculateAndDisplayTotal();
            // Reload products để cập nhật danh sách
            loadProducts();
        } else {
            showAlert("Thông báo", "Vui lòng chọn sản phẩm cần xóa.");
        }
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
                showAlert("Lỗi", "Giảm giá phải là số hợp lệ!");
                discountPercent = 0.0;
            }
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        BigDecimal total = subTotal.multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setGroupingUsed(true);
        String formattedTotal = numberFormat.format(total.doubleValue());
        totalLabel.setText("Total: " + formattedTotal + " VND");
    }

    @FXML
    private void handleSaveInvoice() {
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            showAlert("Lỗi", "Vui lòng chọn khách hàng.");
            return;
        }

        if (invoiceItems.isEmpty()) {
            showAlert("Lỗi", "Vui lòng thêm ít nhất một sản phẩm vào hóa đơn.");
            return;
        }

        // Kiểm tra lại tồn kho trước khi lưu
        for (InvoiceItem item : invoiceItems) {
            int currentStock = ProductDAO.getRealTimeStock(item.getProductID());
            if (item.getQuantity() > currentStock) {
                showAlert("Lỗi", "Sản phẩm '" + item.getProductName() + "' không đủ tồn kho. Hiện tại chỉ còn " + currentStock + " sản phẩm.");
                return;
            }
        }

        calculateAndDisplayTotal();

        Invoice newInvoice = new Invoice();
        newInvoice.setCustomerID(selectedCustomer.getCustomerID());
        newInvoice.setUserID(1); // TODO: Lấy ID nhân viên thực tế từ phiên đăng nhập
        newInvoice.setDate(LocalDateTime.now());
        newInvoice.setTotalAmount(BigDecimal.valueOf(calculateTotalAmount()));
        newInvoice.setDiscount(BigDecimal.valueOf(calculateDiscount()));
        newInvoice.setStatus("Complete");

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

            // Xuất file txt
            String filePath = exportInvoiceToTxt(newInvoice, newInvoiceID, selectedCustomer);

            if (filePath != null) {
                showAlert("Thành công", "Hóa đơn đã được tạo và lưu thành công!\nFile được xuất tại: " + filePath);
            } else {
                showAlert("Thành công", "Hóa đơn đã được tạo và lưu thành công!\n(Xuất file thất bại - kiểm tra console để biết chi tiết)");
            }

            if (staffController != null) {
                try {
                    staffController.loadInvoiceHistory();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Lỗi", "Không thể điều hướng đến trang Lịch sử Hóa đơn.");
                }
            }
        } else {
            showAlert("Lỗi", "Không thể lưu hóa đơn. Vui lòng kiểm tra lại.");
        }
    }

    // Xuất file txt cho hóa đơn đã lưu - FIXED VERSION
    private String exportInvoiceToTxt(Invoice invoice, int invoiceID, Customer customer) {
        try {
            // Sử dụng Desktop thay vì C:\ để tránh vấn đề quyền
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String fileName = desktopPath + File.separator + "invoice_" + invoiceID + ".txt";

            // Kiểm tra thư mục Desktop có tồn tại không
            File desktopDir = new File(desktopPath);
            if (!desktopDir.exists() || !desktopDir.canWrite()) {
                // Nếu Desktop không khả dụng, sử dụng thư mục Documents
                String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
                fileName = documentsPath + File.separator + "invoice_" + invoiceID + ".txt";
            }

            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Header thông tin hóa đơn
            printWriter.println("===========================================");
            printWriter.println("            HÓA ĐƠN BÁN HÀNG");
            printWriter.println("===========================================");
            printWriter.println("Mã hóa đơn: " + invoiceID);
            printWriter.println("Ngày tạo: " + invoice.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            printWriter.println("Khách hàng: " + customer.getFullName());
            printWriter.println("Email: " + (customer.getEmail() != null ? customer.getEmail() : "N/A"));
            printWriter.println("===========================================");
            printWriter.println();

            // Chi tiết sản phẩm
            printWriter.println("CHI TIẾT SẢN PHẨM:");
            printWriter.println("-------------------------------------------");
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền");
            printWriter.println("-------------------------------------------");

            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            double subTotal = 0;

            for (InvoiceItem item : invoiceItems) {
                printWriter.printf("%-30s %-10d %-15s %-15s%n",
                        item.getProductName(),
                        item.getQuantity(),
                        numberFormat.format(item.getPrice()) + " VND",
                        numberFormat.format(item.getTotal()) + " VND"
                );
                subTotal += item.getTotal();
            }

            printWriter.println("-------------------------------------------");
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "TỔNG CỘNG", "", "",
                    numberFormat.format(subTotal) + " VND");

            // Thông tin khuyến mãi nếu có
            if (!selectedPromotions.isEmpty()) {
                printWriter.println();
                printWriter.println("KHUYẾN MÃI ÁP DỤNG:");
                for (Promotion promo : selectedPromotions) {
                    printWriter.println("- " + promo.getPromotionName() + ": " + promo.getDiscountPercentage() + "%");
                }
            }

            double discountPercent = calculateDiscount();
            if (discountPercent > 0) {
                double discountAmount = subTotal * (discountPercent / 100);
                printWriter.printf("%-30s %-10s %-15s %-15s%n", "Giảm giá (" + discountPercent + "%)", "", "",
                        "-" + numberFormat.format(discountAmount) + " VND");
            }

            printWriter.printf("%-30s %-10s %-15s %-15s%n", "THÀNH TIỀN", "", "",
                    numberFormat.format(calculateTotalAmount()) + " VND");

            printWriter.println("===========================================");
            printWriter.println("        Cảm ơn quý khách!");
            printWriter.println("===========================================");

            printWriter.close();
            fileWriter.close();

            // Kiểm tra file đã được tạo thành công
            if (file.exists()) {
                System.out.println("File created successfully: " + file.getAbsolutePath());
                return file.getAbsolutePath();
            } else {
                System.err.println("File was not created");
                return null;
            }

        } catch (IOException e) {
            System.err.println("Unable to export invoice to file: " + e.getMessage());
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xuất hóa đơn ra file: " + e.getMessage());
            return null;
        }
    }

    // Xuất file txt cho hóa đơn hiện tại (chưa lưu) - FIXED VERSION
    @FXML
    private void handleExportCurrentInvoice() {
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            showAlert("Lỗi", "Vui lòng chọn khách hàng.");
            return;
        }

        if (invoiceItems.isEmpty()) {
            showAlert("Lỗi", "Vui lòng thêm ít nhất một sản phẩm để xuất.");
            return;
        }

        try {
            // Sử dụng Desktop thay vì C:\ để tránh vấn đề quyền
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String fileName = desktopPath + File.separator + "current_invoice_" + System.currentTimeMillis() + ".txt";

            // Kiểm tra thư mục Desktop có tồn tại không
            File desktopDir = new File(desktopPath);
            if (!desktopDir.exists() || !desktopDir.canWrite()) {
                // Nếu Desktop không khả dụng, sử dụng thư mục Documents
                String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
                fileName = documentsPath + File.separator + "current_invoice_" + System.currentTimeMillis() + ".txt";
            }

            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Header thông tin hóa đơn
            printWriter.println("===========================================");
            printWriter.println("         HÓA ĐƠN BÁN HÀNG (DRAFT)");
            printWriter.println("===========================================");
            printWriter.println("Ngày tạo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            printWriter.println("Khách hàng: " + selectedCustomer.getFullName());
            printWriter.println("Email: " + (selectedCustomer.getEmail() != null ? selectedCustomer.getEmail() : "N/A"));
            printWriter.println("===========================================");
            printWriter.println();

            // Chi tiết sản phẩm
            printWriter.println("CHI TIẾT SẢN PHẨM:");
            printWriter.println("-------------------------------------------");
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền");
            printWriter.println("-------------------------------------------");

            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            double subTotal = 0;

            for (InvoiceItem item : invoiceItems) {
                printWriter.printf("%-30s %-10d %-15s %-15s%n",
                        item.getProductName(),
                        item.getQuantity(),
                        numberFormat.format(item.getPrice()) + " VND",
                        numberFormat.format(item.getTotal()) + " VND"
                );
                subTotal += item.getTotal();
            }

            printWriter.println("-------------------------------------------");
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "TỔNG CỘNG", "", "",
                    numberFormat.format(subTotal) + " VND");

            // Thông tin khuyến mãi nếu có
            if (!selectedPromotions.isEmpty()) {
                printWriter.println();
                printWriter.println("KHUYẾN MÃI ÁP DỤNG:");
                for (Promotion promo : selectedPromotions) {
                    printWriter.println("- " + promo.getPromotionName() + ": " + promo.getDiscountPercentage() + "%");
                }
            }

            double discountPercent = calculateDiscount();
            if (discountPercent > 0) {
                double discountAmount = subTotal * (discountPercent / 100);
                printWriter.printf("%-30s %-10s %-15s %-15s%n", "Giảm giá (" + discountPercent + "%)", "", "",
                        "-" + numberFormat.format(discountAmount) + " VND");
            }

            printWriter.printf("%-30s %-10s %-15s %-15s%n", "THÀNH TIỀN", "", "",
                    numberFormat.format(calculateTotalAmount()) + " VND");

            printWriter.println("===========================================");
            printWriter.println("        Cảm ơn quý khách!");
            printWriter.println("===========================================");

            printWriter.close();
            fileWriter.close();

            // Kiểm tra file đã được tạo thành công
            if (file.exists()) {
                showAlert("Thành công", "Hóa đơn đã được xuất thành công tại: " + file.getAbsolutePath());
            } else {
                showAlert("Lỗi", "File không được tạo thành công");
            }

        } catch (IOException e) {
            showAlert("Lỗi", "Không thể xuất hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        invoiceItems.clear();
        discountField.clear();
        totalLabel.setText("Total: 0 VND");
        customerComboBox.getSelectionModel().clearSelection();
        customerComboBox.getEditor().clear();
        productComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
        selectedPromotions.clear();

        // Reset promotion checkboxes
        promotionListView.getSelectionModel().clearSelection();

        // Reset stock label
        stockLabel.setText("Chọn sản phẩm để xem tồn kho");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        // Reload products để cập nhật danh sách
        loadProducts();
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
        private final int productID;

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
        double totalDiscount = selectedPromotions.stream()
                .mapToDouble(Promotion::getDiscountPercentage)
                .sum();

        discountField.setText(String.valueOf(totalDiscount));
        calculateAndDisplayTotal();
    }

    private void applyMultipleDiscounts() {
        double totalDiscount = selectedPromotions.stream()
                .mapToDouble(Promotion::getDiscountPercentage)
                .sum();

        discountField.setText(String.valueOf(totalDiscount));
        calculateAndDisplayTotal();
    }
}