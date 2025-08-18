package controller.admin;

import dao.EmployeeDAO;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Employee;
import model.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
    private User currentUser; // giữ user hiện tại để xử lý mật khẩu trống

    // ==== VALIDATION REGEX ====
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{9,11}$");
    // CMND 9 số hoặc CCCD 12 số
    private static final Pattern IDCARD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @Override
    public void initialize(URL loc, ResourceBundle res) {
        roleBox.getItems().addAll("Manager", "Staff","Cashier", "Warehouse");
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

        // load User info (có thể null nếu chưa có user)
        currentUser = EmployeeDAO.findByEmployeeID(emp.getEmployeeID());
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            // KHÔNG hiển thị mật khẩu cũ; để trống => giữ nguyên
            passwordField.setText("");
            roleBox.setValue(currentUser.getRole());
        } else {
            // Nếu không có user -> để trống, bắt buộc nhập
            usernameField.setText("");
            passwordField.setText("");
            roleBox.setValue(null);
        }
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
    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    @FXML
    private void handleUpdateEmployee(ActionEvent e) {
        // Lấy & trim input
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

        // 1) Required (Password cho phép trống để giữ cũ)
        boolean anyEmpty = false;
        if (fullName.isEmpty()) { markInvalid(fullNameField, true); anyEmpty = true; }
        if (dob == null) { markInvalid(dobPicker, true); anyEmpty = true; }
        if (idCard.isEmpty()) { markInvalid(idCardField, true); anyEmpty = true; }
        if (hometown.isEmpty()) { markInvalid(hometownField, true); anyEmpty = true; }
        if (phone.isEmpty()) { markInvalid(phoneField, true); anyEmpty = true; }
        if (email.isEmpty()) { markInvalid(emailField, true); anyEmpty = true; }
        if (username.isEmpty()) { markInvalid(usernameField, true); anyEmpty = true; }
        if (role == null) { markInvalid(roleBox, true); anyEmpty = true; }

        if (anyEmpty) errs.append("• Please fill in all required fields (*).\n");

        // 2) DOB: quá khứ & >= 16
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

        // 3) ID card
        if (!idCard.isEmpty() && !IDCARD_REGEX.matcher(idCard).matches()) {
            markInvalid(idCardField, true);
            errs.append("• ID card must be 9 or 12 digits (CMND/CCCD).\n");
        }

        // 4) Phone
        if (!phone.isEmpty() && !PHONE_REGEX.matcher(phone).matches()) {
            markInvalid(phoneField, true);
            errs.append("• Phone number must be 9 to 11 digits.\n");
        }

        // 5) Email
        if (!email.isEmpty() && !EMAIL_REGEX.matcher(email).matches()) {
            markInvalid(emailField, true);
            errs.append("• Invalid email format.\n");
        }

        // 6) Username/Password rule
        if (!username.isEmpty() && username.length() < 4) {
            markInvalid(usernameField, true);
            errs.append("• Username must be at least 4 characters.\n");
        }
        if (!password.isEmpty() && password.length() < 6) {
            markInvalid(passwordField, true);
            errs.append("• New password must be at least 6 characters (or leave blank to keep the old one).\n");
        }

        // Có lỗi -> dừng
        if (errs.length() > 0) {
            showAlert(Alert.AlertType.WARNING, errs.toString());
            return;
        }

        // 7) Cập nhật Employee
        employee.setFullName(fullName);
        employee.setDateOfBirth(dob);
        employee.setIdCard(idCard);
        employee.setHometown(hometown);
        employee.setPhone(phone);
        employee.setEmail(email);
        // giữ status cũ
        employee.setStatus(employee.getStatus());

        boolean ok1 = EmployeeDAO.updateEmployee(employee);

        // 8) Chuẩn bị dữ liệu User
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setEmployeeID(employee.getEmployeeID());
            currentUser.setStatus("Active");
        }
        currentUser.setUsername(username);
        currentUser.setRole(role);
        currentUser.setEmail(email);

        // Nếu password trống -> giữ mật khẩu cũ
        if (!password.isEmpty()) {
            currentUser.setPassword(password);
        }

        // 9) Gọi DAO update/insert tương ứng
        boolean ok2;
        if (EmployeeDAO.findByEmployeeID(employee.getEmployeeID()) == null) {
            // chưa có -> insert (yêu cầu phải có password, nếu trống thì báo)
            if (password.isEmpty()) {
                markInvalid(passwordField, true);
                showAlert(Alert.AlertType.WARNING, "• Please enter a password for the new account.");
                return;
            }
            ok2 = UserDAO.insertUser(currentUser);
        } else {
            // có rồi -> update
            ok2 = UserDAO.updateUser(currentUser);
        }

        if (ok1 && ok2) {
            showAlert(Alert.AlertType.INFORMATION, "Update successful!");
            dialogStage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error updating!");
        }
    }

    @FXML
    private void handleCancel(ActionEvent e) {
        dialogStage.close();
    }
}
