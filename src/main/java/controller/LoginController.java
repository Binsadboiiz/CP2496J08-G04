package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        DatabaseConnection dbConn = new DatabaseConnection();
        Connection conn = dbConn.getConnection();

        String query = "SELECT * FROM [User] WHERE Username = ? AND Password = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String role = rs.getString("Role");
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Đăng nhập thành công! Quyền: " + role);
                // TODO: chuyển qua trang chính hoặc phân quyền theo Role
            } else {
                messageLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
            }

        } catch (Exception e) {
            messageLabel.setText("Lỗi: " + e.getMessage());
            e.printStackTrace();

        }
    }
}
