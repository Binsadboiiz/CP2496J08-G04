package model;

public class ReturnPolicy {
    private int id;
    private String policyName;
    private String description;
    private int daysAllowed;

    public ReturnPolicy(int id, String policyName, String description, int daysAllowed) {
        this.id = id;
        this.policyName = policyName;
        this.description = description;
        this.daysAllowed = daysAllowed;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDaysAllowed() { return daysAllowed; }
    public void setDaysAllowed(int daysAllowed) { this.daysAllowed = daysAllowed; }
}
