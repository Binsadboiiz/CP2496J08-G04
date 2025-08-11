package controller.staff;

import dao.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import model.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Locale;
import java.util.stream.Collectors;

public class CreateInvoiceController {

    @FXML private ListView<Promotion> promotionListView;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField discountField;
    @FXML private TableView<InvoiceItem> invoiceTable;
    @FXML private TableColumn<InvoiceItem, String> productNameColumn;
    @FXML private TableColumn<InvoiceItem, Integer> quantityColumn;
    @FXML private TableColumn<InvoiceItem, Double> priceColumn;
    @FXML private TableColumn<InvoiceItem, Double> totalColumn;
    @FXML private Label totalLabel;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private Label stockLabel;

    private final ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts;
    private FilteredList<Customer> filteredCustomers;

    // cache stock để không query mỗi cell
    private final Map<Integer, Integer> stockCache = new HashMap<>();
    private boolean suppressProductFilter = false;
    private boolean suppressCustomerFilter = false;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO invoiceDetailDAO = new InvoiceDetailDAO();

    private final ObservableList<Promotion> selectedPromotions = FXCollections.observableArrayList();

    private StaffController staffController;
    public void setStaffController(StaffController s) { this.staffController = s; }

    private static final ObservableList<Product> selectedProducts = FXCollections.observableArrayList();
    public static void addProductToInvoice(Product p){ selectedProducts.add(p); }

    @FXML
    public void initialize() {
        loadCustomers();
        loadProductsAndStock();

        setupCustomerCombo();
        setupProductCombo();
        setupTable();
        setupPromotionList();

        if (!selectedProducts.isEmpty()) {
            for (Product p : selectedProducts) {
                invoiceItems.add(new InvoiceItem(p.getProductName(), 1, p.getPrice(), p.getProductID()));
            }
            selectedProducts.clear();
        }

        invoiceTable.setItems(invoiceItems);
        calculateAndDisplayTotal();
    }

    /* ===== data load ===== */
    private void loadCustomers() {
        allCustomers.setAll(customerDAO.getAllCustomers());
        filteredCustomers = new FilteredList<>(allCustomers, c -> true);
    }

    private void loadProductsAndStock() {
        allProducts.clear();
        stockCache.clear();
        for (Product p : ProductDAO.getAll()) {
            int stock = InventoryDAO.getCurrentStock(p.getProductID());
            stockCache.put(p.getProductID(), stock);
            if (stock > 0) allProducts.add(p);
        }
        filteredProducts = new FilteredList<>(allProducts, x -> true);
    }

    /* ===== UI setup ===== */
    private void setupTable() {
        productNameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
        nf.setGroupingUsed(true);

        priceColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : nf.format(v) + " VND");
            }
        });
        totalColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : nf.format(v) + " VND");
            }
        });
    }

    private void setupPromotionList() {
        var allPromos = FXCollections.<Promotion>observableArrayList(PromotionDAO.getAll());
        promotionListView.setItems(allPromos);
        promotionListView.setCellFactory(CheckBoxListCell.forListView(
                promo -> {
                    var sel = new javafx.beans.property.SimpleBooleanProperty(false);
                    sel.addListener((o, was, nowSel) -> {
                        if (nowSel) selectedPromotions.add(promo); else selectedPromotions.remove(promo);
                        applyMultipleDiscounts();
                    });
                    return sel;
                },
                new StringConverter<>() {
                    @Override public String toString(Promotion p){ return p.getPromotionName()+" ("+p.getDiscountPercentage()+"%)"; }
                    @Override public Promotion fromString(String s){ return null; }
                }
        ));
    }


    /* ===== UI setup ===== */
    private void setupCustomerCombo() {
        customerComboBox.setEditable(true);
        customerComboBox.setItems(filteredCustomers);
        customerComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Customer c) { return c == null ? "" : c.getFullName(); }
            @Override public Customer fromString(String s) {
                // ĐỪNG bao giờ trả về null vì sẽ clear selection
                if (s == null) return customerComboBox.getValue();
                String typed = s.trim();
                if (typed.isEmpty()) return customerComboBox.getValue();

                // exact match trước
                for (Customer c : allCustomers) {
                    if (c.getFullName().equalsIgnoreCase(typed)) return c;
                }
                // nếu filter chỉ còn 1 kết quả -> chọn
                List<Customer> matches = allCustomers.stream()
                        .filter(c -> c.getFullName().toLowerCase().contains(typed.toLowerCase()))
                        .limit(2)
                        .collect(Collectors.toList());
                if (matches.size() == 1) return matches.get(0);

                // không rõ ràng -> giữ nguyên selection hiện tại
                return customerComboBox.getValue();
            }
        });

        PauseTransition debounce = new PauseTransition(Duration.millis(120));
        customerComboBox.getEditor().textProperty().addListener((obs, o, q) -> {
            if (suppressCustomerFilter) return;
            if (!(customerComboBox.isFocused() || customerComboBox.getEditor().isFocused() || customerComboBox.isShowing())) return;

            debounce.stop();
            debounce.setOnFinished(e -> {
                String key = q == null ? "" : q.trim().toLowerCase();
                Platform.runLater(() -> {
                    filteredCustomers.setPredicate(c -> key.isEmpty() || c.getFullName().toLowerCase().contains(key));
                    if (filteredCustomers.isEmpty() && customerComboBox.isShowing()) customerComboBox.hide();
                });
            });
            debounce.playFromStart();
        });

        customerComboBox.getSelectionModel().selectedItemProperty().addListener((o, oldC, newC) -> {
            suppressCustomerFilter = true;
            Platform.runLater(() -> suppressCustomerFilter = false);
        });

        customerComboBox.setOnHidden(e -> Platform.runLater(() -> filteredCustomers.setPredicate(c -> true)));
    }

    private void setupProductCombo() {
        productComboBox.setEditable(true);
        productComboBox.setItems(filteredProducts);

        // Converter: map text -> Product, KHÔNG trả về null
        productComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Product p) { return p == null ? "" : p.getProductName(); }
            @Override public Product fromString(String s) {
                if (s == null) return productComboBox.getValue();
                String typed = s.trim();
                if (typed.isEmpty()) return productComboBox.getValue();

                // exact match
                for (Product p : allProducts) {
                    if (p.getProductName().equalsIgnoreCase(typed)) return p;
                }
                // single fuzzy
                List<Product> matches = allProducts.stream()
                        .filter(p -> p.getProductName().toLowerCase().contains(typed.toLowerCase()))
                        .limit(2)
                        .collect(Collectors.toList());
                if (matches.size() == 1) return matches.get(0);

                // không chắc -> giữ value cũ
                return productComboBox.getValue();
            }
        });

        // Hiển thị đã chọn: tên + giá + stock
        productComboBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setText(null); return; }
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
                int stock = stockCache.getOrDefault(p.getProductID(), 0);
                setText(p.getProductName() + " - " + nf.format(p.getPrice()) + " VND (Stock: " + stock + ")");
            }
        });

        productComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setText(null); setDisable(false); return; }
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
                int stock = stockCache.getOrDefault(p.getProductID(), 0);
                setText(p.getProductName() + " - " + nf.format(p.getPrice()) + " VND (Stock: " + stock + ")");
                setDisable(stock <= 0);
                setStyle(stock <= 0 ? "-fx-text-fill: gray;" : "");
            }
        });

        PauseTransition debounce = new PauseTransition(Duration.millis(120));
        productComboBox.getEditor().textProperty().addListener((obs, o, q) -> {
            if (suppressProductFilter) return;
            if (!(productComboBox.isFocused() || productComboBox.getEditor().isFocused() || productComboBox.isShowing())) return;

            debounce.stop();
            debounce.setOnFinished(e -> {
                String key = q == null ? "" : q.trim().toLowerCase();
                Platform.runLater(() -> {
                    filteredProducts.setPredicate(p -> key.isEmpty() || p.getProductName().toLowerCase().contains(key));
                    if (filteredProducts.isEmpty() && productComboBox.isShowing()) productComboBox.hide();
                });
            });
            debounce.playFromStart();
        });

        // ENTER trong editor -> commit chọn
        productComboBox.getEditor().setOnAction(e -> {
            if (productComboBox.getValue() == null) {
                Product hit = resolveProductFromEditor();
                if (hit != null) {
                    suppressProductFilter = true;
                    productComboBox.setValue(hit);
                    productComboBox.getEditor().setText(hit.getProductName());
                    Platform.runLater(() -> {
                        suppressProductFilter = false;
                        filteredProducts.setPredicate(p -> true);
                    });
                }
            }
        });

        // MẤT FOCUS editor -> thử commit chọn (nhưng từ nay converter đã lo)
        productComboBox.focusedProperty().addListener((obs, was, is) -> {
            if (!is && productComboBox.getValue() == null) {
                Product hit = resolveProductFromEditor();
                if (hit != null) {
                    suppressProductFilter = true;
                    productComboBox.setValue(hit);
                    productComboBox.getEditor().setText(hit.getProductName());
                    Platform.runLater(() -> {
                        suppressProductFilter = false;
                        filteredProducts.setPredicate(p -> true);
                    });
                }
            }
        });

        // Khi chọn -> cập nhật label, reset filter, giữ editor = tên
        productComboBox.getSelectionModel().selectedItemProperty().addListener((o, oldP, newP) -> {
            suppressProductFilter = true;
            try {
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
                if (newP != null) {
                    int currentStock = stockCache.getOrDefault(newP.getProductID(), 0);
                    if (currentStock > 0) {
                        stockLabel.setText("Stock: " + nf.format(currentStock) + " items");
                        stockLabel.setStyle("-fx-font-size:12px;-fx-font-style:italic;-fx-text-fill:green;");
                    } else {
                        stockLabel.setText("Out of stock");
                        stockLabel.setStyle("-fx-font-size:12px;-fx-font-style:italic;-fx-text-fill:red;");
                    }
                    productComboBox.getEditor().setText(newP.getProductName());
                    Platform.runLater(() -> filteredProducts.setPredicate(p -> true));
                } else {
                    stockLabel.setText("Select product to view stock");
                    stockLabel.setStyle("-fx-font-size:12px;-fx-font-style:italic;");
                }
            } finally {
                Platform.runLater(() -> suppressProductFilter = false);
            }
        });

        productComboBox.setOnHidden(e -> Platform.runLater(() -> filteredProducts.setPredicate(p -> true)));
    }

    private Customer resolveCustomerFromEditor() {
        if (customerComboBox == null) return null;
        String typed = customerComboBox.getEditor().getText();
        if (typed == null || typed.isBlank()) return null;
        String key = typed.trim().toLowerCase();

        for (Customer c : allCustomers) {
            if (c.getFullName().equalsIgnoreCase(typed.trim())) return c;
        }
        List<Customer> matches = allCustomers.stream()
                .filter(c -> c.getFullName().toLowerCase().contains(key))
                .limit(2)
                .collect(Collectors.toList());
        if (matches.size() == 1) return matches.get(0);
        return null;
    }

    private Product resolveProductFromEditor() {
        if (productComboBox == null) return null;
        String typed = productComboBox.getEditor().getText();
        if (typed == null || typed.isBlank()) return null;
        String key = typed.trim().toLowerCase();

        // Ưu tiên khớp tên chính xác
        for (Product p : allProducts) {
            if (p.getProductName().equalsIgnoreCase(typed.trim())) return p;
        }
        // Nếu filter đang còn 1 kết quả duy nhất -> chọn nó
        if (filteredProducts != null) {
            List<Product> matches = filteredProducts.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(key))
                    .limit(2)
                    .collect(Collectors.toList());
            if (matches.size() == 1) return matches.get(0);
        }
        return null;
    }

    @FXML
    private void handleAddProduct() {
        // 1) Lấy selection hiện tại
        Product p = productComboBox.getSelectionModel().getSelectedItem();

        // 2) Nếu chưa có, thử lấy từ text trong editor rồi select vào combobox
        if (p == null) {
            Product hit = resolveProductFromEditor(); // bạn đã có hàm này
            if (hit != null) {
                suppressProductFilter = true;
                productComboBox.getSelectionModel().select(hit);
                productComboBox.getEditor().setText(hit.getProductName());
                p = hit;
                Platform.runLater(() -> {
                    suppressProductFilter = false;
                    filteredProducts.setPredicate(x -> true);
                });
            }
        }

        // 3) Vẫn không có -> báo lỗi như cũ
        if (p == null) {
            alert("Error","Please select a product.");
            return;
        }

        int currentStock = InventoryDAO.getCurrentStock(p.getProductID());
        stockCache.put(p.getProductID(), currentStock);
        if (currentStock <= 0) { alert("Error","This product is out of stock!"); return; }

        int qty;
        try { qty = Integer.parseInt(quantityField.getText()); }
        catch (NumberFormatException e) { alert("Error","Quantity must be a valid number."); return; }
        if (qty <= 0) { alert("Error","Quantity must be greater than 0."); return; }
        if (qty > currentStock) {
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
            alert("Error","Requested ("+nf.format(qty)+") > stock ("+nf.format(currentStock)+").");
            return;
        }

        boolean merged=false;
        for (int i=0;i<invoiceItems.size();i++){
            InvoiceItem it = invoiceItems.get(i);
            if (it.getProductID()==p.getProductID()){
                int total = it.getQuantity()+qty;
                if (total>currentStock){
                    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
                    alert("Error","Total of '"+p.getProductName()+"' = "+nf.format(total)+" > stock ("+nf.format(currentStock)+").");
                    return;
                }
                invoiceItems.set(i, new InvoiceItem(it.getProductName(), total, it.getPrice(), it.getProductID()));
                merged=true; break;
            }
        }
        if (!merged) invoiceItems.add(new InvoiceItem(p.getProductName(), qty, p.getPrice(), p.getProductID()));

        productComboBox.getSelectionModel().clearSelection();
        productComboBox.getEditor().clear();
        quantityField.clear();
        stockLabel.setText("Select product to view stock");
        stockLabel.setStyle("-fx-font-size:12px;-fx-font-style:italic;");

        calculateAndDisplayTotal();

        // refresh nhanh
        loadProductsAndStock();
        productComboBox.setItems(filteredProducts);
    }


    @FXML
    private void handleRemoveProduct() {
        InvoiceItem sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Notice","Please select a product to remove."); return; }
        invoiceItems.remove(sel);
        calculateAndDisplayTotal();
        loadProductsAndStock();
        productComboBox.setItems(filteredProducts);
    }

    @FXML private void handleCalculateTotal(){ calculateAndDisplayTotal(); }

    @FXML
    private void handleSaveInvoice() {
        // Lấy customer đã chọn, nếu null thì map từ editor
        Customer c = customerComboBox.getSelectionModel().getSelectedItem();
        if (c == null) {
            Customer hit = resolveCustomerFromEditor(); // có ở dưới, nếu chưa có dán thêm
            if (hit != null) {
                suppressCustomerFilter = true;
                customerComboBox.getSelectionModel().select(hit);
                customerComboBox.getEditor().setText(hit.getFullName());
                c = hit;
                Platform.runLater(() -> {
                    suppressCustomerFilter = false;
                    filteredCustomers.setPredicate(x -> true);
                });
            }
        }
        if (c == null) { alert("Error","Please select a customer."); return; }

        if (invoiceItems.isEmpty()) { alert("Error","Please add at least one product."); return; }

        for (InvoiceItem it : invoiceItems) {
            int stock = InventoryDAO.getCurrentStock(it.getProductID());
            if (it.getQuantity() > stock) {
                alert("Error","Product '"+it.getProductName()+"' has only "+stock+" items left.");
                return;
            }
        }

        Invoice inv = new Invoice();
        inv.setCustomerID(c.getCustomerID());
        inv.setUserID(1);
        inv.setDate(LocalDateTime.now());
        inv.setTotalAmount(BigDecimal.valueOf(calculateTotalAmount()));
        inv.setDiscount(BigDecimal.valueOf(calculateDiscount()));
        inv.setStatus("Complete");

        int id = invoiceDAO.insertInvoice(inv);
        if (id <= 0) { alert("Error","Unable to save invoice."); return; }

        for (InvoiceItem it : invoiceItems) {
            invoiceDetailDAO.insertInvoiceDetail(new InvoiceDetail(
                    id, it.getProductID(), it.getQuantity(),
                    BigDecimal.valueOf(it.getPrice()), BigDecimal.ZERO));
        }

        String path = exportInvoiceToTxt(inv, id, c);
        alert("Success","Invoice created!\n" + (path!=null? "Saved to: "+path : "(Export failed)"));

        if (staffController != null) {
            try { staffController.loadInvoiceHistory(); }
            catch (Exception e) { e.printStackTrace(); alert("Error","Unable to navigate to Invoice History page."); }
        }
    }


    @FXML
    private void handleExportCurrentInvoice() {
        Customer c = customerComboBox.getSelectionModel().getSelectedItem();
        if (c == null) {
            Customer hit = resolveCustomerFromEditor();
            if (hit != null) {
                suppressCustomerFilter = true;
                customerComboBox.getSelectionModel().select(hit);
                customerComboBox.getEditor().setText(hit.getFullName());
                c = hit;
                Platform.runLater(() -> {
                    suppressCustomerFilter = false;
                    filteredCustomers.setPredicate(x -> true);
                });
            }
        }
        if (c == null) { alert("Error","Please select a customer."); return; }
        if (invoiceItems.isEmpty()) { alert("Error","Please add at least one product to export."); return; }

        // ... phần còn lại y như bạn đang có
    }


    @FXML
    private void handleReset() {
        invoiceItems.clear();
        discountField.clear();
        totalLabel.setText("Total: 0 VND");

        customerComboBox.getSelectionModel().clearSelection();
        customerComboBox.getEditor().clear();

        productComboBox.getSelectionModel().clearSelection();
        productComboBox.getEditor().clear();

        quantityField.clear();
        selectedPromotions.clear();
        promotionListView.getSelectionModel().clearSelection();

        stockLabel.setText("Select product to view stock");
        stockLabel.setStyle("-fx-font-size:12px;-fx-font-style:italic;");

        loadProductsAndStock();
        productComboBox.setItems(filteredProducts);
    }

    /* ===== totals / discounts ===== */
    private void calculateAndDisplayTotal() {
        BigDecimal sub = invoiceItems.stream()
                .map(i -> BigDecimal.valueOf(i.getTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double discountPercent = 0.0;
        if (!discountField.getText().isEmpty()) {
            try { discountPercent = Double.parseDouble(discountField.getText()); }
            catch (NumberFormatException e) { alert("Error","Discount must be a valid number!"); }
        }

        BigDecimal total = sub.multiply(
                        BigDecimal.ONE.subtract(BigDecimal.valueOf(discountPercent)
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)))
                .setScale(0, RoundingMode.HALF_UP);

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
        nf.setGroupingUsed(true);
        totalLabel.setText("Total: " + nf.format(total.doubleValue()) + " VND");
    }

    private double calculateTotalAmount() {
        double sub = invoiceItems.stream().mapToDouble(InvoiceItem::getTotal).sum();
        double d = calculateDiscount();
        return sub - sub * (d / 100);
    }

    private double calculateDiscount() {
        try { if (!discountField.getText().isEmpty()) return Double.parseDouble(discountField.getText()); }
        catch (NumberFormatException ignored) {}
        return 0;
    }

    private void applyMultipleDiscounts() {
        double totalDiscount = selectedPromotions.stream().mapToDouble(Promotion::getDiscountPercentage).sum();
        discountField.setText(String.valueOf(totalDiscount));
        calculateAndDisplayTotal();
    }

    /* ===== utils ===== */
    private void alert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private String exportInvoiceToTxt(Invoice invoice, int invoiceID, Customer customer) {
        try {
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String fileName = desktopPath + File.separator + "invoice_" + invoiceID + ".txt";
            File desktopDir = new File(desktopPath);
            if (!desktopDir.exists() || !desktopDir.canWrite()) {
                fileName = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "invoice_" + invoiceID + ".txt";
            }

            File file = new File(fileName);
            try (FileWriter fw = new FileWriter(file); PrintWriter pw = new PrintWriter(fw)) {
                pw.println("===========================================");
                pw.println("            SALES INVOICE");
                pw.println("===========================================");
                pw.println("Invoice ID: " + invoiceID);
                pw.println("Date: " + invoice.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                pw.println("Customer: " + customer.getFullName());
                pw.println("Email: " + (customer.getEmail()!=null? customer.getEmail() : "N/A"));
                pw.println("===========================================");
                pw.println();
                pw.println("PRODUCT DETAILS:");
                pw.println("-------------------------------------------");
                pw.printf("%-30s %-10s %-15s %-15s%n", "Product Name", "Qty", "Unit Price", "Amount");
                pw.println("-------------------------------------------");

                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
                double sub = 0;
                for (InvoiceItem it : invoiceItems) {
                    pw.printf("%-30s %-10d %-15s %-15s%n",
                            it.getProductName(), it.getQuantity(),
                            nf.format(it.getPrice()) + " VND",
                            nf.format(it.getTotal()) + " VND");
                    sub += it.getTotal();
                }

                pw.println("-------------------------------------------");
                pw.printf("%-30s %-10s %-15s %-15s%n", "SUBTOTAL", "", "", nf.format(sub) + " VND");

                if (!selectedPromotions.isEmpty()) {
                    pw.println();
                    pw.println("APPLIED PROMOTIONS:");
                    for (Promotion promo : selectedPromotions) {
                        pw.println("- " + promo.getPromotionName() + ": " + promo.getDiscountPercentage() + "%");
                    }
                }

                double d = calculateDiscount();
                if (d > 0) {
                    double da = sub * (d / 100);
                    pw.printf("%-30s %-10s %-15s %-15s%n", "Discount (" + d + "%)", "", "", "-" + nf.format(da) + " VND");
                }

                pw.printf("%-30s %-10s %-15s %-15s%n", "TOTAL AMOUNT", "", "", nf.format(calculateTotalAmount()) + " VND");
                pw.println("===========================================");
                pw.println("        Thank you for your business!");
                pw.println("===========================================");
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            alert("Error","Unable to export invoice: " + e.getMessage());
            return null;
        }
    }
}
