package com.leanx.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtils {

    private static final Logger logger = Logger.getLogger(DatabaseUtils.class.getName());

    private static final String JDBC_URL = "jdbc:mysql://" + System.getenv("RDS_MYSQL_ENDPOINT") + ":" + System.getenv("RDS_MYSQL_PORT") + "/";
    private static final String DB_USER = System.getenv("RDS_MYSQL_USER");
    private static final String DB_PASSWORD = System.getenv("RDS_MYSQL_PASSWORD");
    public static final String DB_NAME = System.getenv("RDS_MYSQL_DB_NAME");

    // Static block to load the MySQL JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to load MySQL JDBC Driver: {0}", e.getMessage());
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    // Method to connect to database
    public static Connection getMySQLConnection() throws SQLException {
        try {
            Connection c = DriverManager.getConnection(JDBC_URL + DB_NAME, DB_USER, DB_PASSWORD);
            return c;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to database: {0}", JDBC_URL + DB_NAME + e.getMessage());
            throw e;
        }
    }

    // Method to test the database connection (optional)
    public static boolean testMySQLConnection() {
        try (Connection connection = getMySQLConnection()) {
            return connection != null && connection.isValid(5); // Test connection with a 5-second timeout
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection test failed: {0}", e.getMessage());
            return false;
        }
    }  
}
