package com.leanx.app.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.leanx.app.utils.DatabaseUtils;

public class StoredProceduresRepository {

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
            System.err.println("An error occured while calling the stored procedure 'CreateNewUserAccount'" + e.getMessage());
            return false;
        }
    }

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
            System.err.println("An error occured while calling the stored procedure 'DeactivateUserAccount'" + e.getMessage());
            return false;
        }
    }


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
            System.err.println("An error occured while calling the stored procedure 'TerminateEmployee'" + e.getMessage());
            return false;
        }
    }
}
