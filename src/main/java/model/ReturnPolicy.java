package model;

import javafx.beans.property.*;

public class ReturnPolicy {
    private final IntegerProperty policyID;
    private final StringProperty policyName;
    private final StringProperty description;
    private final IntegerProperty daysAllowed;

    // Constructor đầy đủ
    public ReturnPolicy(int policyID, String policyName, String description, int daysAllowed) {
        this.policyID = new SimpleIntegerProperty(policyID);
        this.policyName = new SimpleStringProperty(policyName);
        this.description = new SimpleStringProperty(description);
        this.daysAllowed = new SimpleIntegerProperty(daysAllowed);
    }

    // Constructor không có ID (dùng cho Insert)
    public ReturnPolicy(String policyName, String description, int daysAllowed) {
        this(0, policyName, description, daysAllowed);
    }

    // Getters & Setters (JavaFX Property)
    public int getPolicyID() { return policyID.get(); }
    public IntegerProperty policyIDProperty() { return policyID; }

    public String getPolicyName() { return policyName.get(); }
    public StringProperty policyNameProperty() { return policyName; }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }

    public int getDaysAllowed() { return daysAllowed.get(); }
    public IntegerProperty daysAllowedProperty() { return daysAllowed; }

    public void setPolicyID(int value) { policyID.set(value); }
    public void setPolicyName(String value) { policyName.set(value); }
    public void setDescription(String value) { description.set(value); }
    public void setDaysAllowed(int value) { daysAllowed.set(value); }

    @Override
    public String toString() {
        return getPolicyName();
    }
}
