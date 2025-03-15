package com.leanx.app.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.leanx.app.utils.DatabaseUtils;

public abstract class ViewRepository<T> {

    protected Set<String> views;

    
    public ViewRepository() {
        try {
            this.views = loadViews();
        } catch (SQLException e) {
            System.err.println("Error initializing ViewRepository: " + e.getMessage());
        }
    }

    public static Set<String> loadViews() throws SQLException {
        Set<String> views = new HashSet<>();
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ?";

        try (java.sql.Connection connection = DatabaseUtils.getMySQLConnection();
             java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, DatabaseUtils.DB_NAME);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    views.add(rs.getString("TABLE_NAME"));
                }
            }
        }
        return views;
    }
    

    public List<T> findAll(String viewName, Function<ResultSet, T> mapper) throws SQLException {
        if (!this.views.contains(viewName)) {
            throw new IllegalArgumentException("Error: " + viewName + " does not exist!");
        }

        String sql = "SELECT * FROM " + viewName;

        try (java.sql.Connection connection = com.leanx.app.utils.DatabaseUtils.getMySQLConnection();
             java.sql.Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<T> resultList = new java.util.ArrayList<>();
            while (rs.next()) {
                resultList.add(mapper.apply(rs));
            }
            return resultList;
        }
    }

}