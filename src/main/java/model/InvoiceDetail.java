package model;

import java.math.BigDecimal;

public class InvoiceDetail {
    private int invoiceDetailID;
    private int invoiceID;
    private int productID;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;

    // Constructors
    public InvoiceDetail() {}

    public InvoiceDetail(int invoiceID, int productID, int quantity, BigDecimal unitPrice, BigDecimal discount) {
        this.invoiceID = invoiceID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
    }

    // Getters and Setters
    public int getInvoiceDetailID() { return invoiceDetailID; }
    public void setInvoiceDetailID(int invoiceDetailID) { this.invoiceDetailID = invoiceDetailID; }

    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
}
