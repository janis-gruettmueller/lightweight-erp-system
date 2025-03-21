package com.leanx.app.service.modules.user.admin;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.User;
import com.leanx.app.repository.StoredProceduresRepository;
import com.leanx.app.repository.UserCrudRepository;

public final class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserCrudRepository userCrudRepository;
    private final StoredProceduresRepository storedProceduresRepository;

    // constructor
    public UserService() {
        this.userCrudRepository = new UserCrudRepository();
        this.storedProceduresRepository = new StoredProceduresRepository();
    }

    private boolean executeUpdate(Map<String, Object> updates, Integer userId) {
        try {
            int affectedRows = userCrudRepository.update(userId, updates);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update user with ID: " + userId, e);
            return false;
        }
    }

    public int getUserId(String username) throws SQLException {
        User user = userCrudRepository.read(username);
        if (user != null) {
            return user.getId();
        }
        throw new SQLException("User not found with username: " + username);
    }

    public User getUserByIdentifier(Object identifier) throws SQLException {
        if (identifier instanceof String username) {
            return userCrudRepository.read(username);
        } 
        
        if (identifier instanceof Integer userId) {
            return userCrudRepository.read(userId);
        }
        
        throw new IllegalArgumentException("Invalid identifier type! Expected String or Integer.");
    }

    public boolean updatePassword(Integer userId, Integer updatedBy, String hashedPassword) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("password_hash", hashedPassword);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean updateLastLoginAt(Integer userId, Integer updatedBy) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> updates = new HashMap<>();
        updates.put("last_login_at", timestamp);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean setIsFirstLoginFalse(Integer userId, Integer updatedBy) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("is_first_login", false);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean updateNumFailedLoginAttempts(Integer userId, Integer updatedBy, Integer numFailedAttempts) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("num_failed_login_attempts", numFailedAttempts);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean resetNumFailedLoginAttempts(Integer userId, Integer updatedBy) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("num_failed_login_attempts", 0);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean lockUser(Integer userId, Integer updatedBy) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "LOCKED");
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean unlockUser(Integer userId, Integer updatedBy) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "ACTIVE");
        updates.put("lock_until", null);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean deactivateUser(Integer userId, Integer currentUserId) throws IllegalArgumentException {
        return storedProceduresRepository.callDeactivateUserAccount(userId, currentUserId);
    }
}
