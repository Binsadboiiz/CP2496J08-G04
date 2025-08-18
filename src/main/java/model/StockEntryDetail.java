package model;

public class StockEntryDetail {
    private int entryDetailID;
    private int entryID;
    private int productID;
    private int quantity;
    private double unitCost;

    private String productName;

    public StockEntryDetail() {}

    public StockEntryDetail(int entryDetailID, int entryID, int productID, int quantity, double unitCost, String productName) {
        this.entryDetailID = entryDetailID;
        this.entryID = entryID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.productName = productName;
    }

    public int getEntryDetailID() {
        return entryDetailID;
    }

    public void setEntryDetailID(int entryDetailID) {
        this.entryDetailID = entryDetailID;
    }

    public int getEntryID() {
        return entryID;
    }

    public void setEntryID(int entryID) {
        this.entryID = entryID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
