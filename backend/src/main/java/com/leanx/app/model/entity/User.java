package com.leanx.app.model.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity representing a user account within the LeanX application.
 * Stores authentication details, status, type, and audit information.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 20L;

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

    /**
     * Enumeration defining the possible statuses of a user account.
     */
    public enum UserStatus {
        ACTIVE, LOCKED, DEACTIVATED
    }

    /**
     * Enumeration defining the different types of users within the system,
     * which may imply different levels of access or permissions.
     */
    public enum UserType {
        NORMAL, ADMIN, SYSTEM, SUPER
    }

    /**
     * Default constructor for the User class.
     */
    public User() {}

    /**
     * Constructor for creating a User with essential details.
     * Excludes auto-generated fields and those with default values.
     *
     * @param username         The unique username for the user account.
     * @param status           The current status of the user account (e.g., ACTIVE).
     * @param type             The type of user (e.g., NORMAL, ADMIN).
     * @param passwordHash     The hashed password for the user account.
     * @param passwordExpiryDate The date when the user's password expires.
     * @param validUntil       The date until which the user account is valid.
     * @param createdBy        The ID of the user who created this user account.
     */
    public User(String username, UserStatus status, UserType type, String passwordHash, Date passwordExpiryDate, Date validUntil, Integer createdBy) {
        this.name = username;
        this.status = status;
        this.type = type;
        this.passwordHash = passwordHash;
        this.passwordExpiryDate = passwordExpiryDate;
        this.validUntil = validUntil;
        this.createdBy = createdBy;
    }

    /**
     * Gets the unique identifier of the user account.
     *
     * @return The user ID.
     */
    public Integer getId() { return id; }

    /**
     * Gets the unique username of the user account.
     *
     * @return The username.
     */
    public String getName() { return name; }

    /**
     * Gets the current status of the user account.
     *
     * @return The user status.
     */
    public UserStatus getStatus() { return status; }

    /**
     * Gets the type of the user account.
     *
     * @return The user type.
     */
    public UserType getType() { return type; }

    /**
     * Gets the hashed password of the user account.
     *
     * @return The password hash.
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Gets the date when the user's password expires.
     *
     * @return The password expiry date.
     */
    public Date getPasswordExpiryDate() { return passwordExpiryDate; }

    /**
     * Gets the number of failed login attempts for the user account.
     *
     * @return The number of failed login attempts.
     */
    public Integer getNumFailedLoginAttempts() { return numFailedLoginAttempts; }

    /**
     * Gets the timestamp until which the user account is locked due to excessive failed login attempts.
     *
     * @return The lock until timestamp.
     */
    public Timestamp getLockUntil() { return lockUntil; }

    /**
     * Indicates whether this is the first time the user is logging in.
     *
     * @return {@code true} if it's the first login, {@code false} otherwise.
     */
    public Boolean isFirstLogin() { return isFirstLogin; }

    /**
     * Gets the timestamp of the user's last successful login.
     *
     * @return The last login timestamp.
     */
    public Timestamp getLastLoginAt() { return lastLoginAt; }

    /**
     * Gets the date until which the user account is valid.
     *
     * @return The valid until date.
     */
    public Date getValidUntil() { return validUntil; }

    /**
     * Gets the ID of the user who created this user account.
     *
     * @return The ID of the creating user.
     */
    public Integer getCreatedBy() { return createdBy; }

    /**
     * Gets the timestamp when this user account was created.
     *
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() { return createdAt; }

    /**
     * Gets the ID of the user who last updated this user account.
     *
     * @return The ID of the last updating user.
     */
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }

    /**
     * Gets the timestamp when this user account was last updated.
     *
     * @return The last updated timestamp.
     */
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }

    /**
     * Sets the unique identifier of the user account.
     *
     * @param id The user ID.
     */
    public void setId(Integer id) { this.id = id; }

    /**
     * Sets the unique username of the user account.
     *
     * @param name The username.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the current status of the user account.
     *
     * @param status The user status.
     */
    public void setStatus(UserStatus status) { this.status = status; }

    /**
     * Sets the type of the user account.
     *
     * @param type The user type.
     */
    public void setType(UserType type) { this.type = type; }

    /**
     * Sets the hashed password of the user account.
     *
     * @param passwordHash The password hash.
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Sets the date when the user's password expires.
     *
     * @param passwordExpiryDate The password expiry date.
     */
    public void setPasswordExpiryDate(Date passwordExpiryDate) { this.passwordExpiryDate = passwordExpiryDate; }

    /**
     * Sets the number of failed login attempts for the user account.
     *
     * @param numFailedLoginAttempts The number of failed login attempts.
     */
    public void setNumFailedLoginAttempts(Integer numFailedLoginAttempts) { this.numFailedLoginAttempts = numFailedLoginAttempts; }

    /**
     * Sets the timestamp until which the user account is locked.
     *
     * @param lockUntil The lock until timestamp.
     */
    public void setLockUntil(Timestamp lockUntil) { this.lockUntil = lockUntil; }

    /**
     * Sets whether this is the first time the user is logging in.
     *
     * @param isFirstLogin {@code true} if it's the first login, {@code false} otherwise.
     */
    public void setIsFirstLogin(Boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

    /**
     * Sets the timestamp of the user's last successful login.
     *
     * @param lastLoginAt The last login timestamp.
     */
    public void setLastLoginAt(Timestamp lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    /**
     * Sets the date until which the user account is valid.
     *
     * @param validUntil The valid until date.
     */
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }

    /**
     * Sets the ID of the user who created this user account.
     *
     * @param createdBy The ID of the creating user.
     */
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    /**
     * Sets the timestamp when this user account was created.
     *
     * @param createdAt The creation timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Sets the ID of the user who last updated this user account.
     *
     * @param lastUpdatedBy The ID of the last updating user.
     */
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    /**
     * Sets the timestamp when this user account was last updated.
     *
     * @param lastUpdatedAt The last updated timestamp.
     */
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    /**
     * Returns a string representation of the User object.
     *
     * @return A string containing the values of all fields of the User.
     */
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
                '}';
    }
}