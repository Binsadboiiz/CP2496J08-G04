package model;

import java.time.LocalDate;

public class Transaction {
    private int transactionID;
    private String customerName;
    private LocalDate date;
    private double totalAmount;
    private String productName;
    private double price;
    private int quantity;

    // Constructor 6 tham số (Chi tiết sản phẩm)
    public Transaction(int transactionID, String customerName, String productName, double price, int quantity, LocalDate date) {
        this.transactionID = transactionID;
        this.customerName = customerName;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
        this.totalAmount = price * quantity;
    }

    // Constructor 5 tham số (Không có customerName - khách lẻ)
    public Transaction(int transactionID, String productName, double price, int quantity, LocalDate date) {
        this.transactionID = transactionID;
        this.customerName = "Walk-in Customer";
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
        this.totalAmount = price * quantity;
    }

    // Constructor 4 tham số (Dùng cho danh sách hóa đơn tổng quát)
    public Transaction(int transactionID, String customerName, LocalDate date, double totalAmount) {
        this.transactionID = transactionID;
        this.customerName = customerName;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    // Getters & Setters...
    public int getTransactionID() { return transactionID; }
    public String getCustomerName() { return customerName; }
    public LocalDate getDate() { return date; }
    public double getTotalAmount() { return totalAmount; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setPrice(double price) { this.price = price; }
}
