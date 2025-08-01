package model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private String productName;
    private double price;
    private int quantity;
    private LocalDate date;

    public Transaction(int id, String productName, double price, int quantity, LocalDate date) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public LocalDate getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setDate(LocalDate date) { this.date = date; }
}
