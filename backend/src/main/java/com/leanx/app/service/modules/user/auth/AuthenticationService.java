package com.leanx.app.service.modules.user.auth;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.entity.User;
import com.leanx.app.model.entity.User.UserStatus;
import com.leanx.app.repository.PasswordHistoryViewRepository;
import com.leanx.app.repository.base.ViewRepository;
import com.leanx.app.service.modules.user.admin.UserService;
import com.leanx.app.service.modules.user.auth.exceptions.AccountLockedException;
import com.leanx.app.service.modules.user.auth.exceptions.FirstLoginException;
import com.leanx.app.service.modules.user.auth.exceptions.PasswordExpiredException;
import com.leanx.app.utils.PasswordUtils;

/**
 * The {@code AuthenticationService} class provides functionalities related to user authentication,
 * including password validation, login attempts, and password changes.
 * It handles user authentication, password history checking, password policy enforcement, and user lockout logic.
 */
public class AuthenticationService {

    private static final Logger logger = Logger.getLogger(ViewRepository.class.getName());
    
    private final UserService userService;
    private final PasswordUtils passwordUtils;

    /**
     * Constructs an instance of {@code AuthenticationService}.
     * This constructor initializes the user service.
     */
    public AuthenticationService() {
        this.userService = new UserService();
        this.passwordUtils = new PasswordUtils();
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
        try {
            User user = userService.getUserByIdentifier(username);

            if (user == null) {
                logger.log(Level.WARNING, "Failed to find user with username: {0}", username);
                return -1;
            }

            if (user.getStatus() == UserStatus.LOCKED) {
                if (user.getLockUntil() == null) {
                    logger.log(Level.WARNING, "Locked user attempted to login: {0}", username);
                    return -1;
                }

                if(user.getLockUntil() != null && System.currentTimeMillis() < user.getLockUntil().getTime() + passwordUtils.getLockoutDuration() * 60 * 1000) {
                    logger.log(Level.WARNING, "Failed login attempt due to user being temporarily locked: {0}", username);
                    userService.updateNumFailedLoginAttempts(user.getId(), 2, user.getNumFailedLoginAttempts() + 1);
                    throw new AccountLockedException("Too many failed login attempts! Your account is temporarily locked. Please try again later or contact support.");
                }

                userService.unlockUser(user.getId(), 2);
                user.setStatus(UserStatus.ACTIVE);
                user.setNumFailedLoginAttempts(0);
            }

            if (!passwordUtils.checkPassword(password, user.getPasswordHash())) {  
                userService.updateNumFailedLoginAttempts(user.getId(), 2, user.getNumFailedLoginAttempts() + 1);

                if (user.getNumFailedLoginAttempts() + 1 >= passwordUtils.getMaxNumFailedAttempts()) {
                    logger.log(Level.WARNING, "User locked due to exceeding allowed number of failed login attempts: {0}", username);
                    userService.lockUser(user.getId(), 2);
                    return -1;
                }

                logger.log(Level.WARNING, "Incorrect password attempt for user: {0}", username);
                return -1;
            }

            if (user.getPasswordExpiryDate() != null && user.getPasswordExpiryDate().before(new Date(System.currentTimeMillis()))) {
                logger.log(Level.WARNING, "Password expired for user: {0}", username);
                throw new PasswordExpiredException("Password is expired. Please set a new password.");
            }

            userService.resetNumFailedLoginAttempts(user.getId(), 2);
            logger.log(Level.INFO, "Successfully authenticated user: {0}", username);

            if (user.isFirstLogin()) {
                logger.log(Level.INFO, "Password change required prior to first login from user: {0}", username);
                throw new FirstLoginException("First Login. Please set a new password.");
            }

            userService.updateLastLoginAt(user.getId(), 2);
            return user.getId();
        } catch (IllegalArgumentException | SQLException e) {
            logger.log(Level.SEVERE, "Error authenticating user with username: {0}" + username, e);
            return -1;
        }
    }

    /**
     * Changes the user's password.
     * This method validates the new password against the policy, hashes it, and updates the user's password in the database.
     *
     * @param identifierObj The user identifier that {@link UserService#getUserByIdentifier(Object)} can handle.
     * @param changedBy     The ID of the user who is changing the password.
     * @param oldPassword   The old password to be changed.
     * @param newPassword   The new password to be set.
     * @return {@code true} if the password is successfully changed, {@code false} otherwise.
     * @throws SQLException             If a database error occurs during the password change process.
     * @throws IllegalArgumentException If the user or password is invalid.
     * @throws SecurityException        If the authentication fails.
     */
    public boolean changePassword(Object identifierObj, Integer changedBy, String newPassword, String confirmNewPassword) throws SQLException, IllegalArgumentException, SecurityException {
        if (identifierObj == null || newPassword == null || newPassword.isEmpty() || confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            throw new IllegalArgumentException("Missing or illegal argument!");
        }

        User user = userService.getUserByIdentifier(identifierObj);
    
        if (user == null) {
            throw new IllegalArgumentException("User not found!");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("Passwords do not match!");
        }

        if (!passwordUtils.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password does not meet security requirements");
        }

        if (inRecentPasswordHistory(user.getId(), newPassword)) {
            throw new IllegalArgumentException("New password is in recent password history!");
        }

        if(user.isFirstLogin()) {
            userService.setIsFirstLoginFalse(user.getId(), 2);
        }

        String newPasswordHash = passwordUtils.hashPassword(newPassword);
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
        List<String> passwordHistory = PasswordHistoryViewRepository.loadPasswordHistory(userId, passwordUtils.getHistorySize());
        boolean found = false;
        for (String hash : passwordHistory) {
            if(passwordUtils.checkPassword(newPassword, hash)) {
                found = true;
            }
        }
        
        return found;
    }

}