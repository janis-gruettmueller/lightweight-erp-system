package com.leanx.app.service.modules.user.auth;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import com.leanx.app.model.User;
import com.leanx.app.model.User.UserStatus;
import com.leanx.app.repository.PasswordHistoryViewRepository;
import com.leanx.app.repository.ViewRepository;
import com.leanx.app.service.modules.system.configs.SecurityConfig;
import com.leanx.app.service.modules.user.admin.UserService;
import com.leanx.app.service.modules.user.auth.exceptions.AccountLockedException;
import com.leanx.app.service.modules.user.auth.exceptions.FirstLoginException;
import com.leanx.app.service.modules.user.auth.exceptions.PasswordExpiredException;

/**
 * The {@code AuthenticationService} class provides functionalities related to user authentication,
 * including password validation, login attempts, and password changes.
 * It handles user authentication, password history checking, password policy enforcement, and user lockout logic.
 */
public class AuthenticationService {

    private static final Logger logger = Logger.getLogger(ViewRepository.class.getName());

    private final Map<String, String> passwordSettings = SecurityConfig.PASSWORD_SETTINGS;
    private final UserService userService;

    /**
     * Constructs an instance of {@code AuthenticationService}.
     * This constructor initializes the user service.
     */
    public AuthenticationService() {
        this.userService = new UserService();
    }

    /**
     * Hashes the plain text password using BCrypt.
     *
     * @param plainTextPassword The plain text password.
     * @return The hashed password.
     */
    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks if the provided plain text password matches the hashed password.
     *
     * @param plainTextPassword The plain text password.
     * @param hashedPassword    The password hash of the user object.
     * @return {@code true} if passwords match, {@code false} otherwise.
     */
    private boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    /**
     * Authenticates the user by checking their username and password.
     * This method verifies the user's existence, lock status, failed login attempts, password expiry, and checks if
     * the password matches the stored hash.
     *
     * @param username The username of the user attempting to authenticate.
     * @param password The plain text password entered by the user.
     * @return The user ID if authentication is successful, {@code -1} otherwise.
     * @throws AccountLockedException  If the user's account is locked.
     * @throws PasswordExpiredException If the user's password has expired.
     * @throws FirstLoginException      If the user needs to change their password before first login.
     */
    public int authenticate(String username, String password) throws AccountLockedException, PasswordExpiredException, FirstLoginException {
        User user;
        try {
            user = userService.getUserByIdentifier(username);
        } catch (IllegalArgumentException | SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving user with username: {0}" + username, e);
            return -1;
        }

        if (user == null) {
            logger.log(Level.WARNING, "No user found for username: {0}", username);
            return -1;
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            if (user.getLockUntil() == null) {
                logger.log(Level.WARNING, "Locked user attempted to login: {0}", username);
                return -1;
            }

            if(user.getLockUntil() != null && System.currentTimeMillis() < user.getLockUntil().getTime() + 30 * 60 * 1000) {
                logger.log(Level.WARNING, "Failed login attempt due to user being temporarily locked: {0}", username);
                userService.updateNumFailedLoginAttempts(user.getId(), 2, user.getNumFailedLoginAttempts() + 1);
                throw new AccountLockedException("Your account is temporarily locked due to multiple failed login attempts. Please try again later or contact support.");
            }

            userService.unlockUser(user.getId(), 2);
            user.setStatus(UserStatus.ACTIVE);
            user.setNumFailedLoginAttempts(0);
        }

        if (!this.checkPassword(password, user.getPasswordHash())) {
            int maxFailedAttempts = Integer.parseInt(passwordSettings.get("password.num_failed_attempts_before_lockout"));
            
            userService.updateNumFailedLoginAttempts(user.getId(), 2, user.getNumFailedLoginAttempts() + 1);
            if (user.getNumFailedLoginAttempts() + 1 >= maxFailedAttempts) {
                logger.log(Level.WARNING, "User locked due to exceeding allowed number of failed login attempts: {0}", username);
                userService.lockUser(user.getId(), 2);
                return -1;
            }

            logger.log(Level.WARNING, "Incorrect password attempt for user: {0}", username);
            return -1;
        }

        if (user.getPasswordExpiryDate() != null && user.getPasswordExpiryDate().before(new Date(System.currentTimeMillis()))) {
            logger.log(Level.WARNING, "Password expired for user: {0}", username);
            throw new PasswordExpiredException("Password has expired. Please reset your password.");
        }

        userService.resetNumFailedLoginAttempts(user.getId(), 2);
        logger.log(Level.INFO, "Successfully authenticated user: {0}", username);

        if (user.isFirstLogin()) {
            logger.log(Level.INFO, "Password change required prior to first login from user: {0}", username);
            throw new FirstLoginException("Password change required before first login!");
        }

        userService.updateLastLoginAt(user.getId(), 2);
        return user.getId();
    }

    /**
     * Validates if the given password meets the required password policy.
     * The password must meet the specified criteria like minimum length, maximum length, required uppercase letters,
     * lowercase letters, numbers, and special characters.
     * Additionally, the password must not be part of the recent password history.
     *
     * @param userId      The user ID of the account attempting to set a new password.
     * @param newPassword The new password to be validated.
     * @return {@code true} if the password is valid, {@code false} otherwise.
     * @throws SQLException If a database error occurs while checking the password history.
     */
    public boolean isValidPassword(Integer userId, String newPassword) throws SQLException {
        if (newPassword == null || newPassword.isEmpty()) {
            return false;
        }

        int minLength = Integer.parseInt(this.passwordSettings.get("password.min_length"));
        int maxLength = Integer.parseInt(this.passwordSettings.get("password.max_length"));

        if (newPassword.length() < minLength || newPassword.length() > maxLength) {
            return false;
        }

        if (Boolean.parseBoolean(this.passwordSettings.get("password.require_uppercase")) &&
                !newPassword.matches(".*[A-Z].*")) {
            return false;
        }

        if (Boolean.parseBoolean(this.passwordSettings.get("password.require_lowercase")) &&
                !newPassword.matches(".*[a-z].*")) {
            return false;
        }

        if (Boolean.parseBoolean(this.passwordSettings.get("password.require_numbers")) &&
                !newPassword.matches(".*\\d.*")) {
            return false;
        }

        if (Boolean.parseBoolean(this.passwordSettings.get("password.require_special_characters")) &&
                !newPassword.matches(".*[!@#$%^&*].*")) {
            return false;
        }

        return !inRecentPasswordHistory(userId, newPassword);
    }

    /**
     * Changes the user's password.
     * This method validates the old password, validates the new password against the policy, hashes it, and updates the user's password in the database.
     *
     * @param identifierObj The user identifier, either a User object or a user identifier that {@link UserService#getUserByIdentifier(Object)} can handle.
     * @param changedBy     The ID of the user who is changing the password.
     * @param oldPassword   The old password to be changed.
     * @param newPassword   The new password to be set.
     * @return {@code true} if the password is successfully changed, {@code false} otherwise.
     * @throws SQLException             If a database error occurs during the password change process.
     * @throws IllegalArgumentException If the user or password is invalid.
     * @throws SecurityException        If the authentication fails.
     */
    public boolean changePassword(Object identifierObj, Integer changedBy, String oldPassword, String newPassword) throws SQLException, IllegalArgumentException, SecurityException {
        if (identifierObj == null || newPassword == null || newPassword.isEmpty() || oldPassword == null || oldPassword.isEmpty()) {
            throw new IllegalArgumentException("Missing or illegal argument!");
        }

        User user;
        if (identifierObj instanceof User userObj) {
            user = userObj;
        } else {
            user = userService.getUserByIdentifier(identifierObj);
        }
    
        if (user == null) {
            throw new IllegalArgumentException("User not found!");
        }

        if (!this.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new SecurityException("Authentication failure!");
        }

        if (!isValidPassword(user.getId(), newPassword)) {
            throw new IllegalArgumentException("New password does not meet security requirements!");
        }

        if(user.isFirstLogin()) {
            userService.setIsFirstLoginFalse(user.getId(), 2);
        }

        String newPasswordHash = this.hashPassword(newPassword);
        return userService.updatePassword(user.getId(), changedBy, newPasswordHash);
    }

    /**
     * Checks if the new password is found in the user's recent password history entries.
     * 
     * @param userId The ID of the user whose password history is being checked.
     * @param newPassword The new password to check against the user's password history.
     * @return {@code true} if the new password is found in the recent password history, {@code false} otherwise.
     * @throws SQLException if a database error occurs while retrieving the password history.
     */
    public boolean inRecentPasswordHistory(Integer userId, String newPassword) throws SQLException {
        List<String> passwordHistory = PasswordHistoryViewRepository.loadPasswordHistory(userId, Integer.valueOf(passwordSettings.get("password.history_size")));
        boolean check = false;
        for (String hash : passwordHistory) {
            if(checkPassword(newPassword, hash)) {
                check = true;
            }
        }
        
        return check;
    }

}