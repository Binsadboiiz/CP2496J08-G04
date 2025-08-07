package controller.cashier;

import dao.DatabaseConnection;
import dao.ProductDAO;
import dao.PromotionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Promotion;

import java.sql.SQLException;
import java.time.LocalDate;

public class PromotionManagementController {

    @FXML private TextField promotionNameField, discountField;
    @FXML private ComboBox<String> productNameComboBox;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private TableView<Promotion> promotionTable;
    @FXML private TableColumn<Promotion, String> promotionNameColumn;
    @FXML private TableColumn<Promotion, Integer> productIDColumn;
    @FXML private TableColumn<Promotion, Double> discountColumn;
    @FXML private TableColumn<Promotion, LocalDate> startDateColumn, endDateColumn;

    private ObservableList<Promotion> promotionList;

    private ProductDAO productDAO;
    private PromotionDAO promotionDAO;

    @FXML
    public void initialize() {
        try {
            productDAO = new ProductDAO(DatabaseConnection.getConnection());
            promotionDAO = new PromotionDAO(DatabaseConnection.getConnection());

            productNameComboBox.setItems(FXCollections.observableArrayList(productDAO.getAllProductNames()));
            productNameComboBox.setEditable(true);

            promotionNameColumn.setCellValueFactory(cell -> cell.getValue().promotionNameProperty());
            productIDColumn.setCellValueFactory(cell -> cell.getValue().appliedProductIDProperty().asObject());
            discountColumn.setCellValueFactory(cell -> cell.getValue().discountPercentProperty().asObject());
            startDateColumn.setCellValueFactory(cell -> cell.getValue().startDateProperty());
            endDateColumn.setCellValueFactory(cell -> cell.getValue().endDateProperty());

            promotionTable.setRowFactory(tv -> {
                TableRow<Promotion> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        Promotion selected = row.getItem();
                        populateForm(selected);
                    }
                });
                return row;
            });

            loadPromotions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateForm(Promotion promo) {
        promotionNameField.setText(promo.getPromotionName());
        productNameComboBox.setValue(productDAO.getProductNameByID(promo.getAppliedProductID()));
        discountField.setText(String.valueOf(promo.getDiscountPercent()));
        startDatePicker.setValue(promo.getStartDate());
        endDatePicker.setValue(promo.getEndDate());
    }

    @FXML
    private void handleAddPromotion() {
        String promoName = promotionNameField.getText();
        String selectedProduct = productNameComboBox.getEditor().getText();

        if (promoName.isEmpty() || selectedProduct.isEmpty() || discountField.getText().isEmpty()
                || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        double discount;
        try {
            discount = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Giảm giá phải là số.");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        int productID = productDAO.getProductIDByName(selectedProduct);
        if (productID == -1) {
            showAlert("Lỗi", "Không tìm thấy sản phẩm.");
            return;
        }

        Promotion promo = new Promotion(0, promoName, productID, discount, startDate, endDate);
        boolean success = promotionDAO.insertPromotion(promo);
        if (success) {
            showAlert("Thành Công", "Đã thêm chương trình khuyến mãi.");
            loadPromotions();
            clearForm();
        } else {
            showAlert("Lỗi", "Không thể thêm chương trình khuyến mãi.");
        }
    }

    @FXML
    private void handleUpdatePromotion() {
        Promotion selected = promotionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn khuyến mãi để cập nhật.");
            return;
        }

        String promoName = promotionNameField.getText();
        String selectedProduct = productNameComboBox.getEditor().getText();

        if (promoName.isEmpty() || selectedProduct.isEmpty() || discountField.getText().isEmpty()
                || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        double discount;
        try {
            discount = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Giảm giá phải là số.");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        int productID = productDAO.getProductIDByName(selectedProduct);
        if (productID == -1) {
            showAlert("Lỗi", "Không tìm thấy sản phẩm.");
            return;
        }

        Promotion updated = new Promotion(selected.getPromotionID(), promoName, productID, discount, startDate, endDate);
        boolean success = promotionDAO.updatePromotion(updated);
        if (success) {
            showAlert("Thành Công", "Đã cập nhật khuyến mãi.");
            loadPromotions();
            clearForm();
        } else {
            showAlert("Lỗi", "Không thể cập nhật khuyến mãi.");
        }
    }

    @FXML
    private void handleDeletePromotion() {
        Promotion selected = promotionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn khuyến mãi để xoá.");
            return;
        }

        boolean success = promotionDAO.deletePromotion(selected.getPromotionID());
        if (success) {
            showAlert("Thành Công", "Đã xoá khuyến mãi.");
            loadPromotions();
            clearForm();
        } else {
            showAlert("Lỗi", "Không thể xoá khuyến mãi.");
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    private void loadPromotions() {
        try {
            promotionList = FXCollections.observableArrayList(promotionDAO.getAllPromotions());
            promotionTable.setItems(promotionList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải danh sách khuyến mãi.");
        }
    }

    private void clearForm() {
        promotionNameField.clear();
        discountField.clear();
        productNameComboBox.getSelectionModel().clearSelection();
        productNameComboBox.getEditor().clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        promotionTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
