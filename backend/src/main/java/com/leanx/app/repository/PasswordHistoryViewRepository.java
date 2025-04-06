package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.leanx.app.model.views.PasswordHistoryView;
import com.leanx.app.repository.base.ViewRepository;
import com.leanx.app.utils.DatabaseUtils;

/**
 * Repository class for accessing the {@code password_history_view} and the
 * {@code password_history} table. Provides methods to retrieve the entire
 * password history view and to load the password history for a specific user.
 */
public class PasswordHistoryViewRepository extends ViewRepository<PasswordHistoryView> {

    /**
     * Constructs a new {@code PasswordHistoryViewRepository}.
     */
    public PasswordHistoryViewRepository() {
        super();
    }

    /**
     * Retrieves all entries from the {@code password_history_view}.
     * Each row in the view is mapped to a {@link PasswordHistoryView} object.
     *
     * @return A {@code List} of {@link PasswordHistoryView} objects representing
     * the entire password history.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public List<PasswordHistoryView> findAllPasswordHistory() throws SQLException {
        return findAll("password_history_view", rs -> {
            try {
                return new PasswordHistoryView(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Mapping failed!", e);
            }
        });
    }

    /**
     * Loads the password history for a specific user, ordered by creation date
     * in descending order, up to a specified history size. This method directly
     * queries the {@code password_history} table.
     *
     * @param userId      The ID of the user whose password history is to be retrieved.
     * @param historySize The maximum number of recent passwords to retrieve.
     * @return A {@code List} of hashed passwords (Strings) for the given user,
     * ordered from the most recent to the least recent, up to the specified
     * {@code historySize}.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public static List<String> loadPasswordHistory(Integer userId, Integer historySize) throws SQLException {
        List<String> passwordHistory = new ArrayList<>();
        String sql = "SELECT password_hash FROM password_history WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, historySize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String passwordHash = rs.getString("password_hash");
                    if (passwordHash != null) {
                        passwordHistory.add(passwordHash);
                    }
                }
            }
        }

        return passwordHistory;
    }
}