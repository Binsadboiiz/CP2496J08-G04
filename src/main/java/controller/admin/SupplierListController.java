package controller.admin;

import dao.SupplierDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
    @FXML private TableView<Supplier> tableSupplier;
    @FXML private TableColumn<Supplier, Integer> colCode;
    @FXML private TableColumn<Supplier, String>  colName;
    @FXML private TableColumn<Supplier, String>  colContact;
    @FXML private TableColumn<Supplier, String>  colProducts;
    @FXML private TableColumn<Supplier, String>  colStatus;
    @FXML private TableColumn<Supplier, Void>    colActions;

    @FXML private Label lblTotal, lblActive, lblInactive, lblResult;
    @FXML private Pagination pagination;

    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10;
    private boolean paginationBound = false;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        setupPagination();
        updateCards();
    }

    /* DATA */
    private void loadData() {
        supplierList.setAll(SupplierDAO.getAll());
    }

    /* TABLE */
    private void setupTable() {
        colCode.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getSupplierID()).asObject());
        colName.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getName())));
        colContact.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getContactName())));
        colProducts.setCellValueFactory(d -> new SimpleStringProperty("")); // TODO: bind số sản phẩm nếu có
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isActive() ? "Active" : "Inactive"));

        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button btnEdit   = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            {
                btnEdit.setOnAction(e -> {
                    Supplier s = getTableView().getItems().get(getIndex());
                    onEdit(s);
                });
                btnDelete.setOnAction(e -> {
                    Supplier s = getTableView().getItems().get(getIndex());
                    onDelete(s);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(8, btnEdit, btnDelete));
            }
        });
    }

    /* PAGINATION */
    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) supplierList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);

        if (!paginationBound) {
            pagination.currentPageIndexProperty().addListener((obs, o, n) ->
                    updateTableForPage(n == null ? 0 : n.intValue()));
            paginationBound = true;
        }

        pagination.setCurrentPageIndex(0);
        updateTableForPage(0);
    }

    private void updateTableForPage(int pageIndex) {
        int from = pageIndex * ROWS_PER_PAGE;
        int to   = Math.min(from + ROWS_PER_PAGE, supplierList.size());
        if (from > to) { from = 0; to = Math.min(ROWS_PER_PAGE, supplierList.size()); }

        tableSupplier.setItems(FXCollections.observableArrayList(supplierList.subList(from, to)));

        lblResult.setText(
                supplierList.isEmpty()
                        ? "No results"
                        : "Showing " + (from + 1) + " to " + to + " of " + supplierList.size() + " results"
        );
    }

    private void refreshKeepPage() {
        int current = pagination.getCurrentPageIndex();
        loadData();

        int pc = (int) Math.ceil((double) supplierList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pc == 0 ? 1 : pc);

        int target = Math.min(current, Math.max(pc - 1, 0));
        pagination.setCurrentPageIndex(target);
        updateTableForPage(target);
        updateCards();
    }

    /* CARDS */
    private void updateCards() {
        int total    = supplierList.size();
        int active   = (int) supplierList.stream().filter(Supplier::isActive).count();
        int inactive = total - active;

        if (lblTotal   != null) lblTotal.setText(String.valueOf(total));
        if (lblActive  != null) lblActive.setText(String.valueOf(active));
        if (lblInactive!= null) lblInactive.setText(String.valueOf(inactive));
    }

    /* HANDLERS */
    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/SupplierForm.fxml"));
            Parent root = loader.load();

            SupplierFormController form = loader.getController();
            form.setModeAdd();

            Stage dialog = new Stage();
            dialog.setTitle("Add Supplier");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            if (form.isSaved()) {
                refreshKeepPage();
                new Alert(Alert.AlertType.INFORMATION, "Supplier added successfully!", ButtonType.OK).showAndWait();
            } else if (form.getLastErrorMessage() != null) {
                new Alert(Alert.AlertType.ERROR, form.getLastErrorMessage(), ButtonType.OK).showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot open Add Supplier form.", ButtonType.OK).showAndWait();
        }
    }

    private void onEdit(Supplier supplier) {
        if (supplier == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/SupplierForm.fxml"));
            Parent root = loader.load();

            SupplierFormController form = loader.getController();
            form.setSupplier(supplier);

            Stage dialog = new Stage();
            dialog.setTitle("Edit Supplier");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            if (form.isSaved()) {
                refreshKeepPage();
                new Alert(Alert.AlertType.INFORMATION, "Supplier updated successfully!", ButtonType.OK).showAndWait();
            } else if (form.getLastErrorMessage() != null) {
                new Alert(Alert.AlertType.ERROR, form.getLastErrorMessage(), ButtonType.OK).showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot open Edit Supplier form.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void onRefresh() {
        refreshKeepPage();
    }

    private void onDelete(Supplier s) {
        if (s == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "This will permanently delete this supplier. Continue?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Supplier");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = SupplierDAO.softDelete(s.getSupplierID()); // xóa cứng
                if (ok) {
                    refreshKeepPage();
                    new Alert(Alert.AlertType.INFORMATION, "Deleted successfully!", ButtonType.OK).showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Delete failed!", ButtonType.OK).showAndWait();
                }
            }
        });
    }

    private static String nvl(String s) { return s == null ? "" : s; }
}
