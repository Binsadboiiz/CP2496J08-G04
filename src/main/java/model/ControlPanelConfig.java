package model;

public class ControlPanelConfig {
    private int configID;
    private String configName;
    private String configValue;

    public ControlPanelConfig(int configID, String configName, String configValue) {
        this.configID = configID;
        this.configName = configName;
        this.configValue = configValue;
    }

    // Getters
    public int getConfigID() { return configID; }
    public String getConfigName() { return configName; }
    public String getConfigValue() { return configValue; }
}
