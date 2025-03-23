package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.leanx.app.utils.DatabaseUtils;

public class UserEmployeeLinkRepository {

    public int saveUserEmployeeLink(Integer employeeId, Integer userId) throws SQLException {
        String sql = "INSERT INTO user_employee_link (user_id, employee_id) VALUES (?, ?)";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, employeeId);
            return stmt.executeUpdate();
        }
    }

    public int deleteUserEmployeeLink(Integer employeeId, Integer userId) throws SQLException {
        String sql = "DELETE FROM user_employee_link WHERE user_id = ? AND employee_id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, employeeId);
            return stmt.executeUpdate();
        }
    }

    public List<Integer> findUserIdByEmployeeId(Integer employeeId) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM user_employee_link WHERE employee_id = ?";
        
        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        }

        return userIds;
    }

    public Integer findEmployeeIdByUserId(Integer userId) throws SQLException{
        String sql = "SELECT employee_id FROM user_employee_link WHERE user_id = ?";
        
        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employee_id");
                }
            }
        }

        return null;
    }
    
}
