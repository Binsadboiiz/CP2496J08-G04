package controller.warehousestaff;

import dao.StockEntryDetailDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.StockEntryDetail;

import java.util.List;

public class StockEntryDetailController {
    @FXML private TableView<StockEntryDetail> tableDetail;
    @FXML private TableColumn<StockEntryDetail, String> colProduct;
    @FXML private TableColumn<StockEntryDetail, Integer> colQty;
    @FXML private TableColumn<StockEntryDetail, Double> colUnitCost;
    @FXML private Label lblTitle;
    @FXML private Label lblTotal;

    private int entryID;

    public void setEntryID(int entryID) {
        this.entryID = entryID;
        loadDetails();
    }

    private void loadDetails() {
        List<StockEntryDetail> list = StockEntryDetailDAO.getByEntryID(entryID);
        tableDetail.setItems(FXCollections.observableArrayList(list));
        double sum = list.stream().mapToDouble(d -> d.getQuantity() * d.getUnitCost()).sum();
        lblTotal.setText(String.format("%.2f", sum));
    }

    @FXML
    private void initialize() {
        colProduct.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProductName()));
        colQty.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colUnitCost.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getUnitCost()).asObject());
        Stage dialog = new Stage();
        dialog.setResizable(false);
    }

    @FXML
    private void onClose() {
        ((Stage) tableDetail.getScene().getWindow()).close();
    }
}
