package controller.cashier;

import dao.SalaryHistoryDAO;
import dao.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.SalaryHistory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SalaryHistoryController {

    @FXML private ComboBox<String> employeeNameCombo;
    @FXML private TextField monthField;
    @FXML private TextField yearField;
    @FXML private TextField basicSalaryField;
    @FXML private TextField workingDaysField;
    @FXML private TextField bonusField;
    @FXML private TextField penaltyField;

    private SalaryHistoryDAO dao;

    @FXML
    public void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection(); // Có thể ném SQLException
            dao = new SalaryHistoryDAO(conn);
            loadEmployeeNames();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to database.");
        }
    }

    private void loadEmployeeNames() {
        try {
            List<String> names = dao.getAllEmployeeNames();
            employeeNameCombo.getItems().setAll(names);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load employee names.");
        }
    }

    @FXML
    public void handleAddSalary() {
        try {
            String employeeName = employeeNameCombo.getValue();
            if (employeeName == null || employeeName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select an employee.");
                return;
            }

            int month = Integer.parseInt(monthField.getText());
            int year = Integer.parseInt(yearField.getText());
            double basicSalary = Double.parseDouble(basicSalaryField.getText());
            int workingDays = Integer.parseInt(workingDaysField.getText());
            double bonus = Double.parseDouble(bonusField.getText());
            double penalty = Double.parseDouble(penaltyField.getText());

            double totalSalary = basicSalary / 26 * workingDays + bonus - penalty;

            SalaryHistory salary = new SalaryHistory(0, employeeName, month, year, basicSalary, workingDays, bonus, penalty, totalSalary);

            int employeeID = dao.getEmployeeIDByName(employeeName);
            if (employeeID == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Employee not found in database.");
                return;
            }

            if (dao.insertSalaryHistory(salary, employeeID)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Salary history added successfully.");
                closeForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to add salary history.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers.");
        }
    }

    private void closeForm() {
        Stage stage = (Stage) employeeNameCombo.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
