package com.leanx.app.service.modules.user_admin;

import java.sql.SQLException;
import java.util.Map;

import com.leanx.app.model.User;
import com.leanx.app.repository.StoredProceduresRepository;
import com.leanx.app.repository.UserCrudRepository;

public class UserService {

    private final UserCrudRepository userCrudRepository = new UserCrudRepository();
    private final StoredProceduresRepository storedProceduresRepository = new StoredProceduresRepository();


    public int getUserId(String username) {
        try {
            User user = userCrudRepository.read(username);
            return user.getId();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
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

    public boolean updatePassword(Integer userId, String hashedPassword) throws IllegalArgumentException {
        Map<String, Object> updates = Map.of("password_hash", hashedPassword); // Map.of -> imutable Map (Java 9+)
    
        try {
            int rowsEffected = userCrudRepository.update(userId, updates);
            return rowsEffected > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update password: " + e.getMessage());
            return false;
        }
    }

    public boolean incrementNumFailedLoginAttempts(Integer userId, Integer numFailedAttempts) throws IllegalArgumentException {
        Map<String, Object> updates = Map.of("num_failed_login_attempts", numFailedAttempts + 1);

        try {
            int affectedRows = userCrudRepository.update(userId, updates);
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Failed to increment number of failed login attempts: " + e.getMessage());
            return false;
        }
    }

    public boolean resetNumFailedLoginAttempts(Integer userId) throws IllegalArgumentException {
        Map<String, Object> updates = Map.of("num_failed_login_attempts", 0);

        try {
            int affectedRows = userCrudRepository.update(userId, updates);
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Failed to reset number of failed login attempts: " + e.getMessage());
            return false;
        }
    }

    public boolean lockUser(Integer userId) throws IllegalArgumentException {
        Map<String, Object> updates = Map.of("status", "LOCKED");

        try {
            int affectedRows = userCrudRepository.update(userId, updates);
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update user status to LOCKED: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateUser(Integer userId, Integer currentUserId) throws IllegalArgumentException {
        return storedProceduresRepository.callDeactivateUserAccount(userId, currentUserId);
    }
}
