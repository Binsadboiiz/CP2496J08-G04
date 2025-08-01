package model;

public class Transaction {
    private int invoiceID;
    private String customer;
    private String time;
    private double amount;
    private String status;

    public Transaction(int invoiceID, String customer, String time, double amount, String status) {
        this.invoiceID = invoiceID;
        this.customer = customer;
        this.time = time;
        this.amount = amount;
        this.status = status;
    }

    public int getInvoiceID() { return invoiceID; }
    public String getCustomer() { return customer; }
    public String getTime() { return time; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}
