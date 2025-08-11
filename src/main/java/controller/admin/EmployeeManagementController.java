package controller.admin;

import dao.EmployeeDAO;
import dao.UserDAO;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import model.Employee;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeManagementController {
    @FXML private TableView<Employee> tableView;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String>  colName;
    @FXML private TableColumn<Employee, String>  colDob;
    @FXML private TableColumn<Employee, String>  colIdCard;
    @FXML private TableColumn<Employee, String>  colHometown;
    @FXML private TableColumn<Employee, String>  colPhone;
    @FXML private TableColumn<Employee, String>  colEmail;
    @FXML private TableColumn<Employee, String>  colStatus;

    private ObservableList<Employee> data;

    @FXML
    public void initialize() {
        // mapping columns
        colId.setCellValueFactory(cell -> cell.getValue().employeeIDProperty().asObject());
        colName.setCellValueFactory(cell -> cell.getValue().fullNameProperty());
        colDob.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDateOfBirth()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        colIdCard.setCellValueFactory(cell -> cell.getValue().idCardProperty());
        colHometown.setCellValueFactory(cell -> cell.getValue().hometownProperty());
        colPhone.setCellValueFactory(cell -> cell.getValue().phoneProperty());
        colEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());

        loadData();
    }

    public void loadData() {
        List<Employee> list = EmployeeDAO.getAll();
        data = FXCollections.observableArrayList(list);
        tableView.setItems(data);
    }

    @FXML
    private void onRefresh() {
        loadData();
    }

    @FXML
    private void onAddEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/admin/AddEmployee.fxml")
            );
            Parent pane = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add Employee");
            dialog.setScene(new Scene(pane));

            AddEmployeeController ctrl = loader.getController();
            ctrl.setDialogStage(dialog);

            dialog.showAndWait();
            loadData();
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(AlertType.ERROR,
                    "Error loading Add Employee form:\n" + ex.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    private void onEditEmployee() {
        Employee sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(AlertType.WARNING,
                    "Please select staff to repair!").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/admin/EditEmployee.fxml")
            );
            Parent pane = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Edit Employee");
            dialog.setScene(new Scene(pane));

            EditEmployeeController ctrl = loader.getController();
            ctrl.setDialogStage(dialog);
            ctrl.setEmployee(sel);

            dialog.showAndWait();
            loadData();
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(AlertType.ERROR,
                    "Error loading Edit Employee form:\n" + ex.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    private void onDeleteEmployee() {
        Employee sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select 1 employee to delete!").showAndWait();
            return;
        }

        // 1) Không cho xóa nếu nhân viên đã có hóa đơn
        boolean hasInvoices = EmployeeDAO.hasInvoices(sel.getEmployeeID()); // DAO trả boolean, không throws
        if (hasInvoices) {
            new Alert(Alert.AlertType.WARNING,
                    "This employee cannot be deleted because they have sales invoices.")
                    .showAndWait();
            return;
        }

        // 2) Xác nhận xóa
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirm delete employee [" + sel.getFullName() + "]?",
                ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    UserDAO.deleteUserByEmployeeID(sel.getEmployeeID());
                    EmployeeDAO.deleteEmployee(sel.getEmployeeID());

                    loadData();
                    new Alert(Alert.AlertType.INFORMATION, "Deleted successfully.").showAndWait();
                } catch (Exception ex) { // dùng Exception để tránh warning "SQLException is never thrown..."
                    new Alert(Alert.AlertType.ERROR,
                            "Delete failed:\n" + ex.getMessage()).showAndWait();
                }
            }
        });
    }


    private void showEmployeeDialog(String fxmlPath,
                                    String title,
                                    Employee emp) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(fxmlPath)
        );
        Parent pane = loader.load();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setScene(new Scene(pane));

        // Lấy controller chung rồi tuỳ xử lý
        Object controller = loader.getController();
        if (emp == null) {
            // ADD
            AddEmployeeController addCtrl = (AddEmployeeController) controller;
            addCtrl.setDialogStage(dialog);
        } else {
            // EDIT
            EditEmployeeController editCtrl = (EditEmployeeController) controller;
            editCtrl.setDialogStage(dialog);
            editCtrl.setEmployee(emp);
        }

        dialog.showAndWait();
        loadData();
    }

}
