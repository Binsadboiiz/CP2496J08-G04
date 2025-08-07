package model;

public class TransactionDetail {
    private int invoiceID;
    private String productName;
    private String customerName;
    private String paymentMethod;
    private int quantity;
    private double totalAmount;

    // Constructor without InvoiceID (optional)
    public TransactionDetail(String productName, double totalAmount, String customerName, String paymentMethod, int quantity) {
        this.productName = productName;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
    }

    // Constructor with InvoiceID (if needed)
    public TransactionDetail(int invoiceID, String productName, double totalAmount, String customerName, String paymentMethod, int quantity) {
        this.invoiceID = invoiceID;
        this.productName = productName;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
    }

    // Getters
    public int getInvoiceID() {
        return invoiceID;
    }

    public String getProductName() {
        return productName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    // Optional: Setters (if needed)
    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
