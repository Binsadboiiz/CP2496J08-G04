package model;

public class Promotion {
    private int promotionID;
    private String promotionName;
    private String description;
    private double discountPercentage;
    private String startDate;
    private String endDate;

    public Promotion(int promotionID, String promotionName, String description, double discountPercentage, String startDate, String endDate) {
        this.promotionID = promotionID;
        this.promotionName = promotionName;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Promotion(String promotionName, String description, double discountPercentage, String startDate, String endDate) {
        this.promotionName = promotionName;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getPromotionID() { return promotionID; }
    public String getPromotionName() { return promotionName; }
    public String getDescription() { return description; }
    public double getDiscountPercentage() { return discountPercentage; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
}
