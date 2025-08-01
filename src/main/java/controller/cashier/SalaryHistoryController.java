package controller.cashier;

import dao.SalaryHistoryDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.SalaryHistory;

import java.util.List;

public class SalaryHistoryController {

    @FXML private TableView<SalaryHistory> salaryTable;
    @FXML private TableColumn<SalaryHistory, String> colEmployee;
    @FXML private TableColumn<SalaryHistory, Integer> colMonth;
    @FXML private TableColumn<SalaryHistory, Integer> colYear;
    @FXML private TableColumn<SalaryHistory, Double> colTotalSalary;

    public void initialize() {
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colTotalSalary.setCellValueFactory(new PropertyValueFactory<>("totalSalary"));

        List<SalaryHistory> list = SalaryHistoryDAO.getAllSalaryHistory();
        salaryTable.getItems().setAll(list);
    }
}
