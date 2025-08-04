package controller.admin;

import dao.ProductDAO;
import dao.StockEntryDAO;
import dao.StockEntryDetailDAO;
import dao.SupplierDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Product;
import model.StockEntry;
import model.StockEntryDetail;
import model.Supplier;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class StockEntryFormController {
    @FXML private ComboBox<Supplier> cbSupplier;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Product> cbProduct;
    @FXML private TextField txtQty;
    @FXML private TextField txtUnitCost;
    @FXML private Button btnAddProduct;
    @FXML private TableView<StockEntryDetail> tableDetails;
    @FXML private TableColumn<StockEntryDetail, String> colProduct;
    @FXML private TableColumn<StockEntryDetail, Integer> colQty;
    @FXML private TableColumn<StockEntryDetail, Double> colUnitCost;
    @FXML private TableColumn<StockEntryDetail, Void> colAction;
    @FXML private Label lblTotal;

    private ObservableList<StockEntryDetail> detailList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load supplier list
        List<Supplier> suppliers = SupplierDAO.getAll();
        cbSupplier.setItems(FXCollections.observableArrayList(suppliers));

        // Load product list
        List<Product> products = ProductDAO.getAll();
        cbProduct.setItems(FXCollections.observableArrayList(products));

        // Table columns
        colProduct.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProductName()));
        colQty.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colUnitCost.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getUnitCost()).asObject());
        Stage dialog = new Stage();
        dialog.setResizable(false);

        // Thêm nút xóa từng dòng chi tiết
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnDelete = new Button("Delete");
            {
                btnDelete.setOnAction(e -> {
                    detailList.remove(getIndex());
                    updateTotal();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        tableDetails.setItems(detailList);
        lblTotal.setText("0");
    }

    @FXML
    private void onAddProduct() {
        Product product = cbProduct.getValue();
        String qtyStr = txtQty.getText().trim();
        String priceStr = txtUnitCost.getText().trim();

        if (product == null || qtyStr.isEmpty() || priceStr.isEmpty()) {
            showAlert("Please select product and enter quantity & price.");
            return;
        }
        int qty;
        double price;
        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
            if (qty <= 0 || price < 0) throw new Exception();
        } catch (Exception ex) {
            showAlert("Invalid quantity or price.");
            return;
        }

        // Check trùng sản phẩm (nếu muốn, cộng dồn hoặc báo lỗi)
        for (StockEntryDetail d : detailList) {
            if (d.getProductID() == product.getProductID()) {
                showAlert("Product already added.");
                return;
            }
        }

        StockEntryDetail detail = new StockEntryDetail();
        detail.setProductID(product.getProductID());
        detail.setProductName(product.getProductName());
        detail.setQuantity(qty);
        detail.setUnitCost(price);
        detailList.add(detail);

        updateTotal();

        cbProduct.getSelectionModel().clearSelection();
        txtQty.clear();
        txtUnitCost.clear();
    }

    private void updateTotal() {
        double sum = detailList.stream().mapToDouble(d -> d.getQuantity() * d.getUnitCost()).sum();
        lblTotal.setText(String.format("%.2f", sum));
    }

    @FXML
    private void onSave() {
        Supplier supplier = cbSupplier.getValue();
        LocalDate localDate = dpDate.getValue();

        if (supplier == null || localDate == null || detailList.isEmpty()) {
            showAlert("Please fill all required fields.");
            return;
        }
        // Lưu phiếu nhập
        StockEntry entry = new StockEntry();
        entry.setSupplierID(supplier.getSupplierID());
        entry.setDate(java.sql.Date.valueOf(localDate));

        entry.setUserID(1); // tạm hardcode, sau thay bằng user đăng nhập

        int entryID = StockEntryDAO.insert(entry);
        if (entryID <= 0) {
            showAlert("Insert stock entry failed!");
            return;
        }
        for (StockEntryDetail detail : detailList) {
            detail.setEntryID(entryID);
            StockEntryDetailDAO.insert(detail);
        }

        showAlert("Stock entry saved!");
        ((Stage) cbSupplier.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel() {
        ((Stage) cbSupplier.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
