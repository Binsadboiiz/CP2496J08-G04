package controller.cashier;

import dao.DatabaseConnection;
import dao.ReturnPolicyDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ReturnPolicy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReturnPolicyManagementController {

    @FXML private TableView<ReturnPolicy> returnPolicyTable;
    @FXML private TableColumn<ReturnPolicy, Integer> policyIDColumn;
    @FXML private TableColumn<ReturnPolicy, String> nameColumn;
    @FXML private TableColumn<ReturnPolicy, String> descColumn;
    @FXML private TableColumn<ReturnPolicy, Integer> daysAllowedColumn;

    @FXML private TextField nameField;
    @FXML private TextField descField;
    @FXML private TextField daysAllowedField;

    private ReturnPolicyDAO policyDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            policyDAO = new ReturnPolicyDAO(conn);
            setupTable();
            loadPolicies();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database connection failed.");
        }
    }

    private void setupTable() {
        policyIDColumn.setCellValueFactory(new PropertyValueFactory<>("policyID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("policyName"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        daysAllowedColumn.setCellValueFactory(new PropertyValueFactory<>("daysAllowed"));
    }

    private void loadPolicies() {
        List<ReturnPolicy> policies = policyDAO.getAllPolicies();
        returnPolicyTable.setItems(FXCollections.observableArrayList(policies));
    }

    @FXML
    private void handleAddReturnPolicy() {
        String name = nameField.getText();
        String description = descField.getText();
        String daysAllowedText = daysAllowedField.getText();

        if (name.isEmpty() || description.isEmpty() || daysAllowedText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try {
            int daysAllowed = Integer.parseInt(daysAllowedText);
            ReturnPolicy policy = new ReturnPolicy(name, description, daysAllowed);

            boolean success = policyDAO.insertPolicy(policy);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thêm chính sách trả hàng thành công.");
                clearFields();
                loadPolicies();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thêm thất bại. Vui lòng thử lại.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Số ngày phải là một số hợp lệ.");
        }
    }

    private void clearFields() {
        nameField.clear();
        descField.clear();
        daysAllowedField.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
