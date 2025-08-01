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

    private Connection conn;
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    public void initialize() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=CellPhoneStore;encrypt=false",
                    "sa",         // ✅ user mặc định của MySQL
                    "sqladmin"              // ✅ nếu không có mật khẩu
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (discountComboBox != null) {
            discountComboBox.setItems(FXCollections.observableArrayList("5%", "10%", "15%"));
            discountComboBox.setValue("10%");
        }

        if (customerTable != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colRegDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
            customerTable.setItems(customerList);
        }
    }

    public void createInvoice(ActionEvent event) {
        double total = 5000000.0; // demo: tổng giá trị sản phẩm

        String selectedDiscount = discountComboBox.getValue();
        double discountRate = 0.0;
        if (selectedDiscount != null && selectedDiscount.endsWith("%")) {
            discountRate = Double.parseDouble(selectedDiscount.replace("%", "")) / 100.0;
        }
        double discount = total * discountRate;
        double finalAmount = total - discount;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận tạo hóa đơn");
        confirm.setHeaderText("Tổng tiền sau giảm: " + finalAmount + " VND");
        confirm.setContentText("Bạn có chắc chắn muốn lưu hóa đơn?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Invoice(total, discount, finalAmount) VALUES (?, ?, ?)");
            ps.setDouble(1, total);
            ps.setDouble(2, discount);
            ps.setDouble(3, finalAmount);
            ps.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã tạo hóa đơn thành công");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterCustomersByCriteria() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        int minInvoice = 0;
        try {
            minInvoice = Integer.parseInt(invoiceCountField.getText());
        } catch (NumberFormatException e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Số hóa đơn không hợp lệ");
            a.show();
            return;
        }

        String sql = "SELECT * FROM Customer c WHERE c.registrationDate BETWEEN ? AND ? AND " +
                "(SELECT COUNT(*) FROM Invoice i WHERE i.customerId = c.id) >= ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ps.setInt(3, minInvoice);
            ResultSet rs = ps.executeQuery();

            customerList.clear();
            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("registrationDate")
                );
                customerList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void loadCustomerView() throws IOException {
//        Parent fxml = FXMLLoader.load(getClass().getResource("/view/ManageCustomerView.fxml"));
//        contentArea.getChildren().removeAll();
//        contentArea.getChildren().setAll(fxml);
//    }
    public void loadCustomerManagement() throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/view/staff/CustomerManagement.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }
    public void loadInvoiceHistory() throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/view/staff/InvoiceHistory.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }
    public void loadCreateInvoice() throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/view/staff/CreateInvoice.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleLogout(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}
