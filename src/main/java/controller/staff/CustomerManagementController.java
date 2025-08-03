package controller.staff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerManagementController {

    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> typeColumn;
    @FXML private TableColumn<Customer, Void> actionsColumn;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        customerList.addAll(
                new Customer("Nguyễn Văn A", "0909123456", "Thân thiết"),
                new Customer("Trần Thị B", "0911222333", "VIP")
        );

        customerTable.setItems(customerList);
        addActionButtons();
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");

            {
                editButton.setOnAction(event -> {
                    Customer selected = getTableView().getItems().get(getIndex());
                    EditCustomerController.showDialog(selected); // mở popup sửa
                    customerTable.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        ObservableList<Customer> filtered = customerList.filtered(
                c -> c.getName().toLowerCase().contains(keyword) || c.getType().toLowerCase().contains(keyword)
        );
        customerTable.setItems(filtered);
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        customerTable.setItems(customerList);
    }

    @FXML
    private void handleAddCustomer() {
        Customer newCustomer = AddCustomerController.showDialog();
        if (newCustomer != null) {
            customerList.add(newCustomer);
        }
    }
}
