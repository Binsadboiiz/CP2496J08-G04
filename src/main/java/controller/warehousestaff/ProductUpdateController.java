package controller.warehousestaff;

import dao.ProductDAO;
import dao.ProductSpecificationDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Product;
import model.ProductSpecification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Random;

public class ProductUpdateController implements Initializable {

    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField productNameField;
    @FXML private TextField productCodeField;
    @FXML private TextField barcodeField;
    @FXML private TextField brandField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageField;
    @FXML private TableView<ProductSpecification> specTable;
    @FXML private TableColumn<ProductSpecification, String> specNameColumn;
    @FXML private TableColumn<ProductSpecification, String> specValueColumn;
    @FXML private TextField specNameField;
    @FXML private TextField specValueField;
    @FXML private Button updateButton;
    @FXML private Button addSpecButton;
    @FXML private Button deleteSpecButton;
    @FXML private Button generateBarcodeButton;
    @FXML private Label statusLabel;

    private ObservableList<ProductSpecification> specList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadProducts();
        setupEventHandlers();
    }

    private void setupTable() {
        specNameColumn.setCellValueFactory(new PropertyValueFactory<>("specificationName"));
        specValueColumn.setCellValueFactory(new PropertyValueFactory<>("specificationValue"));
        specTable.setItems(specList);
    }

    private void loadProducts() {
        List<Product> products = ProductDAO.getAll();
        productComboBox.setItems(FXCollections.observableArrayList(products));
    }

    private void setupEventHandlers() {
        productComboBox.setOnAction(e -> loadProductDetails());
        updateButton.setOnAction(e -> updateProduct());
        addSpecButton.setOnAction(e -> addSpecification());
        deleteSpecButton.setOnAction(e -> deleteSpecification());
        generateBarcodeButton.setOnAction(e -> generateBarcode());
    }

    private void loadProductDetails() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct != null) {
            // Load thông tin cơ bản
            productNameField.setText(selectedProduct.getProductName());
            productCodeField.setText(selectedProduct.getProductCode());
            brandField.setText(selectedProduct.getBrand());
            typeField.setText(selectedProduct.getType());
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
            descriptionArea.setText(selectedProduct.getDescription());
            imageField.setText(selectedProduct.getImage());

            // Load thông số kỹ thuật
            loadSpecifications(selectedProduct.getProductID());

            // Load mã vạch từ thông số kỹ thuật
            loadBarcodeFromSpecifications();
        }
    }

    private void loadSpecifications(int productId) {
        List<ProductSpecification> specs = ProductSpecificationDAO.getByProductId(productId);
        specList.clear();
        specList.addAll(specs);
    }

    private void loadBarcodeFromSpecifications() {
        // Tìm mã vạch trong danh sách thông số kỹ thuật
        for (ProductSpecification spec : specList) {
            if ("Mã vạch".equals(spec.getSpecificationName())) {
                barcodeField.setText(spec.getSpecificationValue());
                break;
            }
        }
    }

    private void updateProduct() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct == null) {
            showStatus("Vui lòng chọn sản phẩm!", false);
            return;
        }

        try {
            // Kiểm tra mã vạch có trống không
            if (barcodeField.getText().trim().isEmpty()) {
                showStatus("Vui lòng nhập mã vạch!", false);
                return;
            }

            // Kiểm tra mã vạch có trùng với sản phẩm khác không
            if (isBarcodeExists(barcodeField.getText().trim(), selectedProduct.getProductID())) {
                showStatus("Mã vạch đã tồn tại!", false);
                return;
            }

            // Cập nhật thông tin sản phẩm (không có barcode trong model)
            selectedProduct.setProductName(productNameField.getText());
            selectedProduct.setProductCode(productCodeField.getText());
            selectedProduct.setBrand(brandField.getText());
            selectedProduct.setType(typeField.getText());
            selectedProduct.setPrice(Double.parseDouble(priceField.getText()));
            selectedProduct.setDescription(descriptionArea.getText());
            selectedProduct.setImage(imageField.getText());

            boolean success = ProductDAO.update(selectedProduct);

            if (success) {
                // Cập nhật mã vạch và thông số kỹ thuật
                updateBarcodeAndSpecifications(selectedProduct.getProductID());
                showStatus("Cập nhật sản phẩm thành công!", true);
                loadProducts(); // Refresh combo box
            } else {
                showStatus("Cập nhật thất bại!", false);
            }

        } catch (NumberFormatException e) {
            showStatus("Giá phải là số!", false);
        } catch (Exception e) {
            showStatus("Lỗi: " + e.getMessage(), false);
        }
    }

    private void updateBarcodeAndSpecifications(int productId) {
        // Xóa tất cả thông số cũ
        ProductSpecificationDAO.deleteByProductId(productId);

        // Thêm mã vạch như một thông số kỹ thuật
        String barcode = barcodeField.getText().trim();
        if (!barcode.isEmpty()) {
            ProductSpecification barcodeSpec = new ProductSpecification(0, productId, "Mã vạch", barcode);
            ProductSpecificationDAO.insert(barcodeSpec);
        }

        // Thêm lại các thông số kỹ thuật khác (loại trừ mã vạch)
        for (ProductSpecification spec : specList) {
            if (!"Mã vạch".equals(spec.getSpecificationName())) {
                spec.setProductID(productId);
                ProductSpecificationDAO.insert(spec);
            }
        }
    }

    private void addSpecification() {
        String name = specNameField.getText().trim();
        String value = specValueField.getText().trim();

        if (name.isEmpty() || value.isEmpty()) {
            showStatus("Vui lòng nhập đầy đủ thông tin thông số!", false);
            return;
        }

        // Không cho phép thêm thông số "Mã vạch" qua form này
        if ("Mã vạch".equals(name)) {
            showStatus("Mã vạch chỉ có thể chỉnh sửa ở trường riêng!", false);
            return;
        }

        ProductSpecification spec = new ProductSpecification(0, 0, name, value);
        specList.add(spec);

        // Clear fields
        specNameField.clear();
        specValueField.clear();
        showStatus("Đã thêm thông số!", true);
    }

    private void deleteSpecification() {
        ProductSpecification selected = specTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Không cho phép xóa thông số "Mã vạch" qua bảng
            if ("Mã vạch".equals(selected.getSpecificationName())) {
                showStatus("Không thể xóa mã vạch từ bảng thông số!", false);
                return;
            }

            specList.remove(selected);
            showStatus("Đã xóa thông số!", true);
        } else {
            showStatus("Vui lòng chọn thông số để xóa!", false);
        }
    }

    private void generateBarcode() {
        String barcode = generateRandomBarcode();

        // Kiểm tra mã vạch có trùng không, nếu trùng thì tạo lại
        while (isBarcodeExists(barcode, -1)) {
            barcode = generateRandomBarcode();
        }

        barcodeField.setText(barcode);
        showStatus("Đã tạo mã vạch: " + barcode, true);
    }

    private String generateRandomBarcode() {
        // Tạo mã vạch 13 số (EAN-13 format)
        Random random = new Random();
        StringBuilder barcode = new StringBuilder();

        // 12 số đầu
        for (int i = 0; i < 12; i++) {
            barcode.append(random.nextInt(10));
        }

        // Tính check digit cho EAN-13
        int checkDigit = calculateEAN13CheckDigit(barcode.toString());
        barcode.append(checkDigit);

        return barcode.toString();
    }

    private int calculateEAN13CheckDigit(String barcode) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(barcode.charAt(i));
            if (i % 2 == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }
        int remainder = sum % 10;
        return remainder == 0 ? 0 : 10 - remainder;
    }

    private boolean isBarcodeExists(String barcode, int excludeProductId) {
        // Kiểm tra mã vạch có tồn tại trong thông số kỹ thuật của sản phẩm khác không
        List<Product> products = ProductDAO.getAll();
        for (Product product : products) {
            if (product.getProductID() != excludeProductId) {
                List<ProductSpecification> specs = ProductSpecificationDAO.getByProductId(product.getProductID());
                for (ProductSpecification spec : specs) {
                    if ("Mã vạch".equals(spec.getSpecificationName()) &&
                            barcode.equals(spec.getSpecificationValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    @FXML
    private void refreshProducts() {
        loadProducts();
        showStatus("Đã làm mới danh sách sản phẩm!", true);
    }

    @FXML
    private void clearForm() {
        productComboBox.setValue(null);
        productNameField.clear();
        productCodeField.clear();
        barcodeField.clear();
        brandField.clear();
        typeField.clear();
        priceField.clear();
        descriptionArea.clear();
        imageField.clear();
        specList.clear();
        specNameField.clear();
        specValueField.clear();
        statusLabel.setText("");
    }
}