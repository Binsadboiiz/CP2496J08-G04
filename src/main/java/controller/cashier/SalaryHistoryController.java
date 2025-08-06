package controller.cashier;

import dao.SalaryHistoryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.SalaryHistory;
import java.io.IOException;

public class SalaryHistoryController {

    @FXML private TableView<SalaryHistory> salaryTable;
    @FXML private TableColumn<SalaryHistory, Integer> idColumn;
    @FXML private TableColumn<SalaryHistory, String> nameColumn;
    @FXML private TableColumn<SalaryHistory, Integer> monthColumn;
    @FXML private TableColumn<SalaryHistory, Integer> yearColumn;
    @FXML private TableColumn<SalaryHistory, Double> basicSalaryColumn;
    @FXML private TableColumn<SalaryHistory, Integer> workingDaysColumn;
    @FXML private TableColumn<SalaryHistory, Double> bonusColumn;
    @FXML private TableColumn<SalaryHistory, Double> penaltyColumn;
    @FXML private TableColumn<SalaryHistory, Double> totalSalaryColumn;

    private ObservableList<SalaryHistory> salaryData;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("salaryID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        basicSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
        workingDaysColumn.setCellValueFactory(new PropertyValueFactory<>("workingDays"));
        bonusColumn.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        penaltyColumn.setCellValueFactory(new PropertyValueFactory<>("penalty"));
        totalSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("totalSalary"));

        loadSalaryHistories();
    }

    private void loadSalaryHistories() {
        salaryData = FXCollections.observableArrayList(SalaryHistoryDAO.getAllSalaryHistories());
        salaryTable.setItems(salaryData);
    }

    @FXML
    private void openAddSalaryForm() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/cashier/SalaryHistoryForm.fxml"));  // <== Quan trọng!
            Parent form = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Lịch Sử Lương");
            stage.setScene(new Scene(form));
            stage.showAndWait();

            // Sau khi đóng form, reload lại bảng
            loadSalaryHistoryTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSalaryHistoryTable() {

    }

}
