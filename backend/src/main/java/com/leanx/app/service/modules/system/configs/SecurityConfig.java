package com.leanx.app.service.modules.system.configs;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.repository.PasswordSettingsViewRepository;

public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    public static final int SESSION_TIMEOUT = 3600; // 1 hour in seconds
    public static Map<String, String> PASSWORD_SETTINGS; // current password settings -> needs to be updated if dynmically changed during runtime! (lazy loading)

    static {
        try {
            PASSWORD_SETTINGS = new PasswordSettingsViewRepository().loadPasswordSettings();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
