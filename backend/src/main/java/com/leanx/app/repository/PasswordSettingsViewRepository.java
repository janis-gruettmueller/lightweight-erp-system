package com.leanx.app.repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leanx.app.model.views.PasswordSettingsView;

public class PasswordSettingsViewRepository extends ViewRepository<PasswordSettingsView> {

    public PasswordSettingsViewRepository() {
        super();
    }

    public List<PasswordSettingsView> findAllPasswordSettings() throws SQLException {
        return findAll("passwordSettingsView", rs -> {
            try {
                return new PasswordSettingsView(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Mapping failed!", e);
            }
        });
    }

    public static Map<String, String> loadPasswordSettings() throws SQLException {
        PasswordSettingsViewRepository pwSettingsRepo = new PasswordSettingsViewRepository();
        Map<String, String> pwSettings = new HashMap<>();

        for (PasswordSettingsView setting : pwSettingsRepo.findAllPasswordSettings()) {
            pwSettings.put(setting.getConfigKey(), setting.getConfigValue());
        }

        return pwSettings;
    }
    
}