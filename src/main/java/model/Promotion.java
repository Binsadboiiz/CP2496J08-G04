package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Promotion {

    private final IntegerProperty promotionID;
    private final StringProperty promotionName;
    private final IntegerProperty appliedProductID;
    private final DoubleProperty discountPercent;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;

    public Promotion(int promotionID, String promotionName, int appliedProductID, double discountPercent, LocalDate startDate, LocalDate endDate) {
        this.promotionID = new SimpleIntegerProperty(promotionID);
        this.promotionName = new SimpleStringProperty(promotionName);
        this.appliedProductID = new SimpleIntegerProperty(appliedProductID);
        this.discountPercent = new SimpleDoubleProperty(discountPercent);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
    }

    // Getters (Property for Table Binding)
    public IntegerProperty promotionIDProperty() { return promotionID; }
    public StringProperty promotionNameProperty() { return promotionName; }
    public IntegerProperty appliedProductIDProperty() { return appliedProductID; }
    public DoubleProperty discountPercentProperty() { return discountPercent; }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }

    // Normal Getters
    public int getPromotionID() { return promotionID.get(); }
    public String getPromotionName() { return promotionName.get(); }
    public int getAppliedProductID() { return appliedProductID.get(); }
    public double getDiscountPercent() { return discountPercent.get(); }
    public LocalDate getStartDate() { return startDate.get(); }
    public LocalDate getEndDate() { return endDate.get(); }

    // Setters
    public void setPromotionID(int value) { promotionID.set(value); }
    public void setPromotionName(String value) { promotionName.set(value); }
    public void setAppliedProductID(int value) { appliedProductID.set(value); }
    public void setDiscountPercent(double value) { discountPercent.set(value); }
    public void setStartDate(LocalDate value) { startDate.set(value); }
    public void setEndDate(LocalDate value) { endDate.set(value); }
}
