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

/**
 * Service class for administrative operations related to user accounts.
 * Provides functionalities for retrieving, updating, locking, unlocking,
 * assigning roles, linking to employees, and generating usernames.
 */
public final class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userCrudRepository;
    private final UserEmployeeLinkRepository userEmployeeLinkRepository;
    private final StoredProceduresRepository storedProceduresRepository;

    /**
     * Constructs a new {@code UserService} instance, initializing the
     * necessary repositories.
     */
    public UserService() {
        this.userCrudRepository = new UserRepository();
        this.userEmployeeLinkRepository = new UserEmployeeLinkRepository();
        this.storedProceduresRepository = new StoredProceduresRepository();
    }

    /**
     * Executes an update operation on a user record in the database.
     *
     * @param updates A map containing the fields to update and their new values.
     * The keys of the map should correspond to the database column names.
     * @param userId  The ID of the user to update.
     * @return {@code true} if the update was successful (at least one row was affected),
     * {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    private boolean executeUpdate(Map<String, Object> updates, Integer userId) throws SQLException {
        try {
            return userCrudRepository.update(userId, updates) > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update user with ID: " + userId, e);
            throw e;
        }
    }

    /**
     * Retrieves the unique ID of a user based on their username.
     *
     * @param username The username of the user to find.
     * @return The ID of the user if found, otherwise -1.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
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

    /**
     * Retrieves a user entity based on either their username (String) or user ID (Integer).
     *
     * @param identifier The username (String) or user ID (Integer) of the user to retrieve.
     * @return The {@link User} entity corresponding to the provided identifier.
     * @throws SQLException             If a database access error occurs during the retrieval.
     * @throws IllegalArgumentException If the provided identifier is neither a String nor an Integer.
     */
    public User getUserByIdentifier(Object identifier) throws SQLException, IllegalArgumentException {
        if (identifier instanceof String username) {
            return userCrudRepository.read(username);
        }

        if (identifier instanceof Integer userId) {
            return userCrudRepository.read(userId);
        }

        throw new IllegalArgumentException("Invalid identifier type! Expected String or Integer.");
    }

    /**
     * Updates the password hash for a specific user.
     *
     * @param userId         The ID of the user whose password is to be updated.
     * @param updatedBy      The ID of the user who performed the update.
     * @param hashedPassword The new hashed password.
     * @return {@code true} if the password was updated successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean updatePassword(Integer userId, Integer updatedBy, String hashedPassword) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("password_hash", hashedPassword);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Updates the 'last_login_at' timestamp for a specific user.
     *
     * @param userId    The ID of the user who logged in.
     * @param updatedBy The ID of the user who triggered the update (could be the same user).
     * @return {@code true} if the last login timestamp was updated successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean updateLastLoginAt(Integer userId, Integer updatedBy) throws SQLException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> updates = new HashMap<>();
        updates.put("last_login_at", timestamp);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Sets the 'is_first_login' flag to false for a specific user.
     *
     * @param userId    The ID of the user whose first login status is to be updated.
     * @param updatedBy The ID of the user who performed the update.
     * @return {@code true} if the flag was updated successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean setIsFirstLoginFalse(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("is_first_login", false);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Updates the number of failed login attempts for a specific user.
     *
     * @param userId            The ID of the user whose failed login attempts count is to be updated.
     * @param updatedBy         The ID of the user who performed the update.
     * @param numFailedAttempts The new number of failed login attempts.
     * @return {@code true} if the count was updated successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean updateNumFailedLoginAttempts(Integer userId, Integer updatedBy, Integer numFailedAttempts) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("num_failed_login_attempts", numFailedAttempts);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Resets the number of failed login attempts to zero for a specific user.
     *
     * @param userId    The ID of the user whose failed login attempts count is to be reset.
     * @param updatedBy The ID of the user who performed the update.
     * @return {@code true} if the count was reset successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean resetNumFailedLoginAttempts(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("num_failed_login_attempts", 0);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Locks a specific user account by setting its status to "LOCKED".
     *
     * @param userId    The ID of the user to lock.
     * @param updatedBy The ID of the user who performed the lock operation.
     * @return {@code true} if the user was locked successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean lockUser(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "LOCKED");
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Unlocks a specific user account by setting its status to "ACTIVE" and clearing the lock_until timestamp.
     *
     * @param userId    The ID of the user to unlock.
     * @param updatedBy The ID of the user who performed the unlock operation.
     * @return {@code true} if the user was unlocked successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    public boolean unlockUser(Integer userId, Integer updatedBy) throws SQLException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "ACTIVE");
        updates.put("lock_until", null);
        updates.put("last_updated_by", updatedBy);
        return executeUpdate(updates, userId);
    }

    /**
     * Assigns a specific role to a user.
     *
     * @param userId     The ID of the user to whom the role is to be assigned.
     * @param assignedBy The ID of the user who performed the role assignment.
     * @param role       The role to assign to the user.
     * @return {@code true} if the role was assigned successfully, {@code false} otherwise.
     * This functionality is currently a placeholder.
     */
    public boolean assignRole(Integer userId, Integer assignedBy, String role) {
        // TODO: Implement the logic for assigning roles to users.
        return false;
    }

    /**
     * Removes a specific role from a user.
     *
     * @param userId     The ID of the user from whom the role is to be removed.
     * @param assignedBy The ID of the user who performed the role removal.
     * @param role       The role to remove from the user.
     * @return {@code true} if the role was removed successfully, {@code false} otherwise.
     * This functionality is currently a placeholder.
     */
    public boolean removeRole(Integer userId, Integer assignedBy, String role) {
        // TODO: Implement the logic for removing roles from users.
        return false;
    }

    /**
     * Retrieves a list of roles assigned to a specific user.
     *
     * @param userId The ID of the user whose roles are to be retrieved.
     * @return A list of strings representing the roles assigned to the user.
     * This functionality is currently a placeholder.
     */
    public List<String> getRolesForUser(Integer userId) {
        // TODO: Implement the logic for retrieving roles for a user.
        List<String> roles = new ArrayList<>();
        return roles;
    }

    /**
     * Links a user account to an employee record. This establishes a relationship
     * between the user's login credentials and their employee profile.
     *
     * @param userId     The ID of the user to link.
     * @param employeeId The ID of the employee to link to.
     * @return {@code true} if the link was created successfully, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the linking process.
     */
    public boolean linkUserToEmployee(Integer userId, Integer employeeId) throws SQLException {
        try {
            int affectedRows = userEmployeeLinkRepository.saveUserEmployeeLink(employeeId, userId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to link user with ID: " + userId + " to employee with ID: " + employeeId, e);
            throw e;
        }
    }

    /**
     * Generates a unique username based on the employee's first and last name.
     * The username is generated by taking the first letter of the first name
     * and appending the lowercase version of the last name (with German umlauts
     * replaced). If the generated username already exists, a counter is appended
     * to ensure uniqueness. The maximum length of the base username (before the
     * counter) is 7 characters.
     *
     * @param firstName The first name of the employee.
     * @param lastName  The last name of the employee.
     * @return A unique username generated based on the provided names.
     */
    public String generateUsername(String firstName, String lastName) {
        int MAX_LENGTH = 7;

        String normalizedFirstName = firstName.toLowerCase(Locale.GERMAN)
            .replace(" ", "")
            .replace("-", "")
            .replace("ä", "ae")
            .replace("ü", "ue")
            .replace("ö", "oe");
        String normalizedLastName = lastName.toLowerCase(Locale.GERMAN)
            .replace(" ", "")
            .replace("-", "")
            .replace("ä", "ae")
            .replace("ü", "ue")
            .replace("ö", "oe");

        String baseUsername = normalizedFirstName.substring(0, 1) + normalizedLastName;
        if (baseUsername.length() > MAX_LENGTH) {
            baseUsername = baseUsername.substring(0, MAX_LENGTH);
        }

        String username = baseUsername;
        int counter = 1;
        try {
            while (getUserId(username) != -1) {
                username = baseUsername + counter;
                counter++;
            }
        } catch (SQLException e) {
            // Expected condition when checking for username existence
        }

        return username;
    }

    /**
     * Deactivates a user account using a stored procedure.
     *
     * @param userId        The ID of the user account to deactivate.
     * @param currentUserId The ID of the user performing the deactivation.
     * @return {@code true} if the stored procedure call was successful, {@code false} otherwise.
     * @throws IllegalArgumentException If there are issues with the input parameters for the
     * stored procedure (as defined by the procedure itself).
     */
    public boolean deactivateUser(Integer userId, Integer currentUserId) throws IllegalArgumentException {
        return storedProceduresRepository.callDeactivateUserAccount(userId, currentUserId);
    }
}