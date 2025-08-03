package controller.cashier;

import dao.RevenueReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.RevenueReport;

import java.time.LocalDate;

public class RevenueReportsController {
    @FXML private TableView<RevenueReport> reportTable;
    @FXML private TableColumn<RevenueReport, Integer> idColumn;
    @FXML private TableColumn<RevenueReport, String> typeColumn;
    @FXML private TableColumn<RevenueReport, LocalDate> dateColumn;
    @FXML private TableColumn<RevenueReport, Double> revenueColumn;
    @FXML private TableColumn<RevenueReport, Integer> invoicesColumn;

    private ObservableList<RevenueReport> reportList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getReportID()).asObject());
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReportType()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getReportDate()));
        revenueColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotalRevenue()).asObject());
        invoicesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTotalInvoices()).asObject());

        loadReports(); // Gọi phương thức để tải báo cáo khi khởi tạo
    }

    private void loadReports() {
        reportList.clear();
        reportList.addAll(RevenueReportDAO.getAllReports()); // Sửa đổi để lấy tất cả báo cáo
        reportTable.setItems(reportList);
    }

    @FXML
    public void handleLoadReport() {
        loadReports(); // Gọi lại phương thức loadReports để tải lại báo cáo
    }
}
