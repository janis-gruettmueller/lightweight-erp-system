package com.leanx.app.model.views;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a read-only view of password history entries.
 * This class encapsulates the user ID, the hashed password, and the timestamp
 * when the password was created. It is designed to be populated from a
 * database view or a similar query result.
 */
public class PasswordHistoryView implements Serializable {

    private static final long serialVersionUID = 100L;

    private final Integer userId;
    private final String passwordHash;
    private final Timestamp createdAt;

    /**
     * Constructs an instance of {@code PasswordHistoryView} by extracting data
     * from a {@code ResultSet}.
     *
     * @param rs The {@code ResultSet} containing the password history data.
     * It is expected to have columns named "user_id", "password_hash",
     * and "created_at".
     * @throws SQLException If an error occurs while accessing the data from the {@code ResultSet}.
     */
    public PasswordHistoryView(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("user_id");
        this.passwordHash = rs.getString("password_hash");
        this.createdAt= rs.getTimestamp("created_at");
    }

    /**
     * Gets the ID of the user associated with this password history entry.
     *
     * @return The user ID.
     */
    public Integer getUserId() { return this.userId; }

    /**
     * Gets the hashed password from this history entry.
     *
     * @return The hashed password.
     */
    public String getPasswordHash() { return this.passwordHash; }

    /**
     * Gets the timestamp indicating when this password was created.
     *
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() { return this.createdAt; }

    /**
     * Returns a string representation of the {@code PasswordHistoryView} object.
     *
     * @return A string containing the user ID, password hash, and creation timestamp.
     */
    @Override
    public String toString() {
        return "PasswordHistoryView{" +
                "userId=" + userId +
                ", passwordHash='" + passwordHash + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}