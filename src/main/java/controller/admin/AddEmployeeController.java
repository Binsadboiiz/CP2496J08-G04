package controller.admin;

import dao.EmployeeDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Employee;
import model.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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

    // ==== VALIDATION REGEX ====
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{9,11}$");
    // CMND 9 số hoặc CCCD 12 số
    private static final Pattern IDCARD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");

    public void setDialogStage(Stage stage) { this.dialogStage = stage; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleBox.getItems().addAll("Manager", "Staff", "Cashier", "Warehouse");
    }

    // ==== Helper: đánh dấu invalid (viền đỏ) ====
    private void markInvalid(Control c, boolean invalid) {
        if (invalid) {
            if (!c.getStyleClass().contains("field-error")) c.getStyleClass().add("field-error");
        } else {
            c.getStyleClass().remove("field-error");
        }
    }

    // ==== Helper: show alert chuẩn ====
    private void showAlert(AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        // Trim inputs
        String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String idCard = idCardField.getText() == null ? "" : idCardField.getText().trim();
        String hometown = hometownField.getText() == null ? "" : hometownField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String role = roleBox.getValue();

        // Reset style
        markInvalid(fullNameField, false);
        markInvalid(dobPicker, false);
        markInvalid(idCardField, false);
        markInvalid(hometownField, false);
        markInvalid(phoneField, false);
        markInvalid(emailField, false);
        markInvalid(usernameField, false);
        markInvalid(passwordField, false);
        markInvalid(roleBox, false);

        StringBuilder errs = new StringBuilder();

        // 1) Required
        boolean anyEmpty = false;
        if (fullName.isEmpty()) { markInvalid(fullNameField, true); anyEmpty = true; }
        if (dob == null) { markInvalid(dobPicker, true); anyEmpty = true; }
        if (idCard.isEmpty()) { markInvalid(idCardField, true); anyEmpty = true; }
        if (hometown.isEmpty()) { markInvalid(hometownField, true); anyEmpty = true; }
        if (phone.isEmpty()) { markInvalid(phoneField, true); anyEmpty = true; }
        if (email.isEmpty()) { markInvalid(emailField, true); anyEmpty = true; }
        if (username.isEmpty()) { markInvalid(usernameField, true); anyEmpty = true; }
        if (password.isEmpty()) { markInvalid(passwordField, true); anyEmpty = true; }
        if (role == null) { markInvalid(roleBox, true); anyEmpty = true; }

        if (anyEmpty) errs.append("• Please fill in all required fields (*).\n");

        // 2) DOB: ở quá khứ & >= 16 tuổi
        if (dob != null) {
            if (!dob.isBefore(LocalDate.now())) {
                markInvalid(dobPicker, true);
                errs.append("• Date of birth must be in the past.\n");
            } else {
                int age = Period.between(dob, LocalDate.now()).getYears();
                if (age < 16) {
                    markInvalid(dobPicker, true);
                    errs.append("• Employee must be at least 16 years old.\n");
                }
            }
        }

        // 3) ID card: 9 hoặc 12 số
        if (!idCard.isEmpty() && !IDCARD_REGEX.matcher(idCard).matches()) {
            markInvalid(idCardField, true);
            errs.append("• ID card must be 9 or 12 digits (CMND/CCCD).\n");
        }

        // 4) Phone: 9–11 số
        if (!phone.isEmpty() && !PHONE_REGEX.matcher(phone).matches()) {
            markInvalid(phoneField, true);
            errs.append("• Phone number must be 9 to 11 digits.\n");
        }

        // 5) Email hợp lệ
        if (!email.isEmpty() && !EMAIL_REGEX.matcher(email).matches()) {
            markInvalid(emailField, true);
            errs.append("• Invalid email format.\n");
        }

        // 6) Username/Password tối thiểu 4 ký tự (tuỳ chỉnh)
        if (!username.isEmpty() && username.length() < 4) {
            markInvalid(usernameField, true);
            errs.append("• Username must be at least 4 characters.\n");
        }
        if (!password.isEmpty() && password.length() < 6) {
            markInvalid(passwordField, true);
            errs.append("• Password must be at least 6 characters.\n");
        }

        // Nếu có lỗi -> báo và dừng
        if (errs.length() > 0) {
            showAlert(AlertType.WARNING, errs.toString());
            return;
        }

        // 7) Tạo Employee & insert
        Employee emp = new Employee();
        emp.setFullName(fullName);
        emp.setDateOfBirth(dob);
        emp.setIdCard(idCard);
        emp.setHometown(hometown);
        emp.setPhone(phone);
        emp.setEmail(email);
        emp.setStatus("Active");

        int newEmpId = EmployeeDAO.insertEmployee(emp);
        if (newEmpId <= 0) {
            showAlert(AlertType.ERROR, "Error adding employee!");
            return;
        }

        // 8) Tạo User & insert
        User u = new User();
        u.setEmployeeID(newEmpId);
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(role);
        u.setEmail(email);
        u.setStatus("Active");

        boolean userOk = UserDAO.insertUser(u);
        if (!userOk) {
            showAlert(AlertType.ERROR, "User account creation failed!");
            // Rollback nếu cần:
            // EmployeeDAO.deleteEmployee(newEmpId);
            return;
        }

        showAlert(AlertType.INFORMATION, "Add staff and account successfully!");
        dialogStage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }
}
