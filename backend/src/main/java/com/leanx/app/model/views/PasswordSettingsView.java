package com.leanx.app.model.views;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a read-only view of password configuration settings.
 * This class encapsulates the ID, configuration key, configuration value,
 * category, description, last updated timestamp, and the ID of the user
 * who last updated the setting. It is designed to be populated from a
 * database view or a similar query result.
 */
public class PasswordSettingsView implements Serializable {

    private static final long serialVersionUID = 200L;

    private final Integer id;
    private final String configKey;
    private final String configValue;
    private final String configCategory;
    private final String description;
    private final Timestamp lastUpdated;
    private final Integer lastUpdatedBy;

    /**
     * Constructs an instance of {@code PasswordSettingsView} by extracting data
     * from a {@code ResultSet}.
     *
     * @param rs The {@code ResultSet} containing the password settings data.
     * It is expected to have columns named "id", "config_key", "config_value",
     * "config_category", "description", "last_updated", and "last_updated_by".
     * @throws SQLException If an error occurs while accessing the data from the 
     * {@code ResultSet}.
     */
    public PasswordSettingsView(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.configKey = rs.getString("config_key");
        this.configValue = rs.getString("config_value");
        this.configCategory = rs.getString("config_category");
        this.description = rs.getString("description");
        this.lastUpdated = rs.getTimestamp("last_updated");
        this.lastUpdatedBy = rs.getInt("last_updated_by");
    }

    /**
     * Gets the unique identifier of the password setting.
     *
     * @return The setting ID.
     */
    public Integer getId() { return id; }

    /**
     * Gets the key of the password configuration setting.
     *
     * @return The configuration key.
     */
    public String getConfigKey() { return configKey; }

    /**
     * Gets the value of the password configuration setting.
     *
     * @return The configuration value.
     */
    public String getConfigValue() { return configValue; }

    /**
     * Gets the category of the password configuration setting.
     *
     * @return The configuration category.
     */
    public String getConfigCategory() { return configCategory; }

    /**
     * Gets the description of the password configuration setting.
     *
     * @return The description.
     */
    public String getDescription() { return description; }

    /**
     * Gets the timestamp when this password setting was last updated.
     *
     * @return The last updated timestamp.
     */
    public Timestamp getLastUpdated() { return lastUpdated; }

    /**
     * Gets the ID of the user who last updated this password setting.
     *
     * @return The ID of the last updating user.
     */
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }

    /**
     * Returns a string representation of the {@code PasswordSettingsView} object.
     *
     * @return A string containing the ID, configuration key, value, category,
     * description, last updated timestamp, and last updated by user ID.
     */
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