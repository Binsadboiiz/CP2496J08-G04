//package controller.cashier;
//
//import dao.ReturnPolicyDAO;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import model.ReturnPolicy;
//
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
//    @FXML private Button addButton;
//    @FXML private Button updateButton;
//    @FXML private Button deleteButton;
//
//    private ReturnPolicyDAO dao = new ReturnPolicyDAO();
//    private ReturnPolicy selectedPolicy = null;
//
//    @FXML
//    public void initialize() {
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
//    public void handleAddPolicy() {
//        String name = nameField.getText();
//        String desc = descriptionField.getText();
//        int days = Integer.parseInt(daysAllowedField.getText());
//
//        ReturnPolicy policy = new ReturnPolicy(0, name, desc, days);
//        if (dao.insertPolicy(policy)) {
//            loadPolicies();
//            clearFields();
//        }
//    }
//
////    @FXML
////    public void handleUpdatePolicy() {
////        if (selectedPolicy != null) {
////            selectedPolicy.setPolicyName(nameField.getText());
////            selectedPolicy.setDescription(descriptionField.getText());
////            selectedPolicy.setDaysAllowed(Integer.parseInt(daysAllowedField.getText()));
////
////            if (dao.updatePolicy(selectedPolicy)) {
////                loadPolicies();
////                clearFields();
////                selectedPolicy = null;
////            }
////        } else {
////            showAlert("Please select a policy to update.");
////        }
////    }
//
//    @FXML
//    public void handleDeletePolicy() {
//        if (selectedPolicy != null) {
//            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
//            confirm.setTitle("Delete Confirmation");
//            confirm.setHeaderText(null);
//            confirm.setContentText("Are you sure you want to delete this policy?");
//
//            confirm.showAndWait().ifPresent(response -> {
//                if (response == ButtonType.OK) {
//                    if (dao.deletePolicy(selectedPolicy.getPolicyID())) {
//                        loadPolicies();
//                        clearFields();
//                        selectedPolicy = null;
//                        showAlert("Policy deleted successfully.");
//                    } else {
//                        showAlert("Failed to delete policy.");
//                    }
//                }
//            });
//
//        } else {
//            showAlert("Please select a policy to delete.");
//        }
//    }
//
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
//
//    @FXML
//    private void handleToday(javafx.event.ActionEvent event) {
//        System.out.println("Today button clicked!");
//    }
//}
