package model;

public class BestSellingProduct {
    private String productName;
    private int sales;

    public BestSellingProduct(String productName, int sales) {
        this.productName = productName;
        this.sales = sales;
    }

    public String getProductName() {
        return productName;
    }

    public int getSales() {
        return sales;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }
}
