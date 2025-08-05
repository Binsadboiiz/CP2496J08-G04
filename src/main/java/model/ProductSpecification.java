package model;

public class ProductSpecification {
    private int specificationID;
    private int productID;
    private String specificationName;
    private String specificationValue;

    // Constructors
    public ProductSpecification() {}

    public ProductSpecification(int specificationID, int productID, String specificationName, String specificationValue) {
        this.specificationID = specificationID;
        this.productID = productID;
        this.specificationName = specificationName;
        this.specificationValue = specificationValue;
    }

    public ProductSpecification(int productID, String specificationName, String specificationValue) {
        this.productID = productID;
        this.specificationName = specificationName;
        this.specificationValue = specificationValue;
    }

    // Getters and Setters
    public int getSpecificationID() {
        return specificationID;
    }

    public void setSpecificationID(int specificationID) {
        this.specificationID = specificationID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
    }

    public String getSpecificationValue() {
        return specificationValue;
    }

    public void setSpecificationValue(String specificationValue) {
        this.specificationValue = specificationValue;
    }

    @Override
    public String toString() {
        return specificationName + ": " + specificationValue;
    }
}