package controller.staff;

import javafx.beans.property.*;

import java.util.Arrays;
import java.util.List;

public class InvoiceRecord {
    private final StringProperty invoiceId;
    private final StringProperty customerName;
    private final StringProperty productList;
    private final StringProperty date;
    private final DoubleProperty totalAmount;

    public InvoiceRecord(String id, String customer, String products, String date, double total) {
        this.invoiceId = new SimpleStringProperty(id);
        this.customerName = new SimpleStringProperty(customer);
        this.productList = new SimpleStringProperty(products);
        this.date = new SimpleStringProperty(date);
        this.totalAmount = new SimpleDoubleProperty(total);
    }

    public StringProperty invoiceIdProperty() { return invoiceId; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty productListProperty() { return productList; }
    public StringProperty dateProperty() { return date; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }

    public String getCustomerName() { return customerName.get(); }
    public String getProductList() { return productList.get(); }
    public String getDate() { return date.get(); }

    public static List<InvoiceRecord> sampleData() {
        return Arrays.asList(
                new InvoiceRecord("HD001", "Nguyễn Văn A", "Sản phẩm X, Sản phẩm Y", "2025-07-20", 150000),
                new InvoiceRecord("HD002", "Trần Thị B", "Sản phẩm Z", "2025-07-21", 80000),
                new InvoiceRecord("HD003", "Lê Văn C", "Sản phẩm X", "2025-07-22", 100000)
        );
    }
}
