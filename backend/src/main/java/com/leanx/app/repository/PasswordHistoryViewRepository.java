package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.leanx.app.model.views.PasswordHistoryView;
import com.leanx.app.utils.DatabaseUtils;

public class PasswordHistoryViewRepository extends ViewRepository<PasswordHistoryView>{

    public PasswordHistoryViewRepository() {
        super();
    }

    public List<PasswordHistoryView> findAllPasswordHistory() throws SQLException {
        return findAll("password_history_view", rs -> {
            try {
                return new PasswordHistoryView(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Mapping failed!", e);
            }
        });
    }

    public static List<String> loadPasswordHistory(Integer userId, Integer historySize) throws SQLException {
        List<String> passwordHistory = new ArrayList<>();
        String sql = "SELECT hashed_password FROM password_history WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
    
        try (Connection conn = DatabaseUtils.getMySQLConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, historySize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String passwordHash = rs.getString("hashed_password");
                    if (passwordHash != null) {
                        passwordHistory.add(passwordHash);
                    }
                }
            }
        }

        return passwordHistory;
    }

}
