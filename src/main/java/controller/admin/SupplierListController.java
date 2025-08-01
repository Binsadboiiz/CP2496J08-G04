package controller.admin;

import dao.SupplierDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Supplier;

public class SupplierListController {
    @FXML
    private TableView<Supplier> tableSupplier;
    @FXML
    private TableColumn<Supplier, Integer> colCode;
    @FXML
    private TableColumn<Supplier, String> colName;
    @FXML
    private TableColumn<Supplier, String> colContact;
    @FXML
    private TableColumn<Supplier, String> colProducts;
    @FXML
    private TableColumn<Supplier, String> colStatus;
    @FXML
    private TableColumn<Supplier, Void> colActions;

    @FXML
    private Label lblTotal, lblActive, lblInactive, lblResult;

    @FXML
    private Pagination pagination;

    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private static final int ROWS_PER_PAGE = 10;

    @FXML
    public void initialize() {
        loadData();
        setupTable();
        updateCards();
        setupPagination();
        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");

            {
                btnEdit.setOnAction(e -> {
                    Supplier selected = getTableView().getItems().get(getIndex());
                    onEdit(selected);
                });
                btnDelete.setOnAction(e -> {
                    Supplier selected = getTableView().getItems().get(getIndex());
                    onDelete(selected);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(8, btnEdit, btnDelete));
                }
            }
        });
    }

    private void loadData() {
        supplierList.setAll(SupplierDAO.getAll());
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getSupplierID()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colContact.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContactName()));
        colProducts.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("")); // TODO: Hiển thị số sản phẩm nếu có
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isActive() ? "Active" : "Inactive"));
        // Hành động (edit/delete)
        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            {
                btnEdit.setOnAction(e -> onEdit(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> onDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(8, btnEdit, btnDelete);
                    setGraphic(hbox);
                }
            }
        });
        // Bind page đầu tiên
        tableSupplier.setItems(FXCollections.observableArrayList(
                supplierList.subList(0, Math.min(ROWS_PER_PAGE, supplierList.size()))
        ));
    }

    private void updateCards() {
        int total = supplierList.size();
        int active = (int) supplierList.stream().filter(Supplier::isActive).count();
        int inactive = total - active;

        lblTotal.setText(String.valueOf(total));
        lblActive.setText(String.valueOf(active));
        lblInactive.setText(String.valueOf(inactive));
        lblResult.setText("Showing 1 to " + Math.min(ROWS_PER_PAGE, total) + " of " + total + " results");
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) supplierList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> updateTableForPage(newIdx.intValue()));
    }

    private void updateTableForPage(int pageIndex) {
        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, supplierList.size());
        if (from > to) from = to = 0;
        tableSupplier.setItems(FXCollections.observableArrayList(
                supplierList.subList(from, to)
        ));
        lblResult.setText("Showing " + (from + 1) + " to " + to + " of " + supplierList.size() + " results");
    }

    @FXML
    private void onAdd() {
        // TODO: Mở form add supplier mới (dùng dialog hoặc scene khác)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/SupplierForm.fxml"));
            Parent root = loader.load();

            SupplierFormController formController = loader.getController();
            formController.setModeAdd(); // Nếu muốn, set mode để phân biệt add/edit

            Stage dialog = new Stage();
            dialog.setTitle("Add Supplier");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            // Sau khi đóng dialog, reload lại data nếu cần
            loadData();
            setupPagination();
            updateCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onEdit(Supplier supplier) {
        // TODO: Mở form edit supplier
        if (supplier == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/SupplierForm.fxml"));
            Parent root = loader.load();

            SupplierFormController formController = loader.getController();
            formController.setSupplier(supplier); // Truyền dữ liệu để edit

            Stage dialog = new Stage();
            dialog.setTitle("Edit Supplier");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            // Sau khi sửa, reload lại
            loadData();
            setupPagination();
            updateCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onRefresh() {
        loadData();
        setupPagination();
        updateCards();
    }

    private void onDelete(Supplier s) {
        if (s != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this supplier?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    SupplierDAO.softDelete(s.getSupplierID());
                    loadData();
                    setupPagination();
                    updateCards();
                }
            });
        }
    }
}
