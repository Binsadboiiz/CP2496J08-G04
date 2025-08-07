//package controller.cashier;
//
//import dao.ReturnPolicyDAO;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import model.ReturnPolicy;
//import dao.DatabaseConnection;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ReturnPolicyController {
//
//    @FXML private TableView<ReturnPolicy> policyTable;
//    @FXML private TableColumn<ReturnPolicy, Integer> idColumn;
//    @FXML private TableColumn<ReturnPolicy, String> nameColumn;
//    @FXML private TableColumn<ReturnPolicy, String> descriptionColumn;
//    @FXML private TableColumn<ReturnPolicy, Integer> daysAllowedColumn;
//
//    @FXML private TextField nameField;
//    @FXML private TextField descriptionField;
//    @FXML private TextField daysAllowedField;
//
//    private ReturnPolicyDAO dao;
//    private ReturnPolicy selectedPolicy;
//
//    @FXML
//    public void initialize() {
//        Connection conn = null;
//        try {
//            conn = DatabaseConnection.getConnection();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        dao = new ReturnPolicyDAO(conn);
//
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("policyID"));
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("policyName"));
//        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
//        daysAllowedColumn.setCellValueFactory(new PropertyValueFactory<>("daysAllowed"));
//
//        policyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//            if (newSelection != null) {
//                selectedPolicy = newSelection;
//                nameField.setText(selectedPolicy.getPolicyName());
//                descriptionField.setText(selectedPolicy.getDescription());
//                daysAllowedField.setText(String.valueOf(selectedPolicy.getDaysAllowed()));
//            }
//        });
//
//        loadPolicies();
//    }
//
//    private void loadPolicies() {
//        List<ReturnPolicy> policies = dao.getAllPolicies();
//        ObservableList<ReturnPolicy> data = FXCollections.observableArrayList(policies);
//        policyTable.setItems(data);
//    }
//
//    @FXML
//    private void handleAddPolicy() {
//        String name = nameField.getText();
//        String desc = descriptionField.getText();
//        int days = Integer.parseInt(daysAllowedField.getText());
//
//        ReturnPolicy policy = new ReturnPolicy(name, desc, days);
//        if (dao.insertPolicy(policy)) {
//            loadPolicies();
//            clearFields();
//        }
//    }
//
//    @FXML
//    private void handleUpdatePolicy() {
//        if (selectedPolicy != null) {
//            selectedPolicy.setPolicyName(nameField.getText());
//            selectedPolicy.setDescription(descriptionField.getText());
//            selectedPolicy.setDaysAllowed(Integer.parseInt(daysAllowedField.getText()));
//
//            if (dao.updatePolicy(selectedPolicy)) {
//                loadPolicies();
//                clearFields();
//                selectedPolicy = null;
//            }
//        } else {
//            showAlert("Please select a policy to update.");
//        }
//    }
//
//    @FXML
//    private void handleDeletePolicy() {
//        if (selectedPolicy != null) {
//            if (dao.deletePolicy(selectedPolicy.getPolicyID())) {
//                loadPolicies();
//                clearFields();
//                selectedPolicy = null;
//            }
//        } else {
//            showAlert("Please select a policy to delete.");
//        }
//    }
//
//    private void clearFields() {
//        nameField.clear();
//        descriptionField.clear();
//        daysAllowedField.clear();
//    }
//
//    private void showAlert(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information");
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//}
