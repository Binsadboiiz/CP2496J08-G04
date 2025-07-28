package controller.cashier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ReturnPolicyController {

    @FXML private TextField txtInvoiceID;
    @FXML private ComboBox<String> cbProduct;
    @FXML private TextArea txtReturnReason;
    @FXML private Label lblMessage;

    @FXML
    public void initialize() {
        // Load sản phẩm mẫu
        cbProduct.getItems().addAll("Điện thoại Iphone 15 Plus", "Điện thoại Iphone 15 ", "Điện thoại Iphone 15 Pro Max");
    }

    @FXML
    private void submitReturnRequest() {
        String invoiceID = txtInvoiceID.getText();
        String product = cbProduct.getValue();
        String reason = txtReturnReason.getText();

        if (invoiceID.isEmpty() || product == null || reason.isEmpty()) {
            lblMessage.setText("Please enter complete information.");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // Dummy xử lý gửi yêu cầu (sau này có thể kết nối DB)
        System.out.println("Request a return:");
        System.out.println("Invoice code: " + invoiceID);
        System.out.println("Product: " + product);
        System.out.println("Reason: " + reason);

        lblMessage.setText("Return request has been sent!");
        lblMessage.setStyle("-fx-text-fill: green;");

        // Clear form sau khi gửi
        txtInvoiceID.clear();
        cbProduct.getSelectionModel().clearSelection();
        txtReturnReason.clear();
    }
}
