package controller.warehousestaff;

import dao.StockEntryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.StockEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockEntryListController {
    @FXML private TableView<StockEntry> table;
    @FXML private TableColumn<StockEntry, Integer> colEntryID;
    @FXML private TableColumn<StockEntry, String> colDate;
    @FXML private TableColumn<StockEntry, String> colSupplier;
    @FXML private TableColumn<StockEntry, String> colUser;
    @FXML private TableColumn<StockEntry, Void> colDetail;
    @FXML private Button btnAdd, btnRefresh;

    private ObservableList<StockEntry> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadTable();
        btnRefresh.setOnAction(e -> loadTable());
        // Đăng ký double click hoặc nút Xem chi tiết
        table.setRowFactory(tv -> {
            TableRow<StockEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onShowDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void loadTable() {
        list.setAll(StockEntryDAO.getAll());
        colEntryID.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getEntryID()).asObject());

        // SỬA: Chỉ hiển thị ngày, không có thời gian
        colDate.setCellValueFactory(cell -> {
            Date date = cell.getValue().getDate();
            if (date != null) {
                // Chỉ lấy phần ngày, bỏ phần thời gian
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return new javafx.beans.property.SimpleStringProperty(dateFormat.format(date));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colSupplier.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSupplierName()));
        colUser.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUserName()));
        table.setItems(list);
    }

    private void onShowDetail(StockEntry entry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/warehousestaff/StockEntryDetail.fxml"));
            Parent root = loader.load();

            // Truyền entryID cho controller detail
            StockEntryDetailController detailController = loader.getController();
            detailController.setEntryID(entry.getEntryID());

            Stage dialog = new Stage();
            dialog.setTitle("Stock Entry Details");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/StockEntryForm.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Add Stock Entry");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            dialog.setResizable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onRefresh() {
        // TODO:
    }
    @FXML
    private void onClose() {
        // TODO:
    }
}
