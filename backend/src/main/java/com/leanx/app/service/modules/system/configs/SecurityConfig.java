package com.leanx.app.service.modules.system.configs;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.repository.PasswordSettingsViewRepository;

/**
 * Configuration class for security-related settings of the application.
 * Defines constants and loads dynamic configurations such as password policies.
 */
public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    /**
     * The default session timeout duration in seconds (currently set to 24 hours).
     */
    public static final int SESSION_TIMEOUT = 3600 * 24;

    /**
     * A map holding the current password policy settings, loaded from the database.
     * The keys represent the setting names (e.g., "password.min_length"), and the
     * values are their corresponding string representations.
     * <p>
     * Note: This map is loaded once during class initialization. If password
     * settings are changed dynamically at runtime, this static map will not
     * automatically reflect those changes. A mechanism for reloading these
     * settings would be required for dynamic updates.
     */
    public static Map<String, String> PASSWORD_SETTINGS; // current password settings -> needs to be updated if dynamically changed during runtime! (lazy loading)

    /**
     * Static initializer block that loads the password policy settings from the
     * database when the {@code SecurityConfig} class is loaded.
     */
    static {
        try {
            PASSWORD_SETTINGS = new PasswordSettingsViewRepository().loadPasswordSettings();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}