package controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import model.Product;
import dao.ProductDAO;
import util.DialogUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class ProductManagementController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbBrand;
    @FXML private ComboBox<String> cbType;
    @FXML private FlowPane cardContainer;

    private ObservableList<Product> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        setupComboFilters();
        showCards(masterList);
    }

    private void loadData() {
        List<Product> list = ProductDAO.getAll();
        masterList.setAll(list);
    }

    private void setupComboFilters() {
        // Đổ dữ liệu cho brand/type filter
        List<String> brands = masterList.stream().map(Product::getBrand).distinct().sorted().collect(Collectors.toList());
        cbBrand.setItems(FXCollections.observableArrayList(brands));
        List<String> types = masterList.stream().map(Product::getType).distinct().sorted().collect(Collectors.toList());
        cbType.setItems(FXCollections.observableArrayList(types));
    }

    // Hiện danh sách dưới dạng card
    private void showCards(List<Product> products) {
        cardContainer.getChildren().clear();
        for (Product p : products) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/ProductCard.fxml"));
                AnchorPane card = loader.load();
                ProductCardController ctl = loader.getController();
                ctl.setData(p, this);
                cardContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onSearch() {
        String kw = txtSearch.getText().trim().toLowerCase();
        String brand = cbBrand.getValue();
        String type = cbType.getValue();
        List<Product> filtered = masterList.stream()
                .filter(p -> kw.isEmpty() || p.getProductName().toLowerCase().contains(kw) || p.getProductCode().toLowerCase().contains(kw))
                .filter(p -> brand == null || brand.isEmpty() || p.getBrand().equals(brand))
                .filter(p -> type == null || type.isEmpty() || p.getType().equals(type))
                .collect(Collectors.toList());
        showCards(filtered);
    }

    @FXML
    private void onRefresh() {
        txtSearch.clear();
        cbBrand.getSelectionModel().clearSelection();
        cbType.getSelectionModel().clearSelection();
        loadData();
        setupComboFilters();
        showCards(masterList);
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/AddProduct.fxml"));
            AnchorPane pane = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add Product");
            dialog.setScene(new Scene(pane));
            AddProductController ctl = loader.getController();
            ctl.setDialogStage(dialog);
            dialog.showAndWait();
            loadData();
            setupComboFilters();
            showCards(masterList);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Callback từ ProductCardController
    public void editProduct(Product selected) {
        if (selected == null) {
            showAlert("Please select the product you want to repair!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/EditProduct.fxml"));
            Parent root = loader.load();
            AddProductController controller = loader.getController();
            controller.setEditProduct(selected);
            Stage dialog = new Stage();
            dialog.setTitle("Edit Product");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            controller.setDialogStage(dialog);
            dialog.showAndWait();
            loadData();
            setupComboFilters();
            showCards(masterList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(Product sel) {
        if (sel == null) {
            showAlert("Please select a product to delete.");
            return;
        }
        boolean ok = DialogUtil.confirm("Confirm", "Are you sure you want to delete this product?");
        if (ok && ProductDAO.delete(sel.getProductID())) {
            masterList.remove(sel);
            showAlert("Delete successful.");
            setupComboFilters();
            showCards(masterList);
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
