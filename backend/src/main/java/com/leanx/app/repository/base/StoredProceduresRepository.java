package com.leanx.app.repository.base;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.leanx.app.utils.DatabaseUtils;

/**
 * Repository class responsible for calling stored procedures in the database.
 */
public class StoredProceduresRepository {

    /**
     * Calls the 'CreateNewUserAccount' stored procedure to create a new user account.
     *
     * @param username      The username for the new user account.
     * @param password_hash The hashed password for the new user account.
     * @param employeeId    The ID of the employee to link the new user account to.
     * @return {@code true} if the stored procedure executed successfully, {@code false} otherwise.
     * @throws IllegalArgumentException If any of the input parameters are null or invalid.
     */
    public boolean callCreateNewUserAccount(String username, String password_hash, Integer employeeId) throws IllegalArgumentException {
        if (username == null || username.isEmpty() || password_hash == null || password_hash.isEmpty() || employeeId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String sql = "{ CALL CreateNewUserAccount(?, ?, ?) }";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             CallableStatement stmt = c.prepareCall(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password_hash);
            stmt.setInt(3, employeeId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("An error occurred while calling the stored procedure 'CreateNewUserAccount': " + e.getMessage());
            return false;
        }
    }

    /**
     * Calls the 'DeactivateUserAccount' stored procedure to deactivate a user account.
     *
     * @param userId        The ID of the user account to deactivate.
     * @param currentUserId The ID of the user who is performing the deactivation.
     * @return {@code true} if the stored procedure executed successfully, {@code false} otherwise.
     * @throws IllegalArgumentException If any of the input parameters are null.
     */
    public boolean callDeactivateUserAccount(Integer userId, Integer currentUserId) throws IllegalArgumentException {
        if (userId == null || currentUserId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String sql = "{ CALL DeactivateUserAccount(?, ?) }";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             CallableStatement stmt = c.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, currentUserId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("An error occurred while calling the stored procedure 'DeactivateUserAccount': " + e.getMessage());
            return false;
        }
    }

    /**
     * Calls the 'TerminateEmployee' stored procedure to mark an employee as terminated.
     *
     * @param employeeId The ID of the employee to terminate.
     * @return {@code true} if the stored procedure executed successfully, {@code false} otherwise.
     * @throws IllegalArgumentException If the {@code employeeId} is null.
     */
    public boolean callTerminateEmployee(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String sql = "{ CALL TerminateEmployee(?) }";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             CallableStatement stmt = c.prepareCall(sql)) {

            stmt.setInt(1, employeeId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("An error occurred while calling the stored procedure 'TerminateEmployee': " + e.getMessage());
            return false;
        }
    }
}