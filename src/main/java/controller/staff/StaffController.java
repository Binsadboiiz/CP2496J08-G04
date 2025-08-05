package controller.staff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class StaffController {

    @FXML
    private AnchorPane contentArea;
    @FXML
    private ComboBox<String> discountComboBox;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField invoiceCountField;

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> colId;
    @FXML
    private TableColumn<Customer, String> colName;
    @FXML
    private TableColumn<Customer, String> colPhone;
    @FXML
    private TableColumn<Customer, String> colEmail;
    @FXML
    private TableColumn<Customer, Date> colRegDate;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;

    private Connection conn;
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();


    public void loadHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/staff/Home.fxml"));
        Parent fxml = loader.load();
        HomeController homeController = loader.getController();
        homeController.setStaffController(this); // Truyền tham chiếu StaffController
        contentArea.getChildren().setAll(fxml);
    }
    public void loadCustomerManagement() throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/view/staff/CustomerManagement.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }
    public void loadCreateInvoice() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/staff/CreateInvoice.fxml"));
        Parent fxml = loader.load();
        CreateInvoiceController createInvoiceController = loader.getController();
        createInvoiceController.setStaffController(this); // Truyền tham chiếu StaffController
        contentArea.getChildren().setAll(fxml);
    }

    public void loadInvoiceHistory() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/staff/InvoiceHistory.fxml"));
        Parent fxml = loader.load();
        contentArea.getChildren().setAll(fxml);
        // Do initialize() của InvoiceHistoryController tự động load dữ liệu, nên không cần làm gì thêm ở đây.
    }

    public void handleLogout(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
    @FXML
    public void setUserInfo(String name, String role) {
        usernameLabel.setText(name);
        roleLabel.setText(role);
    }
}
