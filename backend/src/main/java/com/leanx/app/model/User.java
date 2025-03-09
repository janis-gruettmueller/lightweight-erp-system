package main.java.com.leanx.app.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String username;
    private UserStatus userStatus;
    private UserType userType;
    private Boolean isVerified;
    private String passwordHash;
    private Integer numFailedLoginAttempts;
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
        NORMAL, ADMIN, SYSTEM
    }

    // Default constructor
    public User() {}

    // Constructor with all fields except auto-generated and default values
    public User(String username, UserStatus userStatus, UserType userType, Boolean isVerified, String passwordHash, Date validUntil, Integer createdBy) {
        this.username = username; 
        this.userStatus = userStatus; 
        this.userType = userType; 
        this.isVerified = isVerified; 
        this.passwordHash = passwordHash; 
        this.validUntil = validUntil; 
        this.createdBy = createdBy;
    }

    // getters
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public UserStatus getUserStatus() { return userStatus; }
    public UserType getUserType() { return userType; }
    public Boolean getIsVerified() { return isVerified; }
    public String getPasswordHash() { return passwordHash; }
    public Integer getNumFailedLoginAttempts() { return numFailedLoginAttempts; }
    public Timestamp getLastLoginAt() { return lastLoginAt; }
    public Date getValidUntil() { return validUntil; }
    public Integer getCreatedBy() { return createdBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }

    // setters
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setNumFailedLoginAttempts(Integer numFailedLoginAttempts) { this.numFailedLoginAttempts = numFailedLoginAttempts; }
    public void setLastLoginAt(Timestamp lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username + 
                ", userStatus=" + userStatus +
                ", userType=" + userType +
                ", isVerified=" + isVerified +
                ", passwordHash=" + passwordHash +
                ", numFailedLoginAttempts=" + numFailedLoginAttempts +
                ", lastLoginAt=" + lastLoginAt +
                ", validUntil=" + validUntil +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdatedAt=" + lastUpdatedAt +
                "}";
    }
}
