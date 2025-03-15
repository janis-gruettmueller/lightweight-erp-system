package com.leanx.app.model.views;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PasswordHistoryView {

    private final Integer userId;
    private final String passwordHash;
    private final Timestamp createdAt;

    public PasswordHistoryView(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("user_id");
        this.passwordHash = rs.getString("password_hash");
        this.createdAt= rs.getTimestamp("created_at");
    }

    public Integer getUserId() { return this.userId; }
    public String getPasswordHash() { return this.passwordHash; }
    public Timestamp getCreatedAt() { return this.createdAt; }

}
