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
    private String name;   // For BestSellingProduct
    private int sales;     // For BestSellingProduct
    private int stockQuantity;

    // === CONSTRUCTORS ===

    // 1. Full Constructor (12 params)
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

    // 2. Constructor cho DashboardDAO (8 params - CHUẨN hóa thứ tự)
    public Product(int productID, String productCode, String productName, String brand, String type, double price, String description, String image) {
        this.productID = productID;
        this.productCode = productCode;
        this.productName = productName;
        this.brand = brand;
        this.type = type;
        this.price = price;
        this.description = description;
        this.image = image;
    }

    // 3. Constructor cho BestSellingProductDAO (name, sales)
    public Product(String name, int sales) {
        this.name = name;
        this.sales = sales;
    }

    // 4. Default Constructor
    public Product() {
    }
    // 5. Constructor chuẩn 10 tham số (ProductManagement / Promotion dùng)
    public Product(int productID, String productName, String productCode, String brand, String type,
                   double price, String description, String image, String createdAt, String updatedAt) {
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

    public Product(int productID, String productName, String productCode, String brand, String type,
                   double price, String description, String image, String createdAt, String updatedAt,
                   int stockQuantity) {
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
        this.stockQuantity = stockQuantity;
    }

    // === GETTERS & SETTERS ===
    // (Keep as bạn đang có)

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSales() { return sales; }
    public void setSales(int sales) { this.sales = sales; }

    @Override
    public String toString() {
        return productName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
