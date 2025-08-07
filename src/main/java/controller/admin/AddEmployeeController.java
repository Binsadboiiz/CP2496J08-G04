package controller.admin;

import dao.EmployeeDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Employee;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEmployeeController implements Initializable {
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

    /** Controller cha sẽ gọi phương thức này để set stage,
     *  từ đó chúng ta mới đóng dialog được */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo combo box chức vụ
        roleBox.getItems().addAll("Manager", "Staff", "Cashier", "Warehouse");
    }

    /** Gán sự kiện cho nút Add */
    @FXML
    private void handleAddEmployee(ActionEvent event) {
        // 1. Validate dữ liệu bắt buộc
        if (fullNameField.getText().trim().isEmpty()
                || dobPicker.getValue() == null
                || idCardField.getText().trim().isEmpty()
                || usernameField.getText().trim().isEmpty()
                || passwordField.getText().trim().isEmpty()
                || roleBox.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Please fill in all fields marked *").showAndWait();
            return;
        }

        // 2. Tạo Employee object và gọi DAO insert
        Employee emp = new Employee();
        emp.setFullName(fullNameField.getText().trim());
        emp.setDateOfBirth(dobPicker.getValue());
        emp.setIdCard(idCardField.getText().trim());
        emp.setHometown(hometownField.getText().trim());
        emp.setPhone(phoneField.getText().trim());
        emp.setEmail(emailField.getText().trim());
        emp.setStatus("Active");

        int newEmpId = EmployeeDAO.insertEmployee(emp);
        if (newEmpId <= 0) {
            new Alert(Alert.AlertType.ERROR,
                    "Error adding employee!").showAndWait();
            return;
        }

        // 3. Tạo User object cho bảng User và gọi DAO insert
        User u = new User();
        u.setEmployeeID(newEmpId);
        u.setUsername (usernameField.getText().trim());
        u.setPassword (passwordField.getText().trim());
        u.setRole     (roleBox.getValue());
        u.setEmail    (emp.getEmail());
        u.setStatus   ("Active");

        boolean userOk = UserDAO.insertUser(u);
        if (!userOk) {
            new Alert(Alert.AlertType.ERROR,
                    "User account creation failed!").showAndWait();
            // nếu cần rollback Employee: EmployeeDAO.deleteEmployee(newEmpId);
            return;
        }

        // 4. Thông báo thành công & đóng dialog
        new Alert(Alert.AlertType.INFORMATION,
                "Add staff and account successfully!").showAndWait();
        dialogStage.close();
    }

    /** Gán sự kiện cho nút Cancel */
    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }
}
