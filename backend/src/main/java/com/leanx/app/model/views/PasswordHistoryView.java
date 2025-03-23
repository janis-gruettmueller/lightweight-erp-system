package com.leanx.app.model.views;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PasswordHistoryView implements Serializable {

    private static final long serialVersionUID = 100L;

    private final Integer userId;
    private final String passwordHash;
    private final Timestamp createdAt;

    /**
     * Constructs an instance of {@code PasswordHistoryView}.
     * This constructor initializes the Password History View Data Object.
     */
    public PasswordHistoryView(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("user_id");
        this.passwordHash = rs.getString("password_hash");
        this.createdAt= rs.getTimestamp("created_at");
    }

    // getters
    public Integer getUserId() { return this.userId; }
    public String getPasswordHash() { return this.passwordHash; }
    public Timestamp getCreatedAt() { return this.createdAt; }


    @Override
    public String toString() {
        return "PasswordHistoryView{" +
                "userId=" + userId +
                ", passwordHash='" + passwordHash + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
