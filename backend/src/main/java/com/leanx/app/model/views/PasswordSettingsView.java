package com.leanx.app.model.views;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordSettingsView {

    private final int id;
    private final String configKey;
    private final String configValue;
    private final String configCategory;
    private final String description;
    private final String lastUpdated;
    private final int lastUpdatedBy;

    // Constructor to map ResultSet to PasswordSettingsView object
    public PasswordSettingsView(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.configKey = rs.getString("config_key");
        this.configValue = rs.getString("config_value");
        this.configCategory = rs.getString("config_category");
        this.description = rs.getString("description");
        this.lastUpdated = rs.getString("last_updated");
        this.lastUpdatedBy = rs.getInt("last_updated_by");
    }

    // Getters
    public int getId() { return id; }
    public String getConfigKey() { return configKey; }
    public String getConfigValue() { return configValue; }
    public String getConfigCategory() { return configCategory; }
    public String getDescription() { return description; }
    public String getLastUpdated() { return lastUpdated; }
    public int getLastUpdatedBy() { return lastUpdatedBy; }

}
