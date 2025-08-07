package controller.cashier;

import dao.PromotionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Promotion;

import java.time.LocalDate;

public class PromotionManagementController {

    @FXML private TableView<Promotion> tblPromotion;
    @FXML private TableColumn<Promotion, Integer> colID;
    @FXML private TableColumn<Promotion, String> colName;
    @FXML private TableColumn<Promotion, String> colDesc;
    @FXML private TableColumn<Promotion, Double> colDiscount;
    @FXML private TableColumn<Promotion, LocalDate> colStart;
    @FXML private TableColumn<Promotion, LocalDate> colEnd;

    @FXML private Button btnAdd, btnEdit, btnDelete, btnRefresh;

    private ObservableList<Promotion> promotionList;

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("promotionID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("promotionName"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        loadTable();

        btnAdd.setOnAction(e -> showAddDialog());
        btnEdit.setOnAction(e -> showEditDialog());
        btnDelete.setOnAction(e -> deleteSelected());
        btnRefresh.setOnAction(e -> loadTable());
    }

    private void loadTable() {
        promotionList = FXCollections.observableArrayList(PromotionDAO.getAll());
        tblPromotion.setItems(promotionList);
    }

    private void showAddDialog() {
        // Hiện dialog nhập thông tin (tạo dialog riêng, ví dụ AddPromotionDialog)
        AddPromotionDialog dialog = new AddPromotionDialog();
        Promotion newPromo = dialog.showAndWait();
        if (newPromo != null) {
            if (PromotionDAO.insert(newPromo)) {
                loadTable();
                showAlert("Thêm thành công!");
            } else {
                showAlert("Lỗi khi thêm!");
            }
        }
    }

    private void showEditDialog() {
        Promotion selected = tblPromotion.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn dòng để sửa!");
            return;
        }
        EditPromotionDialog dialog = new EditPromotionDialog(selected);
        Promotion updated = dialog.showAndWait();
        if (updated != null) {
            if (PromotionDAO.update(updated)) {
                loadTable();
                showAlert("Cập nhật thành công!");
            } else {
                showAlert("Lỗi khi cập nhật!");
            }
        }
    }

    private void deleteSelected() {
        Promotion selected = tblPromotion.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn dòng để xóa!");
            return;
        }
        if (PromotionDAO.delete(selected.getPromotionID())) {
            loadTable();
            showAlert("Xóa thành công!");
        } else {
            showAlert("Lỗi khi xóa!");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
