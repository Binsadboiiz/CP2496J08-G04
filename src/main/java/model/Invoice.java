package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {
    private int invoiceID;
    private int customerID;
    private int userID;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer updatedBy;

    // Constructors
    public Invoice() {}

    public Invoice(int customerID, int userID, LocalDateTime date, BigDecimal totalAmount, BigDecimal discount, String status) {
        this.customerID = customerID;
        this.userID = userID;
        this.date = date;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.status = status;
    }

    // Getters and Setters
    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
}
