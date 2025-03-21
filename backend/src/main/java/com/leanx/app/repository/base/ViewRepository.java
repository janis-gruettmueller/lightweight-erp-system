package com.leanx.app.repository.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.utils.DatabaseUtils;

public abstract class ViewRepository<T> {
    private static final Logger logger = Logger.getLogger(ViewRepository.class.getName());
    
    private Set<String> views = new HashSet<>();

    protected synchronized void lazyLoadViews() throws SQLException {
        if (this.views.isEmpty()) {
            logger.log(Level.INFO, "Loading views from database...");
            this.views = loadViews();
            logger.log(Level.INFO, "Loaded {0} views.", this.views.size());
        }
    }

    protected Set<String> loadViews() throws SQLException {
        Set<String> dbViews = new HashSet<>();
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, DatabaseUtils.DB_NAME);
            logger.log(Level.INFO, "Executing query: {0}", sql);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dbViews.add(rs.getString("TABLE_NAME").toLowerCase());
                }
            }
        }

        return dbViews;
    }

    public List<T> findAll(String viewName, Function<ResultSet, T> mapper) throws SQLException {
        lazyLoadViews();

        if (!views.contains(viewName.toLowerCase())) {
            throw new IllegalArgumentException("Error: " + viewName + " does not exist!");
        }

        String sql = "SELECT * FROM " + viewName;
        logger.log(Level.INFO, "Executing query: {0}", sql);

        try (Connection c = DatabaseUtils.getMySQLConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<T> resultList = new java.util.ArrayList<>();
            while (rs.next()) {
                resultList.add(mapper.apply(rs));
            }

            return resultList;
        }
    }
    
}