package model;

import java.time.LocalDate;

public class Promotion {
    private int promotionID;
    private String promotionName;
    private String description;
    private double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(int promotionID, String promotionName, String description, double discountPercentage, LocalDate startDate, LocalDate endDate) {
        this.promotionID = promotionID;
        this.promotionName = promotionName;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getPromotionID() { return promotionID; }
    public String getPromotionName() { return promotionName; }
    public String getDescription() { return description; }
    public double getDiscountPercentage() { return discountPercentage; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}