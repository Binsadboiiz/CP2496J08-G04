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
import javafx.scene.control.cell.PropertyValueFactory;


import java.math.BigDecimal;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControlPanelConfigController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, Integer> invoiceIDColumn;
    @FXML private TableColumn<Invoice, String> customerColumn;
    @FXML private TableColumn<Invoice, String> dateColumn;
    @FXML private TableColumn<Invoice, String> totalAmountColumn;
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

            Connection conn = DatabaseConnection.getConnection();
            invoiceDAO = new InvoiceDAO();
            productDAO = new ProductDAO(conn);

            // Set cell value factories dùng getter thường
            invoiceIDColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
            customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            // Custom: Hiển thị ngày dạng dd/MM/yyyy HH:mm
            dateColumn.setCellValueFactory(data -> {
                if (data.getValue().getDate() != null) {
                    String dateString = data.getValue().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    return new javafx.beans.property.SimpleStringProperty(dateString);
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            // Custom: Hiển thị tiền có dấu phẩy
            totalAmountColumn.setCellValueFactory(data -> {
                BigDecimal amount = data.getValue().getTotalAmount();
                String money = (amount != null) ? String.format("%,.0f", amount.doubleValue()) : "";
                return new javafx.beans.property.SimpleStringProperty(money);
            });
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

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

        // Tính tổng doanh thu (dùng BigDecimal)
        BigDecimal totalRevenue = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalRevenueLabel.setText(String.format("%,.0f VNĐ", totalRevenue.doubleValue()));
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
