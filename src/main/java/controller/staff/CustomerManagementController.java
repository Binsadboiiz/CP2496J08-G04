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

        // Thiết lập màu sắc cho các nút
        setupButtonStyles();
    }

    private void setupButtonStyles() {
        // Add button - #2584f8 (xanh dương)
        if (addButton != null) {
            addButton.setStyle("-fx-background-color: #2584f8; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        // Refresh button - #3498db (xanh nhạt)
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
                // Thiết lập màu sắc cho nút Edit - #00CC00 (xanh lá)
                editBtn.setStyle("-fx-background-color: #00CC00; -fx-text-fill: white; -fx-font-weight: bold;");

                // Thiết lập màu sắc cho nút Delete - red
                deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");

                editBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    EditCustomerController.showDialog(c);
                    customerDAO.updateCustomer(c);
                    refresh();
                });

                deleteBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());

                    // Kiểm tra xem khách hàng đã có hóa đơn hay chưa
                    if (customerDAO.hasInvoices(c.getCustomerID())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Không thể xóa");
                        alert.setHeaderText("Khách hàng đã có hóa đơn");
                        alert.setContentText("Không thể xóa khách hàng này vì đã có hóa đơn trong hệ thống.");
                        alert.showAndWait();
                        return;
                    }

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Bạn có chắc chắn muốn xóa khách hàng này không?",
                            ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Xác nhận xóa");
                    alert.setHeaderText("Xóa khách hàng");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            if (customerDAO.deleteCustomer(c.getCustomerID())) {
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Thành công");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("Xóa khách hàng thành công!");
                                successAlert.showAndWait();
                                refresh();
                            } else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Lỗi");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Có lỗi xảy ra khi xóa khách hàng!");
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
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Thêm khách hàng thành công!");
                alert.showAndWait();
                refresh();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Có lỗi xảy ra khi thêm khách hàng!");
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