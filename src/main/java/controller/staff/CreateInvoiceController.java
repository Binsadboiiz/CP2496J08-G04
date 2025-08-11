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
        productNameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));

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
        List<Product> productsInStock = productList.stream()
                .filter(product -> ProductDAO.getRealTimeStock(product.getProductID()) > 0)
                .collect(Collectors.toList());
        allProducts.setAll(productsInStock);
    }

    private void setupCustomerFeatures() {
        customerComboBox.setEditable(true);
        customerComboBox.setItems(allCustomers);

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

    private void setupProductFeatures() {
        productComboBox.setItems(allProducts);

        productComboBox.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product != null) {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    int stock = ProductDAO.getRealTimeStock(product.getProductID());
                    return product.getProductName() + " - " + numberFormat.format(product.getPrice()) + " VND (Stock: " + stock + ")";
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
                    setText(product.getProductName() + " - " + numberFormat.format(product.getPrice()) + " VND (Stock: " + stock + ")");

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

        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldProduct, newProduct) -> {
            if (newProduct != null) {
                int currentStock = ProductDAO.getRealTimeStock(newProduct.getProductID());
                NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

                if (currentStock > 0) {
                    stockLabel.setText("Stock: " + numberFormat.format(currentStock) + " items");
                    stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: green;");
                } else {
                    stockLabel.setText("Out of stock");
                    stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: red;");
                }
            } else {
                stockLabel.setText("Select product to view stock");
                stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
            }
        });
    }

    @FXML
    private void handleAddProduct() {
        Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert("Error", "Please select a product.");
            return;
        }

        int currentStock = ProductDAO.getRealTimeStock(selectedProduct.getProductID());

        if (currentStock <= 0) {
            showAlert("Error", "This product is out of stock!");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity must be a valid number.");
            return;
        }

        if (quantity <= 0) {
            showAlert("Error", "Quantity must be greater than 0.");
            return;
        }

        if (quantity > currentStock) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            showAlert("Error", "Requested quantity (" + numberFormat.format(quantity) + ") exceeds available stock (" + numberFormat.format(currentStock) + ").\nPlease enter a quantity less than or equal to " + numberFormat.format(currentStock) + ".");
            return;
        }

        boolean productExists = false;
        for (InvoiceItem existingItem : invoiceItems) {
            if (existingItem.getProductID() == selectedProduct.getProductID()) {
                int totalQuantityAfterAdd = existingItem.getQuantity() + quantity;
                if (totalQuantityAfterAdd > currentStock) {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    showAlert("Error", "Total quantity of product '" + selectedProduct.getProductName() + "' will be " +
                            numberFormat.format(totalQuantityAfterAdd) + ", exceeding available stock (" + numberFormat.format(currentStock) + ").\n" +
                            "Currently in invoice: " + numberFormat.format(existingItem.getQuantity()) + " items.");
                    return;
                }

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

        if (!productExists) {
            invoiceItems.add(new InvoiceItem(selectedProduct.getProductName(), quantity,
                    selectedProduct.getPrice(), selectedProduct.getProductID()));
        }

        productComboBox.getSelectionModel().clearSelection();
        quantityField.clear();

        stockLabel.setText("Select product to view stock");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        calculateAndDisplayTotal();
        loadProducts();
    }

    @FXML
    private void handleRemoveProduct() {
        InvoiceItem selectedItem = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            invoiceItems.remove(selectedItem);
            calculateAndDisplayTotal();
            loadProducts();
        } else {
            showAlert("Notice", "Please select a product to remove.");
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
                showAlert("Error", "Discount must be a valid number!");
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
            showAlert("Error", "Please select a customer.");
            return;
        }

        if (invoiceItems.isEmpty()) {
            showAlert("Error", "Please add at least one product to the invoice.");
            return;
        }

        for (InvoiceItem item : invoiceItems) {
            int currentStock = ProductDAO.getRealTimeStock(item.getProductID());
            if (item.getQuantity() > currentStock) {
                showAlert("Error", "Product '" + item.getProductName() + "' has insufficient stock. Only " + currentStock + " items available.");
                return;
            }
        }

        calculateAndDisplayTotal();

        Invoice newInvoice = new Invoice();
        newInvoice.setCustomerID(selectedCustomer.getCustomerID());
        newInvoice.setUserID(1); // TODO: Get actual staff ID from login session
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

            String filePath = exportInvoiceToTxt(newInvoice, newInvoiceID, selectedCustomer);

            if (filePath != null) {
                showAlert("Success", "Invoice has been created and saved successfully!\nFile exported to: " + filePath);
            } else {
                showAlert("Success", "Invoice has been created and saved successfully!\n(File export failed - check console for details)");
            }

            if (staffController != null) {
                try {
                    staffController.loadInvoiceHistory();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Unable to navigate to Invoice History page.");
                }
            }
        } else {
            showAlert("Error", "Unable to save invoice. Please check again.");
        }
    }

    private String exportInvoiceToTxt(Invoice invoice, int invoiceID, Customer customer) {
        try {
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String fileName = desktopPath + File.separator + "invoice_" + invoiceID + ".txt";

            File desktopDir = new File(desktopPath);
            if (!desktopDir.exists() || !desktopDir.canWrite()) {
                String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
                fileName = documentsPath + File.separator + "invoice_" + invoiceID + ".txt";
            }

            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("===========================================");
            printWriter.println("            SALES INVOICE");
            printWriter.println("===========================================");
            printWriter.println("Invoice ID: " + invoiceID);
            printWriter.println("Date: " + invoice.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            printWriter.println("Customer: " + customer.getFullName());
            printWriter.println("Email: " + (customer.getEmail() != null ? customer.getEmail() : "N/A"));
            printWriter.println("===========================================");
            printWriter.println();

            printWriter.println("PRODUCT DETAILS:");
            printWriter.println("-------------------------------------------");
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "Product Name", "Qty", "Unit Price", "Amount");
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
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "SUBTOTAL", "", "",
                    numberFormat.format(subTotal) + " VND");

            if (!selectedPromotions.isEmpty()) {
                printWriter.println();
                printWriter.println("APPLIED PROMOTIONS:");
                for (Promotion promo : selectedPromotions) {
                    printWriter.println("- " + promo.getPromotionName() + ": " + promo.getDiscountPercentage() + "%");
                }
            }

            double discountPercent = calculateDiscount();
            if (discountPercent > 0) {
                double discountAmount = subTotal * (discountPercent / 100);
                printWriter.printf("%-30s %-10s %-15s %-15s%n", "Discount (" + discountPercent + "%)", "", "",
                        "-" + numberFormat.format(discountAmount) + " VND");
            }

            printWriter.printf("%-30s %-10s %-15s %-15s%n", "TOTAL AMOUNT", "", "",
                    numberFormat.format(calculateTotalAmount()) + " VND");

            printWriter.println("===========================================");
            printWriter.println("        Thank you for your business!");
            printWriter.println("===========================================");

            printWriter.close();
            fileWriter.close();

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
            showAlert("Error", "Unable to export invoice to file: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleExportCurrentInvoice() {
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer.");
            return;
        }

        if (invoiceItems.isEmpty()) {
            showAlert("Error", "Please add at least one product to export.");
            return;
        }

        try {
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String fileName = desktopPath + File.separator + "current_invoice_" + System.currentTimeMillis() + ".txt";

            File desktopDir = new File(desktopPath);
            if (!desktopDir.exists() || !desktopDir.canWrite()) {
                String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
                fileName = documentsPath + File.separator + "current_invoice_" + System.currentTimeMillis() + ".txt";
            }

            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("===========================================");
            printWriter.println("         SALES INVOICE (DRAFT)");
            printWriter.println("===========================================");
            printWriter.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            printWriter.println("Customer: " + selectedCustomer.getFullName());
            printWriter.println("Email: " + (selectedCustomer.getEmail() != null ? selectedCustomer.getEmail() : "N/A"));
            printWriter.println("===========================================");
            printWriter.println();

            printWriter.println("PRODUCT DETAILS:");
            printWriter.println("-------------------------------------------");
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "Product Name", "Qty", "Unit Price", "Amount");
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
            printWriter.printf("%-30s %-10s %-15s %-15s%n", "SUBTOTAL", "", "",
                    numberFormat.format(subTotal) + " VND");

            if (!selectedPromotions.isEmpty()) {
                printWriter.println();
                printWriter.println("APPLIED PROMOTIONS:");
                for (Promotion promo : selectedPromotions) {
                    printWriter.println("- " + promo.getPromotionName() + ": " + promo.getDiscountPercentage() + "%");
                }
            }

            double discountPercent = calculateDiscount();
            if (discountPercent > 0) {
                double discountAmount = subTotal * (discountPercent / 100);
                printWriter.printf("%-30s %-10s %-15s %-15s%n", "Discount (" + discountPercent + "%)", "", "",
                        "-" + numberFormat.format(discountAmount) + " VND");
            }

            printWriter.printf("%-30s %-10s %-15s %-15s%n", "TOTAL AMOUNT", "", "",
                    numberFormat.format(calculateTotalAmount()) + " VND");

            printWriter.println("===========================================");
            printWriter.println("        Thank you for your business!");
            printWriter.println("===========================================");

            printWriter.close();
            fileWriter.close();

            if (file.exists()) {
                showAlert("Success", "Invoice exported successfully to: " + file.getAbsolutePath());
            } else {
                showAlert("Error", "File was not created successfully");
            }

        } catch (IOException e) {
            showAlert("Error", "Unable to export invoice: " + e.getMessage());
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

        promotionListView.getSelectionModel().clearSelection();

        stockLabel.setText("Select product to view stock");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

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