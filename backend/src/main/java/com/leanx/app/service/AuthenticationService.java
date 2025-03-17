package com.leanx.app.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.User;
import com.leanx.app.repository.PasswordHistoryViewRepository;
import com.leanx.app.repository.PasswordSettingsViewRepository;
import com.leanx.app.repository.ViewRepository;
import com.leanx.app.service.modules.user_admin.UserService;
import com.leanx.app.utils.AuthenticationUtils;

/**
 * The {@code AuthenticationService} class provides functionalities related to user authentication,
 * including password validation, login attempts, and password changes.
 * It handles user authentication, password history checking, password policy enforcement, and user lockout logic.
 */
public class AuthenticationService {

    private static final Logger logger = Logger.getLogger(ViewRepository.class.getName());

    private final PasswordSettingsViewRepository passwordSettingsViewRepository;
    private final AuthenticationUtils authenticationUtils;
    private final UserService userService;
    private Map<String, String> passwordSettings;

    /**
     * Constructs an instance of {@code AuthenticationService}.
     * This constructor initializes the password settings, authentication utilities, and user service.
     * 
     * @throws SQLException if an SQL exception occurs during initialization.
     */
    public AuthenticationService() {
        this.passwordSettingsViewRepository = new PasswordSettingsViewRepository();
        try {
            this.passwordSettings = this.passwordSettingsViewRepository.loadPasswordSettings();
            logger.log(Level.INFO, "Loaded {0} password settings!", this.passwordSettings.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        this.authenticationUtils = new AuthenticationUtils();
        this.userService = new UserService();
        
    }

    /**
     * Checks if the user is logging in for the first time.
     * A user is considered to be logging in for the first time if their last login timestamp is null.
     * 
     * @param username The username of the user to check.
     * @return {@code true} if the user is logging in for the first time, {@code false} otherwise.
     * @throws SQLException if a database error occurs while retrieving user information.
     */
    public boolean isFirstLogin(Object loginIdentifier) throws SQLException, IllegalArgumentException {
        return userService.getUserByIdentifier(loginIdentifier).getLastLoginAt() == null;
    }

    /**
     * Authenticates the user by checking their username and password.
     * This method verifies the user's existence, lock status, failed login attempts, password expiry, and checks if
     * the password matches the stored hash.
     * 
     * @param username The username of the user attempting to authenticate.
     * @param password The plain text password entered by the user.
     * @return {@code true} if the authentication is successful, {@code false} otherwise.
     * @throws SQLException if an SQL exception occurs during the authentication process.
     */
    public boolean authenticate(String username, String password) {
        User user = null; 
        try {
            user = userService.getUserByIdentifier(username);
        } catch (IllegalArgumentException | SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    
        if (user == null) {
            logger.log(Level.WARNING, "No user found!");
            return false;
        }
    
        if (user.getStatus() == User.UserStatus.LOCKED) {
            logger.log(Level.WARNING, "User is locked!");
            return false;
        }
    
        if (user.getNumFailedLoginAttempts() >= Integer.valueOf(passwordSettings.get("password.num_failed_attempts_before_lockout"))) {
            logger.log(Level.WARNING, "User exceded number of failed login attempts!");
            userService.lockUser(user.getId());
            return false;
        }
    
        if (user.getPasswordExpiryDate() != null && user.getPasswordExpiryDate().before(new Date(System.currentTimeMillis()))) {
            logger.log(Level.WARNING, "Password is expired!");
            return false;
        }
    
        if (!authenticationUtils.checkPassword(password, user.getPasswordHash())) {
            userService.incrementNumFailedLoginAttempts(user.getId(), user.getNumFailedLoginAttempts());
            logger.log(Level.WARNING, "Incorrect Password!");
            return false;
        }
        
        userService.resetNumFailedLoginAttempts(user.getId());
        logger.log(Level.INFO, "Successfully authenticated!");
        return true;
    }

    /**
     * Validates if the given password meets the required password policy.
     * The password must meet the specified criteria like minimum length, maximum length, required uppercase letters,
     * lowercase letters, numbers, and special characters.
     * Additionally, the password must not be part of the recent password history.
     * 
     * @param userId The user ID of the account attempting to set a new password.
     * @param newPassword The new password to be validated.
     * @return {@code true} if the password is valid, {@code false} otherwise.
     * @throws SQLException if a database error occurs while checking the password history.
     */
    public boolean isValidPassword(Integer userId, String newPassword) throws SQLException {
        if (newPassword.length() < Integer.parseInt(this.passwordSettings.get("password.min_length")) || 
            newPassword.length() > Integer.parseInt(this.passwordSettings.get("password.max_length"))) {
            return false;
        }
    
        if (this.passwordSettings.get("password.require_uppercase").equals("true") && !newPassword.matches(".*[A-Z].*")) {
            return false;
        }

        if (this.passwordSettings.get("password.require_lowercase").equals("true") && !newPassword.matches(".*[a-z].*")) {
            return false;
        }
    
        if (this.passwordSettings.get("password.require_numbers").equals("true") && !newPassword.matches(".*\\d.*")) {
            return false;
        }
    
        if (this.passwordSettings.get("password.require_special_characters").equals("true") && !newPassword.matches(".*[!@#$%^&*].*")) {
            return false;
        }
    
        return inRecentPasswordHistory(userId, newPassword);
    }

    /**
     * Changes the user's password.
     * This method validates the new password, hashes it, and updates the user's password in the database.
     * 
     * @param user The user whose password is being changed.
     * @param oldPassword The old password to be changed.
     * @param newPassword The new password to be set.
     * @return {@code true} if the password is successfully changed, {@code false} otherwise.
     * @throws SQLException if a database error occurs during the password change process.
     * @throws IllegalArgumentException if the user or password is invalid.
     */
    public boolean changePassword(User user, String oldPassword, String newPassword) throws SQLException {
        if (user == null || newPassword == null || newPassword.isEmpty() || oldPassword == null || oldPassword.isEmpty()) {
            throw new IllegalArgumentException("Missing or illegal argument!");
        }
    
        // Verify old password
        if (!authenticationUtils.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new SecurityException("Old password is incorrect!");
        }
    
        // Validate new password
        if (!isValidPassword(user.getId(), newPassword)) {
            throw new IllegalArgumentException("New password does not meet the security requirements!");
        }
    
        String hashedPassword = authenticationUtils.hashPassword(newPassword);
        if (user.getPasswordHash().equals(hashedPassword)) {
            throw new IllegalArgumentException("New password must be different from the old password!");
        }
    
        userService.updatePassword(user.getId(), hashedPassword);
        return true;
    }


    /**
     * Sets the initial password after first login.
     * This method validates the new password, hashes it, and updates the user's password in the database.
     * 
     * @param user The user whose password is being changed.
     * @param newPassword The new password to be set.
     * @return {@code true} if the password is successfully changed, {@code false} otherwise.
     * @throws SQLException if a database error occurs during the password change process.
     * @throws IllegalArgumentException if the user or password is invalid.
     */
    public boolean setInitialPassword(User user, String newPassword) throws SQLException {
        if (user == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Missing or illegal argument!");
        }
    
        if (!isValidPassword(user.getId(), newPassword)) {
            throw new IllegalArgumentException("Password is invalid!");
        }
        
        String hashedPassword = authenticationUtils.hashPassword(newPassword);

        userService.updatePassword(user.getId(), hashedPassword);
        return true;
    }

    /**
     * Checks if the new password is found in the user's recent password history.
     * 
     * @param userId The ID of the user whose password history is being checked.
     * @param newPassword The new password to check against the user's password history.
     * @return {@code true} if the new password is found in the recent password history, {@code false} otherwise.
     * @throws SQLException if a database error occurs while retrieving the password history.
     */
    public boolean inRecentPasswordHistory(Integer userId, String newPassword) throws SQLException {
        List<String> passwordHistory = PasswordHistoryViewRepository.loadPasswordHistory(userId, Integer.valueOf(passwordSettings.get("password.history_size")));

        return passwordHistory.contains(authenticationUtils.hashPassword(newPassword));
    }

}