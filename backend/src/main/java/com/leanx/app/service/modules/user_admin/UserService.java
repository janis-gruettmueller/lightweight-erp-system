package com.leanx.app.service.modules.user_admin;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import com.leanx.app.model.User;
import com.leanx.app.repository.UserCrudRepository;

public class UserService {

    private final UserCrudRepository userCrudRepository = new UserCrudRepository();


    public User getUserbyName(String username) throws SQLException {
        return userCrudRepository.read(username);
    }

    public User getUserById(Integer userId) throws SQLException {
        return userCrudRepository.read(userId);
    }

    public Timestamp getLastLoginAt(Integer userId) throws SQLException {
        User user = userCrudRepository.read(userId);

        return user.getLastLoginAt();
    }

    public boolean updatePassword(Integer userId, String hashedPassword) throws SQLException {
        Map<String, Object> updates = Map.of("password_hash", hashedPassword); // Map.of -> imutable Map (Java 9+)
    
        try {
            userCrudRepository.update(userId, updates);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to update password: " + e.getMessage());
            throw e; 
        }
    }

    public boolean incrementNumFailedLoginAttempts(Integer userId, Integer numFailedAttempts) throws IllegalArgumentException, SQLException {
        Map<String, Object> updates = Map.of("num_failed_login_attempts", numFailedAttempts + 1);

        try {
            userCrudRepository.update(userId, updates);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to increment number of failed login attempts: " + e.getMessage());
            throw e; 
        }
    }

    public boolean resetNumFailedLoginAttempts(Integer userId) throws IllegalArgumentException, SQLException {
        Map<String, Object> updates = Map.of("num_failed_login_attempts", 0);

        try {
            userCrudRepository.update(userId, updates);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to reset number of failed login attempts: " + e.getMessage());
            throw e; 
        }
    }

    public boolean lockUser(Integer userId) throws IllegalArgumentException, SQLException {
        Map<String, Object> updates = Map.of("status", "LOCKED");

        try {
            userCrudRepository.update(userId, updates);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to update user status to LOCKED: " + e.getMessage());
            throw e; 
        }
    }
    
}
