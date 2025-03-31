package com.leanx.app.service.modules.user.admin;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.entity.User;
import com.leanx.app.repository.UserEmployeeLinkRepository;
import com.leanx.app.repository.UserRepository;
import com.leanx.app.repository.base.StoredProceduresRepository;

public final class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userCrudRepository;
    private final UserEmployeeLinkRepository userEmployeeLinkRepository;
    private final StoredProceduresRepository storedProceduresRepository;

    // constructor
    public UserService() {
        this.userCrudRepository = new UserRepository();
        this.userEmployeeLinkRepository = new UserEmployeeLinkRepository();
        this.storedProceduresRepository = new StoredProceduresRepository();
    }

    private boolean executeUpdate(Map<String, Object> updates, Integer userId) throws SQLException {
        try {
            return userCrudRepository.update(userId, updates) > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update user with ID: " + userId, e);
            throw e;
        }
    }

    public int getUserId(String username) throws SQLException {
        try {
            User user = userCrudRepository.read(username);
            if (user == null) {
                logger.log(Level.WARNING, "Failed to find user with name: {0}", username);
                return -1;
            }
            return user.getId();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error accessing the database while trying to find user with name: " + username, e);
            throw e;
        }
    }

    public User getUserByIdentifier(Object identifier) throws SQLException, IllegalArgumentException {
        if (identifier instanceof String username) {
            return userCrudRepository.read(username);
        } 
        
        if (identifier instanceof Integer userId) {
            return userCrudRepository.read(userId);
        }
        
        throw new IllegalArgumentException("Invalid identifier type! Expected String or Integer.");
    }

    public boolean updatePassword(Integer userId, Integer updatedBy, String hashedPassword) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("password_hash", hashedPassword);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean updateLastLoginAt(Integer userId, Integer updatedBy) throws SQLException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> updates = new HashMap<>();
        updates.put("last_login_at", timestamp);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean setIsFirstLoginFalse(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("is_first_login", false);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean updateNumFailedLoginAttempts(Integer userId, Integer updatedBy, Integer numFailedAttempts) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("num_failed_login_attempts", numFailedAttempts);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean resetNumFailedLoginAttempts(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("num_failed_login_attempts", 0);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean lockUser(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "LOCKED");
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean unlockUser(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "ACTIVE");
        updates.put("lock_until", null);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    public boolean assignRole(Integer userId, Integer assignedBy, String role) {
        // TODO
        return false;
    }

    public boolean removeRole(Integer userId, Integer assignedBy, String role) {
        // TODO
        return false;
    }

    public List<String> getRolesForUser(Integer userId) {
        // TODO
        List<String> roles = new ArrayList<>();


        return roles;
    }

    public boolean linkUserToEmployee(Integer userId, Integer employeeId) throws SQLException {
        try {
            int affectedRows = userEmployeeLinkRepository.saveUserEmployeeLink(employeeId, userId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to link user with ID: " + userId + " to employee with ID: " + employeeId, e);
            throw e;
        }
    }

    public String generateUsername(String firstName, String lastName) {
        int maxLength = 7;

        String normalizedFirstName = firstName.toLowerCase(Locale.GERMAN)
            .replace(" ", "")
            .replace("-", "")
            .replace("ä", "ae")
            .replace("ü", "ue")
            .replace("ö", "oe");
        String normalizedLastName = lastName.toLowerCase(Locale.GERMAN)
            .replace(" ", "")
            .replace("ä", "ae")
            .replace("ü", "ue")
            .replace("ö", "oe");

        String baseUsername = normalizedFirstName.substring(0, 1) + normalizedLastName;
        String username = baseUsername;
        if (username.length() > maxLength) {
            username = username.substring(0, maxLength);
        }

        int counter = 1;
        
        try {
            while (getUserId(username) != -1) {
                username = baseUsername + counter;
                counter++;
            }
        } catch (SQLException e) {
            // expected condition
        }
        
        return username;
    }

    public boolean deactivateUser(Integer userId, Integer currentUserId) throws IllegalArgumentException {
        return storedProceduresRepository.callDeactivateUserAccount(userId, currentUserId);
    }
}
