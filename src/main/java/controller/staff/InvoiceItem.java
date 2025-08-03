package controller.staff;

public class InvoiceItem {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public InvoiceItem(String productName, int quantity, double unitPrice, double totalPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
