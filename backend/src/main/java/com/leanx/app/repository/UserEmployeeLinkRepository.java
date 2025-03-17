package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.leanx.app.utils.DatabaseUtils;

public class UserEmployeeLinkRepository {

    public void linkUserToEmployee(Integer employeeId, Integer userId) throws SQLException {
        String sql = "INSERT INTO user_employee_link (user_id, employee_id) VALUES (?, ?)";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
        }
    }

    public List<Integer> findAllUsersForEmployee(Integer employeeId) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM employee_user_link WHERE employee_id = ?";
        
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
    
}
