package main.java.com.leanx.app.service;

import java.util.List;
import java.util.Map;

import main.java.com.leanx.app.model.User;
import main.java.com.leanx.app.model.views.UserRoleView;

public class UserService {

    /**
     * Creates a new user account in the system.
     * @param currentUser The ID of the user performing the operation.
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @param userType The type of user to be created (e.g., UserType.ADMIN, UserType.NORMAL).
     * @return The newly created user object.
     */
    User createUserAccount(Integer currentUser, String username, String password, User.UserType userType);

    /**
     * Deactivates (soft-deletes) a user account by their ID.
     * @param currentUser The ID of the user performing the operation.
     * @param userId The ID of the user to deactivate.
     */
    void deactivateUserAccount(Integer currentUser, Integer userId);

    /**
     * Locks a user account to prevent access.
     * @param currentUser The ID of the user performing the operation.
     * @param userId The ID of the user to lock.
     */
    void lockUserAccount(Integer currentUser, Integer userId);

    /**
     * Unlocks a previously locked user account.
     * @param currentUser The ID of the user performing the operation.
     * @param userId The ID of the user to unlock.
     */
    void unlockUserAccount(Integer currentUser, Integer userId);

    /**
     * Authenticates a user account based on username and password.
     * @param username The username of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @return The user object if authentication is successful, or null if authentication fails.
     */
    User authenticateUserAccount(String username, String password);

    /**
     * Allows a user to change their password.
     * @param currentUser The ID of the user performing the password change.
     * @param userId The ID of the user changing their password.
     * @param oldPassword The user's current password.
     * @param newPassword The new password for the user.
     */
    void changePassword(Integer currentUser, Integer userId, String oldPassword, String newPassword);

    /**
     * Updates user details based on a map of changes.
     * @param currentUser The ID of the user performing the update.
     * @param userId The ID of the user whose details are being updated.
     * @param userUpdateRequest A map where keys are field names and values are the new values for the user.
     */
    void updateUserDetails(Integer currentUser, Integer userId, Map<String, Object> userUpdateRequest);

    /**
     * Initiates the password reset process for a user.
     * @param currentUser The ID of the user performing the password reset.
     * @param username The username of the user requesting a password reset.
     */
    void resetPassword(Integer currentUser, String username);

    /**
     * Verifies a user's email address using a verification token.
     * @param verificationToken The verification token sent to the user.
     * @return true if the email is successfully verified, false otherwise.
     */
    boolean verifyEmail(String verificationToken);

    /**
     * Checks if a user has a specific role.
     * @param userId The ID of the user.
     * @param roleName The name of the role to check for.
     * @return True if the user has the specified role, false otherwise.
     */
    boolean hasRole(Integer userId, String roleName);

    /**
     * Assigns a role to a user.
     * @param currentUser The ID of the user performing the role assignment.
     * @param userId The ID of the user to assign the role to.
     * @param roleId The ID of the role to assign.
     */
    void assignRoleToUser(Integer currentUser, Integer userId, Integer roleId);

    /**
     * Removes a role from a user.
     * @param currentUser The ID of the user performing the role removal.
     * @param userId The ID of the user to remove the role from.
     * @param roleId The ID of the role to remove.
     */
    void removeRoleFromUser(Integer currentUser, Integer userId, Integer roleId);

    /**
     * Retrieves the list of roles assigned to a user.
     * @param userId The ID of the user whose roles are being queried.
     * @return A list of UserRoleView objects representing the roles assigned to the user.
     */
    List<UserRoleView> getUserRoles(Integer userId);

}