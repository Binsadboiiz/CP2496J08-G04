//package controller.cashier;
//
//import dao.ProductDAO;
//import dao.PromotionDAO;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import model.Promotion;
//import java.time.LocalDate;
//
//public class PromotionManagementController {
//
//    @FXML private TextField promotionNameField, discountField;
//    @FXML private ComboBox<String> productNameComboBox;
//    @FXML private DatePicker startDatePicker, endDatePicker;
//    @FXML private TableView<Promotion> promotionTable;
//    @FXML private TableColumn<Promotion, String> promotionNameColumn;
//    @FXML private TableColumn<Promotion, Integer> productIDColumn;
//    @FXML private TableColumn<Promotion, Double> discountColumn;
//    @FXML private TableColumn<Promotion, LocalDate> startDateColumn, endDateColumn;
//
//    private ObservableList<Promotion> promotionList;
//
//    @FXML
//    public void initialize() {
//        productNameComboBox.setItems(FXCollections.observableArrayList(ProductDAO.getAllProductNames()));
//        productNameComboBox.setEditable(true);
//        loadPromotions();
//
//        promotionNameColumn.setCellValueFactory(cell -> cell.getValue().promotionNameProperty());
//        productIDColumn.setCellValueFactory(cell -> cell.getValue().appliedProductIDProperty().asObject());
//        discountColumn.setCellValueFactory(cell -> cell.getValue().discountPercentProperty().asObject());
//        startDateColumn.setCellValueFactory(cell -> cell.getValue().startDateProperty());
//        endDateColumn.setCellValueFactory(cell -> cell.getValue().endDateProperty());
//    }
//
//    @FXML
//    private void handleAddPromotion() {
//        String promoName = promotionNameField.getText();
//        String selectedProduct = productNameComboBox.getEditor().getText();
//
//        if (promoName.isEmpty() || selectedProduct.isEmpty() || discountField.getText().isEmpty()
//                || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
//            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
//            return;
//        }
//
//        double discount;
//        try {
//            discount = Double.parseDouble(discountField.getText());
//        } catch (NumberFormatException e) {
//            showAlert("Lỗi", "Giảm giá phải là số.");
//            return;
//        }
//
//        LocalDate startDate = startDatePicker.getValue();
//        LocalDate endDate = endDatePicker.getValue();
//
//        int productID = ProductDAO.getProductIDByName(selectedProduct);
//        if (productID == -1) {
//            showAlert("Lỗi", "Không tìm thấy sản phẩm.");
//            return;
//        }
//
//        Promotion promo = new Promotion(0, promoName, productID, discount, startDate, endDate);
//        boolean success = PromotionDAO.insertPromotion(promo);
//        if (success) {
//            showAlert("Thành Công", "Đã thêm chương trình khuyến mãi.");
//            loadPromotions();
//            clearForm();
//        } else {
//            showAlert("Lỗi", "Không thể thêm chương trình khuyến mãi.");
//        }
//    }
//
//    private void loadPromotions() {
//        promotionList = FXCollections.observableArrayList(PromotionDAO.getAllPromotions());
//        promotionTable.setItems(promotionList);
//    }
//
//    private void clearForm() {
//        promotionNameField.clear();
//        discountField.clear();
//        productNameComboBox.getSelectionModel().clearSelection();
//        productNameComboBox.getEditor().clear();
//        startDatePicker.setValue(null);
//        endDatePicker.setValue(null);
//    }
//
//    private void showAlert(String title, String content) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//}