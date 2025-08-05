package controller.staff;

import dao.CustomerDAO;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;

public class CustomerManagementController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TextField searchField;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> typeColumn;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customers;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("address")); // Tạm dùng address làm loại khách

        customers = FXCollections.observableArrayList(customerDAO.getAllCustomers());
        customerTable.setItems(customers);
    }

    @FXML
    public void addCustomer() {
        Customer newCustomer = AddCustomerController.showDialog();
        if (newCustomer != null) {
            customerDAO.insertCustomer(newCustomer);
            refresh();
        }
    }

    // Giao diện người dùng sẽ được cập nhật để sử dụng Dialog thay cho TextField trực tiếp
    // Phương thức này có thể sẽ không được sử dụng nữa nếu bạn dùng Dialog
    @FXML
    public void editCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        EditCustomerController.showDialog(selected); // Sử dụng Dialog để sửa thông tin
        customerDAO.updateCustomer(selected);
        refresh();
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