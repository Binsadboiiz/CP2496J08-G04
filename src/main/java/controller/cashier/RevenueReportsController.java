package controller.cashier;

import dao.RevenueReportsDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.RevenueReport;

import java.util.List;

public class RevenueReportsController {

    @FXML private TableView<RevenueReport> reportTable;
    @FXML private TableColumn<RevenueReport, String> colReportType;
    @FXML private TableColumn<RevenueReport, String> colReportDate;
    @FXML private TableColumn<RevenueReport, Double> colTotalRevenue;
    @FXML private TableColumn<RevenueReport, Integer> colTotalInvoices;
    @FXML private TableColumn<RevenueReport, String> colTopSellingProduct;

    public void initialize() {
        colReportType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colReportDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        colTotalRevenue.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        colTotalInvoices.setCellValueFactory(new PropertyValueFactory<>("totalInvoices"));
        colTopSellingProduct.setCellValueFactory(new PropertyValueFactory<>("topSellingProduct"));

        List<RevenueReport> list = RevenueReportsDAO.getAllRevenueReports();
        reportTable.getItems().setAll(list);
    }
}
