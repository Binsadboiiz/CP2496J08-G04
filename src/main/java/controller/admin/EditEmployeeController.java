package controller.admin;

import dao.EmployeeDAO;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Employee;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class EditEmployeeController implements Initializable {
    @FXML private TextField fullNameField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField idCardField;
    @FXML private TextField hometownField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;

    private Stage dialogStage;
    private Employee employee;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @Override
    public void initialize(URL loc, ResourceBundle res) {
        roleBox.getItems().addAll("Admin", "Staff","Cashier", "Warehouse");
    }

    public void setEmployee(Employee emp) {
        this.employee = emp;
        // populate fields
        fullNameField.setText(emp.getFullName());
        dobPicker.setValue(emp.getDateOfBirth());
        idCardField.setText(emp.getIdCard());
        hometownField.setText(emp.getHometown());
        phoneField.setText(emp.getPhone());
        emailField.setText(emp.getEmail());

        // load User info
        User u = UserDAO.findByEmployeeID(emp.getEmployeeID());
        usernameField.setText(u.getUsername());
        passwordField.setText(u.getPassword());
        roleBox.setValue(u.getRole());
    }

    @FXML
    private void handleUpdateEmployee(ActionEvent e) {
        // validate similar Add...
        employee.setFullName(fullNameField.getText().trim());
        employee.setDateOfBirth(dobPicker.getValue());
        employee.setIdCard(idCardField.getText().trim());
        employee.setHometown(hometownField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setStatus(employee.getStatus());

        boolean ok1 = EmployeeDAO.updateEmployee(employee);
        // update User
        User u = new User();
        u.setEmployeeID(employee.getEmployeeID());
        u.setUsername(usernameField.getText().trim());
        u.setPassword(passwordField.getText().trim());
        u.setRole(roleBox.getValue());
        u.setEmail(employee.getEmail());
        u.setStatus("Active");
        boolean ok2 = UserDAO.updateUser(u);

        if (ok1 && ok2) {
            new Alert(Alert.AlertType.INFORMATION, "Update successful!").showAndWait();
            dialogStage.close();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error updating!").showAndWait();
        }
    }

    @FXML
    private void handleCancel(ActionEvent e) {
        dialogStage.close();
    }
}
