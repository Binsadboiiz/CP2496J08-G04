// src/model/Product.java
package model;

public class Product {
    private int productID;
    private String productName;
    private String productCode;
    private String brand;
    private String type;
    private double price;
    private String description;
    private String image;
    private String createdAt;
    private String updatedAt;
    private String name;
    private int sales;

    public Product(int productID, String productName, String productCode, String brand, String type, double price, String description, String image, String createdAt, String updatedAt, String name, int sales) {
        this.productID = productID;
        this.productName = productName;
        this.productCode = productCode;
        this.brand = brand;
        this.type = type;
        this.price = price;
        this.description = description;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.sales = sales;
    }
    public Product() {
        // Constructor mặc định (không tham số)
    }

    public Product(String name, int sales) {
        this.name = name;
        this.sales = sales;
    }

    // Getter
    public String getName() {
        return name;
    }

    public int getSales() {
        return sales;
    }

    // Setter (nếu cần)
    public void setName(String name) {
        this.name = name;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public Product(int productID,
                   String productName,
                   String productCode,
                   String brand,
                   String type,
                   double price,
                   String description,
                   String image,
                   String createdAt,
                   String updatedAt) {
        this.productID = productID;
        this.productName = productName;
        this.productCode = productCode;
        this.brand = brand;
        this.type = type;
        this.price = price;
        this.description = description;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters & setters
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    @Override
    public String toString() {
        return productName;
    }
}
