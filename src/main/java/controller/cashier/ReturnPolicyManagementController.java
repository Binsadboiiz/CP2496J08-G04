package controller.cashier;

import dao.ReturnPolicyDAO;
import model.ReturnPolicy;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReturnPolicyManagementController {
    @FXML private TextField policyNameField;
    @FXML private TextField descriptionField; // Đảm bảo đây là TextField
    @FXML private TextField daysAllowedField;
    @FXML private Button addPolicyButton;
    @FXML private TableView<ReturnPolicy> policyTable;
    @FXML private TableColumn<ReturnPolicy, String> policyNameColumn;
    @FXML private TableColumn<ReturnPolicy, String> descriptionColumn;
    @FXML private TableColumn<ReturnPolicy, Integer> daysAllowedColumn;

    @FXML
    public void initialize() {
        policyNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPolicyName()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        daysAllowedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getDaysAllowed()).asObject());

        loadPolicies();
    }

    private void loadPolicies() {
        // Tải các chính sách trả hàng từ cơ sở dữ liệu
        policyTable.getItems().clear();
        policyTable.getItems().addAll(ReturnPolicyDAO.getAllPolicies());
    }

    @FXML
    public void handleAddReturnPolicy() {
        try {
            String policyName = policyNameField.getText();
            String description = descriptionField.getText();
            int daysAllowed = Integer.parseInt(daysAllowedField.getText());

            ReturnPolicy policy = new ReturnPolicy(0, policyName, description, daysAllowed);
            if (ReturnPolicyDAO.addReturnPolicy(policy)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Return policy added successfully!");
                loadPolicies();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add return policy.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number for days allowed.");
        }
    }

    private void clearFields() {
        policyNameField.clear();
        descriptionField.clear();
        daysAllowedField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
