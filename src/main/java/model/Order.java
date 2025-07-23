package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Order {
    private final StringProperty orderId   = new SimpleStringProperty(this, "orderId");
    private final StringProperty customer  = new SimpleStringProperty(this, "customer");
    private final StringProperty status    = new SimpleStringProperty(this, "status");
    private final StringProperty amount    = new SimpleStringProperty(this, "amount");
    private final StringProperty date      = new SimpleStringProperty(this, "date");

    public Order(String orderId, String customer, String status, String amount, String date) {
        this.orderId.set(orderId);
        this.customer.set(customer);
        this.status.set(status);
        this.amount.set(amount);
        this.date.set(date);
    }

    // --- orderId ---
    public String getOrderId() {
        return orderId.get();
    }
    public void setOrderId(String orderId) {
        this.orderId.set(orderId);
    }
    public StringProperty orderIdProperty() {
        return orderId;
    }

    // --- customer ---
    public String getCustomer() {
        return customer.get();
    }
    public void setCustomer(String customer) {
        this.customer.set(customer);
    }
    public StringProperty customerProperty() {
        return customer;
    }

    // --- status ---
    public String getStatus() {
        return status.get();
    }
    public void setStatus(String status) {
        this.status.set(status);
    }
    public StringProperty statusProperty() {
        return status;
    }

    // --- amount ---
    public String getAmount() {
        return amount.get();
    }
    public void setAmount(String amount) {
        this.amount.set(amount);
    }
    public StringProperty amountProperty() {
        return amount;
    }

    // --- date ---
    public String getDate() {
        return date.get();
    }
    public void setDate(String date) {
        this.date.set(date);
    }
    public StringProperty dateProperty() {
        return date;
    }
}

