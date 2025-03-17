package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.User;
import com.leanx.app.model.User.UserStatus;
import com.leanx.app.model.User.UserType;
import com.leanx.app.utils.DatabaseUtils;

public class UserCrudRepository implements CrudRepository<User> {

    private static final Logger logger = Logger.getLogger(UserCrudRepository.class.getName());

    @Override
    public int create(User user) throws IllegalArgumentException, SQLException {
        if (user == null) {
            throw new IllegalArgumentException("Invalid user object.");
        }

        String sql = "INSERT INTO user (name, status, type, password_hash, password_expiry_date, " +
            "valid_until, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement SQLStatement = c.prepareStatement(sql)) {
            SQLStatement.setString(1, user.getName());
            SQLStatement.setString(2, String.valueOf(user.getStatus()));
            SQLStatement.setString(3, String.valueOf(user.getType()));
            SQLStatement.setString(4, user.getPasswordHash());
            SQLStatement.setDate(5, user.getPasswordExpiryDate());
            SQLStatement.setDate(6, user.getValidUntil());
            SQLStatement.setInt(7, user.getCreatedBy());

            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error creating new user", e);
        }
    }

    @Override
    public User read(Integer id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setInt(1, id);
            try (ResultSet rs = SQLStatement.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setStatus(UserStatus.valueOf(rs.getString("status")));
                    user.setType(UserType.valueOf(rs.getString("type")));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPasswordExpiryDate(rs.getDate("password_expiry_date"));
                    user.setNumFailedLoginAttempts(rs.getInt("num_failed_login_attempts"));
                    user.setLastLoginAt(rs.getTimestamp("last_login_at"));
                    user.setValidUntil(rs.getDate("valid_until"));
                    user.setCreatedBy(rs.getInt("created_by"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setLastUpdatedBy(rs.getInt("last_updated_by"));
                    user.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));

                    return user;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving user with ID " + id, e);
        }
    }

    @Override
    public int update(Integer id, Map<String, Object> updates) throws IllegalArgumentException, SQLException {
        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        StringBuilder sql = new StringBuilder("UPDATE user SET ");
        for (String field : updates.keySet()) {
            sql.append(field).append(" = ?, ");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE user_id = ?");

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement SQLStatement = c.prepareStatement(sql.toString())) {
            int index = 1;

            for (Object value : updates.values()) {
                SQLStatement.setObject(index++, value);
            }

            SQLStatement.setInt(index, id);
            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating user with ID " + id, e);
        }
    }

    @Override
    public int delete(Integer id) throws IllegalArgumentException, SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setInt(1, id);

            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating user with ID " + id, e);
        }

    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (java.sql.Connection connection = DatabaseUtils.getMySQLConnection();
             java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(read(rs.getInt("id")));
                }
            }
        }
        
        return users;
    }

    public User read(String name) throws SQLException {
        String sql = "SELECT * FROM users WHERE name = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setString(1, name);
            logger.log(Level.INFO, "Executing SQL Query: {0}", sql);
            try (ResultSet rs = SQLStatement.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));

                    try {
                        user.setType(UserType.valueOf(rs.getString("status")));
                        user.setStatus(UserStatus.valueOf(rs.getString("type")));
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }

                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPasswordExpiryDate(rs.getDate("password_expiry_date"));
                    user.setNumFailedLoginAttempts(rs.getInt("num_failed_login_attempts"));
                    user.setLastLoginAt(rs.getTimestamp("last_login_at"));
                    user.setValidUntil(rs.getDate("valid_until"));
                    user.setCreatedBy(rs.getInt("created_by"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setLastUpdatedBy(rs.getInt("last_updated_by"));
                    user.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));

                    return user;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving user with name " + name + ": " + e.getMessage(), e);
        }
    }
}
