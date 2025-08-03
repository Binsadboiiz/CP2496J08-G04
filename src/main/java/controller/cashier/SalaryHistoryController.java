package controller.cashier;

import dao.SalaryHistoryDAO;
import model.SalaryHistory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class SalaryHistoryController {

    @FXML
    private TextField employeeIdField;
    @FXML
    private TextField amountField;
    @FXML
    private Button addSalaryButton;
    @FXML
    private TableView<SalaryHistory> salaryTable;
    @FXML
    private TableColumn<SalaryHistory, Integer> employeeIdColumn;
    @FXML
    private TableColumn<SalaryHistory, Double> amountColumn;
    @FXML
    private TableColumn<SalaryHistory, LocalDate> dateColumn;

    @FXML
    public void initialize() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadSalaries();
    }

    private void loadSalaries() {
    }

    @FXML
    public void handleAddSalary() {

    }
}