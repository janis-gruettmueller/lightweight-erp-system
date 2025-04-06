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

import com.leanx.app.model.entity.User;
import com.leanx.app.model.entity.User.UserStatus;
import com.leanx.app.model.entity.User.UserType;
import com.leanx.app.repository.base.CrudRepository;
import com.leanx.app.utils.DatabaseUtils;

/**
 * Repository class for performing CRUD (Create, Read, Update, Delete) operations
 * and other queries on {@link User} entities in the database.
 */
public class UserRepository implements CrudRepository<User> {

    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

    /**
     * Creates a new user record in the database.
     *
     * @param user The {@link User} object containing the data for the new record.
     * @return The number of rows affected by the insert operation (should be 1 on success).
     * @throws IllegalArgumentException If the provided {@code user} object is null.
     * @throws SQLException             If a database access error occurs during the creation.
     */
    @Override
    public int create(User user) throws IllegalArgumentException, SQLException {
        if (user == null) {
            throw new IllegalArgumentException("Invalid user object.");
        }

        String sql = "INSERT INTO users (name, status, type, password_hash, password_expiry_date, " +
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

    /**
     * Retrieves a user record from the database based on its unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A {@link User} object representing the retrieved record, or {@code null}
     * if no user with the given ID exists.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
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

                    try {
                        user.setType(UserType.valueOf(rs.getString("type")));
                        user.setStatus(UserStatus.valueOf(rs.getString("status")));
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "IllegalArgumentException: " + e.getMessage(), e);
                    }

                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPasswordExpiryDate(rs.getDate("password_expiry_date"));
                    user.setNumFailedLoginAttempts(rs.getInt("num_failed_login_attempts"));
                    user.setLockUntil(rs.getTimestamp("lock_until"));
                    user.setIsFirstLogin(rs.getBoolean("is_first_login"));
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

    /**
     * Updates specific fields of an existing user record in the database.
     *
     * @param id      The ID of the user record to update.
     * @param updates A {@code Map} where the keys are the column names to update
     * and the values are the new values for those columns.
     * @return The number of rows affected by the update operation (should be 1 on success).
     * @throws IllegalArgumentException If the provided {@code updates} map is null or empty.
     * @throws SQLException             If a database access error occurs during the update.
     */
    @Override
    public int update(Integer id, Map<String, Object> updates) throws IllegalArgumentException, SQLException {
        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        for (String field : updates.keySet()) {
            sql.append(field).append(" = ?, ");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");

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

    /**
     * Deletes a user record from the database based on its unique ID.
     *
     * @param id The ID of the user record to delete.
     * @return The number of rows affected by the delete operation (should be 1 on success).
     * @throws IllegalArgumentException If the provided {@code id} is null or not positive.
     * @throws SQLException             If a database access error occurs during the deletion.
     */
    @Override
    public int delete(Integer id) throws IllegalArgumentException, SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setInt(1, id);
            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting user with ID " + id, e);
        }

    }

    /**
     * Retrieves all user records from the database.
     *
     * @return A {@code List} containing all {@link User} objects in the database.
     * Returns an empty list if no users are found.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id FROM users"; // Fetch only IDs for efficiency

        try (java.sql.Connection connection = DatabaseUtils.getMySQLConnection();
             java.sql.PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = read(rs.getInt("id"));
                if (user != null) {
                    users.add(user);
                }
            }
        }

        return users;
    }

    /**
     * Retrieves a user record from the database based on their unique username.
     *
     * @param name The username of the user to retrieve.
     * @return A {@link User} object representing the retrieved record, or {@code null}
     * if no user with the given username exists.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
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
                        user.setType(UserType.valueOf(rs.getString("type")));
                        user.setStatus(UserStatus.valueOf(rs.getString("status")));
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "IllegalArgumentException: " + e.getMessage(), e);
                    }

                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPasswordExpiryDate(rs.getDate("password_expiry_date"));
                    user.setNumFailedLoginAttempts(rs.getInt("num_failed_login_attempts"));
                    user.setLockUntil(rs.getTimestamp("lock_until"));
                    user.setIsFirstLogin(rs.getBoolean("is_first_login"));
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