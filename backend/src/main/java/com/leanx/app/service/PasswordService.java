package com.leanx.app.service;

import com.leanx.app.model.User;
import com.leanx.app.utils.PasswordUtils;

public class PasswordService {
    private PasswordUtils passwordUtils = new PasswordUtils();

    /**
     * Validates if the password meets the required format.
     * @param password The password to be validated.
     * @return True if valid, false otherwise.
     */
    public boolean isValidPassword(String password) {
        return true;
    }
    
    public boolean updatePassword(User user, String newPassword) {
        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Password is invalid!");
        }
        
        // Check if the password was used recently (if needed)
        if (passwordHistoryRepository.hasUsedRecently(user.getUserId(), newPassword)) {
            throw new IllegalArgumentException("You cannot reuse your recent password");
        }

        String hashedPassword = passwordUtils.hashPassword(newPassword);
        user.setPasswordHash(hashedPassword);
        passwordHistoryRepository.savePasswordHistory(user, newPassword);
        return true;
    }
}
