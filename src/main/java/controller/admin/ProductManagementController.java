package controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

import model.Product;      // chỉnh lại package nếu khác
import dao.ProductDAO;     // DAO hoặc service của bạn
import util.DialogUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.stream.Collectors;

public class ProductManagementController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbBrand;
    @FXML
    private ComboBox<String> cbType;

    @FXML
    private TableView<Product> tblProducts;
    @FXML
    private TableColumn<Product, Integer> colId;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, String> colCode;
    @FXML
    private TableColumn<Product, String> colBrand;
    @FXML
    private TableColumn<Product, String> colType;
    @FXML
    private TableColumn<Product, Number> colPrice;
    @FXML
    private TableColumn<Product, String> colDescription;
    @FXML
    private TableColumn<Product, String> colImage;
    @FXML
    private TableColumn<Product, String> colCreatedAt;
    @FXML
    private TableColumn<Product, String> colUpdatedAt;
    @FXML
    private TableColumn<Product, Integer> colUpdatedBy;


    private ObservableList<Product> masterList = FXCollections.observableArrayList();
    private ObservableList<Product> filteredList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cấu hình cell value
        colId.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        // 2. Load dữ liệu gốc
        loadData();

        // 3. Điền filter combo
        List<String> brands = masterList.stream()
                .map(Product::getBrand)
                .distinct().sorted()
                .collect(Collectors.toList());
        cbBrand.setItems(FXCollections.observableArrayList(brands));

        List<String> types = masterList.stream()
                .map(Product::getType)
                .distinct().sorted()
                .collect(Collectors.toList());
        cbType.setItems(FXCollections.observableArrayList(types));

        // 4. Show data lên table
        tblProducts.setItems(masterList);
    }

    private void loadData() {
        List<Product> list = ProductDAO.getAll();  // gọi DAO lấy từ DB
        masterList.setAll(list);
    }

    @FXML
    private void onSearch(ActionEvent e) {
        String kw = txtSearch.getText().trim().toLowerCase();
        String brand = cbBrand.getValue();
        String type = cbType.getValue();

        filteredList.setAll(masterList.stream()
                .filter(p -> kw.isEmpty()
                        || p.getProductName().toLowerCase().contains(kw)
                        || p.getProductCode().toLowerCase().contains(kw))
                .filter(p -> brand == null || brand.isEmpty() || p.getBrand().equals(brand))
                .filter(p -> type == null || type.isEmpty() || p.getType().equals(type))
                .collect(Collectors.toList()));
        tblProducts.setItems(filteredList);
    }

    @FXML
    private void onRefresh(ActionEvent e) {
        txtSearch.clear();
        cbBrand.getSelectionModel().clearSelection();
        cbType.getSelectionModel().clearSelection();
        loadData();
        tblProducts.setItems(masterList);
    }

    @FXML
    private void onAdd(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/admin/AddProduct.fxml")
            );
            AnchorPane pane = loader.load();

            // Tạo dialog mới
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add Product");
            dialog.setScene(new Scene(pane));

            // truyền stage vào controller
            AddProductController ctl = loader.getController();
            ctl.setDialogStage(dialog);

            // show & chờ
            dialog.showAndWait();

            // reload lại dữ liệu sau khi đóng dialog
            loadData();
            tblProducts.setItems(masterList);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn sản phẩm cần sửa!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/AddProduct.fxml"));
            Parent root = loader.load();
            AddProductController controller = loader.getController();
            controller.setEditProduct(selected);
            Stage dialog = new Stage();
            dialog.setTitle("Edit Product");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void onDelete(ActionEvent e) {
        Product sel = tblProducts.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Vui lòng chọn sản phẩm để delete.");
            return;
        }
        boolean ok = DialogUtil.confirm("Confirm", "Are you sure you want to delete this product?");
        if (ok && ProductDAO.delete(sel.getProductID())) {
            masterList.remove(sel);
            showAlert("Delete successful.");
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
