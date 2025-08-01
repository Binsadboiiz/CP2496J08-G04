// src/model/RevenueReport.java
package model;

public class RevenueReport {
    private String date;
    private String product;
    private double amount;
    private String paymentMethod;

    public RevenueReport(String date, String product, double amount, String paymentMethod) {
        this.date = date;
        this.product = product;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
