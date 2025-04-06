package com.leanx.app.repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leanx.app.model.views.PasswordSettingsView;
import com.leanx.app.repository.base.ViewRepository;

/**
 * Repository class for accessing the {@code password_settings_view}.
 * Provides methods to retrieve all password settings and load them into a
 * map for easy access by configuration type.
 */
public class PasswordSettingsViewRepository extends ViewRepository<PasswordSettingsView> {

    /**
     * Constructs a new {@code PasswordSettingsViewRepository}.
     */
    public PasswordSettingsViewRepository() {
        super();
    }

    /**
     * Retrieves all entries from the {@code password_settings_view}.
     * Each row in the view is mapped to a {@link PasswordSettingsView} object.
     *
     * @return A {@code List} of {@link PasswordSettingsView} objects representing all password settings.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public List<PasswordSettingsView> findAllPasswordSettings() throws SQLException {
        return findAll("password_settings_view", rs -> {
            try {
                return new PasswordSettingsView(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Mapping failed!", e);
            }
        });
    }

    /**
     * Loads all password settings from the database into a {@code Map}.
     * The keys of the map are the configuration keys (e.g., "password.min_length"),
     * and the values are their corresponding configuration values (as strings).
     *
     * @return A {@code Map} containing all password settings loaded from the view.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public Map<String, String> loadPasswordSettings() throws SQLException {
        Map<String, String> pwSettings = new HashMap<>();

        for (PasswordSettingsView setting : findAllPasswordSettings()) {
            pwSettings.put(setting.getConfigKey(), setting.getConfigValue());
        }

        return pwSettings;
    }
}