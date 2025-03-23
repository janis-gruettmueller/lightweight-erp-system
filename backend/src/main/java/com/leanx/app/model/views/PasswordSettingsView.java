package com.leanx.app.model.views;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PasswordSettingsView implements Serializable {

    private static final long serialVersionUID = 200L;

    private final Integer id;
    private final String configKey;
    private final String configValue;
    private final String configCategory;
    private final String description;
    private final Timestamp lastUpdated;
    private final Integer lastUpdatedBy;

    // Constructor to map ResultSet to PasswordSettingsView object
    public PasswordSettingsView(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.configKey = rs.getString("config_key");
        this.configValue = rs.getString("config_value");
        this.configCategory = rs.getString("config_category");
        this.description = rs.getString("description");
        this.lastUpdated = rs.getTimestamp("last_updated");
        this.lastUpdatedBy = rs.getInt("last_updated_by");
    }

    // Getters
    public Integer getId() { return id; }
    public String getConfigKey() { return configKey; }
    public String getConfigValue() { return configValue; }
    public String getConfigCategory() { return configCategory; }
    public String getDescription() { return description; }
    public Timestamp getLastUpdated() { return lastUpdated; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }

    @Override
    public String toString() {
        return "PasswordSettingView{" +
                "id=" + id +
                ", configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                ", configCategory='" + configCategory + '\'' +
                ", description='" + description + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", lastUpdatedBy=" + lastUpdatedBy +
                '}';
    }
}
