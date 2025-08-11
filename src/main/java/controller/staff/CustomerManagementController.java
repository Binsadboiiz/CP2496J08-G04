package controller.staff;

import dao.CustomerDAO;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class CustomerManagementController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TextField searchField;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button refreshButton;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customers;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        customers = FXCollections.observableArrayList(customerDAO.getAllCustomers());
        customerTable.setItems(customers);

        actionsColumn.setCellFactory(getActionCellFactory());

        setupButtonStyles();
    }

    private void setupButtonStyles() {
        // Add button - #2584f8 (blue)
        if (addButton != null) {
            addButton.setStyle("-fx-background-color: #2584f8; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        // Refresh button - #3498db (light blue)
        if (refreshButton != null) {
            refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    private Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> getActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);

            {
                // Edit button - #00CC00 (green)
                editBtn.setStyle("-fx-background-color: #00CC00; -fx-text-fill: white; -fx-font-weight: bold;");

                // Delete button - red
                deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");

                editBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    EditCustomerController.showDialog(c);
                    customerDAO.updateCustomer(c);
                    refresh();
                });

                deleteBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());

                    if (customerDAO.hasInvoices(c.getCustomerID())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Cannot Delete");
                        alert.setHeaderText("Customer has invoices");
                        alert.setContentText("Cannot delete this customer because they have invoices in the system.");
                        alert.showAndWait();
                        return;
                    }

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this customer?",
                            ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Confirm Deletion");
                    alert.setHeaderText("Delete Customer");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            if (customerDAO.deleteCustomer(c.getCustomerID())) {
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Success");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("Customer deleted successfully!");
                                successAlert.showAndWait();
                                refresh();
                            } else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("An error occurred while deleting the customer!");
                                errorAlert.showAndWait();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    @FXML
    public void addCustomer() {
        Customer newCustomer = AddCustomerController.showDialog();
        if (newCustomer != null) {
            if (customerDAO.insertCustomer(newCustomer)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Customer added successfully!");
                alert.showAndWait();
                refresh();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while adding the customer!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void searchCustomer() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refresh();
        } else {
            customers.setAll(customerDAO.searchCustomers(keyword));
        }
    }

    @FXML
    private void refresh() {
        customers.setAll(customerDAO.getAllCustomers());
    }
}