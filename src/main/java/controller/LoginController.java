package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import dao.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private CheckBox rememberMeBox;

    // Preferences node cho app của bạn
    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);

    /** Gọi tự động sau khi FXML load xong */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load prefs
        String savedUser = prefs.get("username", "");
        boolean savedRemember = prefs.getBoolean("remember", false);

        if (savedRemember && !savedUser.isEmpty()) {
            usernameField.setText(savedUser);
            rememberMeBox.setSelected(true);
        }
    }

    @FXML
    private void handlelogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter complete information!");
            return;
        }

        String query = "SELECT * FROM [User] WHERE Username = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Lưu hoặc xoá prefs tuỳ checkbox
                if (rememberMeBox.isSelected()) {
                    prefs.put("username", username);
                    prefs.put("password", password);
                    prefs.putBoolean("remember", true);
                } else {
                    prefs.remove("username");
                    prefs.remove("password");
                    prefs.putBoolean("remember", false);
                }

                // Hiển thị thông báo
                String role = rs.getString("Role");
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Log In Success " + role);

                // Chuyển scene theo role
                if ("Admin".equalsIgnoreCase(role)) {
                    switchScene("/view/admin/SceneAdmin.fxml", event);
                } else if ("Staff".equalsIgnoreCase(role)) {
                    switchScene("/view/staff/SceneStaff.fxml", event);
                }
                else if ("Cashier".equalsIgnoreCase(role)) {
                    switchScene("/view/cashier/SceneCashier.fxml", event);
                }
                else {
                    messageLabel.setText("No permissions specified!");
                }
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Wrong username or password!");
            }

        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Database connection error!");
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
