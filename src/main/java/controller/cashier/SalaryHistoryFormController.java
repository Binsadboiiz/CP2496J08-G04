//package controller.cashier;
//
//import dao.EmployeeDAO;
//import dao.SalaryHistoryDAO;
//import dao.DatabaseConnection;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import model.SalaryHistory;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Optional;
//
//public class SalaryHistoryFormController {
//
//    @FXML private ComboBox<String> employeeNameComboBox;
//    @FXML private TextField monthField, yearField, basicSalaryField, workingDaysField, bonusField, penaltyField;
//    @FXML private TableView<SalaryHistory> salaryTable;
//    @FXML private TableColumn<SalaryHistory, Integer> idColumn, monthColumn, yearColumn, workingDaysColumn;
//    @FXML private TableColumn<SalaryHistory, String> nameColumn;
//    @FXML private TableColumn<SalaryHistory, Double> basicSalaryColumn, bonusColumn, penaltyColumn, totalSalaryColumn;
//
//    private ObservableList<String> employeeNames;
//    private ObservableList<SalaryHistory> salaryHistories;
//
//    private SalaryHistoryDAO salaryHistoryDAO;
//    private EmployeeDAO employeeDAO;
//
//    @FXML
//    public void initialize() {
//        try {
//            Connection conn = DatabaseConnection.getConnection();
//            salaryHistoryDAO = new SalaryHistoryDAO(conn);
//            employeeDAO = new EmployeeDAO(conn);
//
//            // Load Employee Names
//            loadEmployeeNames();
//
//            // Load Salary History Table
//            loadSalaryHistories();
//
//            // Bind Table Columns
//            idColumn.setCellValueFactory(cellData -> cellData.getValue().salaryIDProperty().asObject());
//            nameColumn.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
//            monthColumn.setCellValueFactory(cellData -> cellData.getValue().monthProperty().asObject());
//            yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
//            basicSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().basicSalaryProperty().asObject());
//            workingDaysColumn.setCellValueFactory(cellData -> cellData.getValue().workingDaysProperty().asObject());
//            bonusColumn.setCellValueFactory(cellData -> cellData.getValue().bonusProperty().asObject());
//            penaltyColumn.setCellValueFactory(cellData -> cellData.getValue().penaltyProperty().asObject());
//            totalSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().totalSalaryProperty().asObject());
//
//        } catch (SQLException e) {
//            showAlert("Database Error", "Failed to connect to database.");
//            e.printStackTrace();
//        }
//    }
//
//    private void loadEmployeeNames() {
//        List<String> names = employeeDAO.getAllEmployeeNames();
//        employeeNames = FXCollections.observableArrayList(names);
//        employeeNameComboBox.setItems(employeeNames);
//        employeeNameComboBox.setEditable(true);
//    }
//
//    private void loadSalaryHistories() {
//        List<SalaryHistory> list = salaryHistoryDAO.getAllSalaryHistories();
//        salaryHistories = FXCollections.observableArrayList(list);
//        salaryTable.setItems(salaryHistories);
//    }
//
//    @FXML
//    private void handleSave() {
//        String name = employeeNameComboBox.getEditor().getText();
//        int employeeID = employeeDAO.getEmployeeIDByName(name);
//
//        if (employeeID == -1) {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Nhân Viên Mới");
//            alert.setHeaderText("Không tìm thấy nhân viên: " + name);
//            alert.setContentText("Bạn có muốn thêm mới nhân viên này không?");
//
//            ButtonType buttonYes = new ButtonType("Thêm Mới");
//            ButtonType buttonNo = new ButtonType("Hủy");
//            alert.getButtonTypes().setAll(buttonYes, buttonNo);
//
//            Optional<ButtonType> result = alert.showAndWait();
//            if (result.isPresent() && result.get() == buttonYes) {
//                if (employeeDAO.insertNewEmployee(name)) {
//                    showAlert("Thành công", "Đã thêm nhân viên mới.");
//                    loadEmployeeNames();
//                    employeeNameComboBox.getSelectionModel().select(name);
//                    employeeID = employeeDAO.getEmployeeIDByName(name);
//                } else {
//                    showAlert("Lỗi", "Không thể thêm nhân viên mới.");
//                    return;
//                }
//            } else {
//                return;
//            }
//        }
//
//        try {
//            int month = Integer.parseInt(monthField.getText());
//            int year = Integer.parseInt(yearField.getText());
//            double basicSalary = Double.parseDouble(basicSalaryField.getText());
//            int workingDays = Integer.parseInt(workingDaysField.getText());
//            double bonus = Double.parseDouble(bonusField.getText());
//            double penalty = Double.parseDouble(penaltyField.getText());
//            double totalSalary = basicSalary / 26 * workingDays + bonus - penalty;
//
//            SalaryHistory salaryHistory = new SalaryHistory(0, name, month, year, basicSalary, workingDays, bonus, penalty, totalSalary);
//            boolean success = salaryHistoryDAO.insertSalaryHistory(salaryHistory, employeeID);
//
//            if (success) {
//                showAlert("Thành công", "Đã lưu lịch sử lương.");
//                loadSalaryHistories();
//                clearForm();
//            } else {
//                showAlert("Lỗi", "Không thể lưu lịch sử lương.");
//            }
//
//        } catch (NumberFormatException e) {
//            showAlert("Lỗi", "Vui lòng nhập đầy đủ và đúng định dạng số.");
//        }
//    }
//
//    private void clearForm() {
//        employeeNameComboBox.getEditor().clear();
//        monthField.clear();
//        yearField.clear();
//        basicSalaryField.clear();
//        workingDaysField.clear();
//        bonusField.clear();
//        penaltyField.clear();
//    }
//
//    private void showAlert(String title, String content) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//}
