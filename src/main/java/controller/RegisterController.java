package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;

import java.time.LocalDate;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button backToLoginBtn;
    @FXML private Button registerBtn;

    @FXML
    public void initialize() {
        registerBtn.setOnAction(e -> handleRegister());
        backToLoginBtn.setOnAction(e -> handleBack());
    }

    private void handleRegister() {
        String name = fullNameField.getText();
        LocalDate dob = dobPicker.getValue();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String role = roleCombo.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || dob == null || phone.isEmpty() || email.isEmpty() ||
                role == null || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            showAlert("Lỗi", "Mật khẩu phải có ít nhất 8 ký tự, chứa chữ và số.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Lỗi", "Mật khẩu không khớp.");
            return;
        }

        User user = new User(name, dob, phone, email, role, password);
        System.out.println("Đăng ký thành công: " + user);

        showAlert("Thành công", "Đăng ký thành công!");
        clearFields();
    }

    private void handleBack() {
        System.out.println("Quay lại đăng nhập...");
        // Bạn có thể điều hướng về màn hình login ở đây.
    }

    private void showAlert(String title, String msg) {
        Alert.AlertType type = title.equals("Thành công") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearFields() {
        fullNameField.clear();
        dobPicker.setValue(null);
        phoneField.clear();
        emailField.clear();
        roleCombo.setValue(null);
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
