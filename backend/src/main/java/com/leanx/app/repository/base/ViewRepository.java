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

/**
 * Abstract base class for repositories that interact with database views.
 * Provides functionality to lazily load available views and execute queries
 * against a specified view, mapping the results to a given entity type.
 *
 * @param <T> The type of the entity that the view data will be mapped to.
 */
public abstract class ViewRepository<T> {
    
    private static final Logger logger = Logger.getLogger(ViewRepository.class.getName());

    private Set<String> views = new HashSet<>();

    /**
     * Lazily loads the names of all available views from the database schema.
     * This method is synchronized to ensure thread-safe initialization of the
     * view set. The loading process occurs only once, when the view set is empty.
     *
     * @throws SQLException If a database access error occurs during the view loading process.
     */
    protected synchronized void lazyLoadViews() throws SQLException {
        if (this.views.isEmpty()) {
            logger.log(Level.INFO, "Loading views from database...");
            this.views = loadViews();
            logger.log(Level.INFO, "Loaded {0} views.", this.views.size());
        }
    }

    /**
     * Loads the names of all views present in the application's database schema.
     *
     * @return A {@code Set} containing the lowercase names of all database views.
     * @throws SQLException If a database access error occurs while querying the view metadata.
     */
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

    /**
     * Retrieves all records from the specified database view and maps each row
     * to an entity of type {@code T} using the provided mapper function.
     *
     * @param viewName The name of the database view to query.
     * @param mapper   A {@code Function} that takes a {@code ResultSet} (representing
     * a row from the view) and maps it to an instance of type {@code T}.
     * @return A {@code List} containing all entities of type {@code T} retrieved
     * from the specified view.
     * @throws SQLException             If a database access error occurs during the query execution.
     * @throws IllegalArgumentException If the specified {@code viewName} does not exist
     * in the database schema.
     */
    public List<T> findAll(String viewName, Function<ResultSet, T> mapper) throws SQLException {
        lazyLoadViews();

        if (!views.contains(viewName.toLowerCase())) {
            throw new IllegalArgumentException("Error: View '" + viewName + "' does not exist!");
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