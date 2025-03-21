package com.leanx.app.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private UserStatus status;
    private UserType type;
    private String passwordHash;
    private Date passwordExpiryDate;
    private Integer numFailedLoginAttempts;
    private Timestamp lockUntil;
    private Boolean isFirstLogin;
    private Timestamp lastLoginAt;
    private Date validUntil;
    private Integer createdBy;
    private Timestamp createdAt;
    private Integer lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    public enum UserStatus {
        ACTIVE, LOCKED, DEACTIVATED
    }

    public enum UserType {
        NORMAL, ADMIN, SYSTEM, SUPER
    }

    // Default constructor
    public User() {}

    // Constructor with all fields except auto-generated and default values
    public User(String username, UserStatus status, UserType type, String passwordHash, Date passwordExpiryDate, Date validUntil, Integer createdBy) {
        this.name = username; 
        this.status = status; 
        this.type = type; 
        this.passwordHash = passwordHash; 
        this.passwordExpiryDate = passwordExpiryDate;
        this.validUntil = validUntil; 
        this.createdBy = createdBy;
    }

    // getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public UserStatus getStatus() { return status; }
    public UserType getType() { return type; }
    public String getPasswordHash() { return passwordHash; }
    public Date getPasswordExpiryDate() { return passwordExpiryDate; }
    public Integer getNumFailedLoginAttempts() { return numFailedLoginAttempts; }
    public Timestamp getLockUntil() { return lockUntil; }
    public Boolean isFirstLogin() { return isFirstLogin; }
    public Timestamp getLastLoginAt() { return lastLoginAt; }
    public Date getValidUntil() { return validUntil; }
    public Integer getCreatedBy() { return createdBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }

    // setters
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setType(UserType type) { this.type = type; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setPasswordExpiryDate(Date passwordExpiryDate) { this.passwordExpiryDate = passwordExpiryDate; }
    public void setNumFailedLoginAttempts(Integer numFailedLoginAttempts) { this.numFailedLoginAttempts = numFailedLoginAttempts; }
    public void setLockUntil(Timestamp lockUntil) { this.lockUntil = lockUntil; }
    public void setIsFirstLogin(Boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }
    public void setLastLoginAt(Timestamp lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name + 
                ", status=" + status +
                ", type=" + type +
                ", passwordHash=" + passwordHash +
                ", passwordExpiryDate=" + passwordExpiryDate +
                ", numFailedLoginAttempts=" + numFailedLoginAttempts +
                ", lockUntil=" + lockUntil +
                ", lisFirstLogin=" + isFirstLogin +
                ", lastLoginAt=" + lastLoginAt +
                ", validUntil=" + validUntil +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdatedAt=" + lastUpdatedAt +
                "}";
    }
}
