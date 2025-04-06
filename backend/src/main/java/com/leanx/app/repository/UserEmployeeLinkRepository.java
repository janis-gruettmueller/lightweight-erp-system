package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.leanx.app.utils.DatabaseUtils;

/**
 * Repository class for managing the links between user accounts and employee records
 * in the {@code user_employee_link} table. Provides methods to create, delete,
 * and query these links.
 */
public class UserEmployeeLinkRepository {

    /**
     * Saves a link between a user and an employee in the database.
     *
     * @param employeeId The ID of the employee to link.
     * @param userId     The ID of the user to link.
     * @return The number of rows affected by the insert operation (should be 1 on success).
     * @throws SQLException If a database access error occurs during the operation.
     */
    public int saveUserEmployeeLink(Integer employeeId, Integer userId) throws SQLException {
        String sql = "INSERT INTO user_employee_link (user_id, employee_id) VALUES (?, ?)";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, employeeId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Deletes a link between a user and an employee from the database.
     *
     * @param employeeId The ID of the employee to unlink.
     * @param userId     The ID of the user to unlink.
     * @return The number of rows affected by the delete operation (should be 1 on success).
     * @throws SQLException If a database access error occurs during the operation.
     */
    public int deleteUserEmployeeLink(Integer employeeId, Integer userId) throws SQLException {
        String sql = "DELETE FROM user_employee_link WHERE user_id = ? AND employee_id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, employeeId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Finds all user IDs associated with a given employee ID.
     * In a typical one-to-one relationship, this list should contain at most one ID.
     *
     * @param employeeId The ID of the employee.
     * @return A {@code List} of user IDs linked to the given employee ID.
     * Returns an empty list if no links are found.
     * @throws SQLException If a database access error occurs during the query.
     */
    public List<Integer> findUserIdByEmployeeId(Integer employeeId) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM user_employee_link WHERE employee_id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        }

        return userIds;
    }

    /**
     * Finds the employee ID associated with a given user ID.
     * Assuming a one-to-one relationship, this method returns a single employee ID or null.
     *
     * @param userId The ID of the user.
     * @return The employee ID linked to the given user ID, or {@code null} if no link exists.
     * @throws SQLException If a database access error occurs during the query.
     */
    public Integer findEmployeeIdByUserId(Integer userId) throws SQLException {
        String sql = "SELECT employee_id FROM user_employee_link WHERE user_id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employee_id");
                } else {
                    return null;
                }
            }
        }
    }
}