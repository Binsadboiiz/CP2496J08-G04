package model;

public class ReturnPolicy {
    private int policyID;
    private String policyName;
    private String description;
    private int daysAllowed;

    public ReturnPolicy(int policyID, String policyName, String description, int daysAllowed) {
        this.policyID = policyID;
        this.policyName = policyName;
        this.description = description;
        this.daysAllowed = daysAllowed;
    }

    // Getters
    public int getPolicyID() { return policyID; }
    public String getPolicyName() { return policyName; }
    public String getDescription() { return description; }
    public int getDaysAllowed() { return daysAllowed; }
}
