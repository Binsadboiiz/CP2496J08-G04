package controller.staff;

import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

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

    // ===== LOAD FXML VỚI LOADING =====
    public void loadPage(String fxmlPath) {
        ProgressIndicator pi = new ProgressIndicator();
        StackPane stack = new StackPane(pi);
        stack.setPrefSize(contentArea.getWidth(), contentArea.getHeight());
        AnchorPane.setTopAnchor(stack, 0.0);
        AnchorPane.setBottomAnchor(stack, 0.0);
        AnchorPane.setLeftAnchor(stack, 0.0);
        AnchorPane.setRightAnchor(stack, 0.0);
        contentArea.getChildren().setAll(stack);

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent fxml = loader.load();

                // Truyền tham chiếu StaffController nếu là Home hoặc CreateInvoice
                if (fxmlPath.endsWith("Home.fxml")) {
                    HomeController homeController = loader.getController();
                    homeController.setStaffController(StaffController.this);
                } else if (fxmlPath.endsWith("CreateInvoice.fxml")) {
                    CreateInvoiceController createInvoiceController = loader.getController();
                    createInvoiceController.setStaffController(StaffController.this);
                }
                return fxml;
            }
        };
        task.setOnSucceeded(e -> contentArea.getChildren().setAll(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    // ======= Các hàm chuyển tab UI =======
    @FXML
    public void initialize() {
        // Load Home khi khởi động staff UI
        loadHome();
    }

    public void loadHome() {
        loadPage("/view/staff/Home.fxml");
    }
    public void loadCustomerManagement() {
        loadPage("/view/staff/CustomerManagement.fxml");
    }
    public void loadCreateInvoice() {
        loadPage("/view/staff/CreateInvoice.fxml");
    }
    public void loadInvoiceHistory() {
        loadPage("/view/staff/InvoiceHistory.fxml");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setUserInfo(String name, String role) {
        usernameLabel.setText(name);
        roleLabel.setText(role);
    }
}
