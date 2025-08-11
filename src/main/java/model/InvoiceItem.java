package model;

public class InvoiceItem {
    private final String productName;
    private final int quantity;
    private final double price;
    private final int productID;

    public InvoiceItem(String productName, int quantity, double price, int productID) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.productID = productID;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return quantity * price; }
    public int getProductID() { return productID; }
}
