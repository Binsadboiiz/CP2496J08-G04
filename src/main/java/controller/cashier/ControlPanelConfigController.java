package controller.cashier;

import dao.DatabaseConnection;
import dao.InvoiceDAO;
import dao.ProductDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Invoice;
import model.Product;

import java.sql.Connection;
import java.util.List;

public class ControlPanelConfigController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, Integer> invoiceIDColumn;
    @FXML private TableColumn<Invoice, String> customerColumn;
    @FXML private TableColumn<Invoice, String> dateColumn;
    @FXML private TableColumn<Invoice, Double> totalAmountColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;

    @FXML private Label totalInvoicesLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label productNameLabel;
    @FXML private ImageView productImageView;

    private InvoiceDAO invoiceDAO;
    private ProductDAO productDAO;

    @FXML
    public void initialize() {
        try {
            // Kết nối cơ sở dữ liệu
            Connection conn = DatabaseConnection.getConnection();
            invoiceDAO = new InvoiceDAO(conn);
            productDAO = new ProductDAO(conn);

            // Cấu hình các cột bảng
            invoiceIDColumn.setCellValueFactory(data -> data.getValue().invoiceIDProperty().asObject());
            customerColumn.setCellValueFactory(data -> data.getValue().customerNameProperty());
            dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
            totalAmountColumn.setCellValueFactory(data -> data.getValue().totalAmountProperty().asObject());
            statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

            // Load dữ liệu hóa đơn
            loadInvoiceTable();

            // Gán sự kiện khi click dòng
            invoiceTable.setOnMouseClicked(this::handleInvoiceClick);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInvoiceTable() {
        List<Invoice> invoices = invoiceDAO.getAllInvoicesWithCustomerName();
        invoiceTable.setItems(FXCollections.observableArrayList(invoices));

        // Hiển thị tổng số hóa đơn
        totalInvoicesLabel.setText(String.valueOf(invoices.size()));

        // Tính tổng doanh thu
        double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        totalRevenueLabel.setText(String.format("%,.0f VNĐ", totalRevenue));
    }

    private void handleInvoiceClick(MouseEvent event) {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Lấy sản phẩm đầu tiên thuộc hóa đơn được chọn
            Product product = productDAO.getFirstProductByInvoiceID(selected.getInvoiceID());
            if (product != null) {
                productNameLabel.setText(product.getProductName());
                try {
                    if (product.getImage() != null && !product.getImage().isEmpty()) {
                        productImageView.setImage(new Image(product.getImage(), true));
                    } else {
                        productImageView.setImage(null);
                    }
                } catch (Exception e) {
                    productImageView.setImage(null);
                }
            } else {
                productNameLabel.setText("Không có sản phẩm");
                productImageView.setImage(null);
            }
        }
    }
}
