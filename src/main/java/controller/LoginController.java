package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import dao.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.net.URL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handlelogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();


        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String query = "SELECT * FROM [User] WHERE Username = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                String role = rs.getString("Role");
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Log In Success " + role);
                // TODO: Chuyển scene hoặc mở dashboard theo role
                if ("Admin".equalsIgnoreCase(role)) {
                    switchScene("/view/AdminView.fxml", event);
                } else if ("Staff".equalsIgnoreCase(role)) {
                    switchScene("/view/StaffView.fxml", event);
                }
                else {
                    messageLabel.setText("Không xác định quyền!");
                }
            } else {
                messageLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
            }

        } catch (SQLException e) {
            messageLabel.setText("Lỗi kết nối CSDL!");
            e.printStackTrace();
        }
    }
    private void switchScene(String fxmlPath, ActionEvent event) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.out.println("FXML file not found: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
