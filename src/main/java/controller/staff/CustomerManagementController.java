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
    }

    private Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> getActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button deleteBtn = new Button("Xóa");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    EditCustomerController.showDialog(c);
                    customerDAO.updateCustomer(c);
                    refresh();
                });

                deleteBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa khách hàng này?", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText(null);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            customerDAO.deleteCustomer(c.getCustomerID());
                            refresh();
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
            customerDAO.insertCustomer(newCustomer);
            refresh();
        }
    }

    @FXML
    public void searchCustomer() {
        customers.setAll(customerDAO.searchCustomers(searchField.getText()));
    }

    @FXML
    private void refresh() {
        customers.setAll(customerDAO.getAllCustomers());
    }
}
