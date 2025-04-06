package com.leanx.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing database connections.
 * Providing different methods to establish connections, test the connection,
 * and load the MySQL JDBC driver.
 * Configuration details such as JDBC URL, username, password, and database name
 * are expected to be provided through environment variables.
 */
public class DatabaseUtils {

    private static final Logger logger = Logger.getLogger(DatabaseUtils.class.getName());

    private static final String JDBC_URL = "jdbc:mysql://" + System.getenv("RDS_MYSQL_ENDPOINT") + ":" + System.getenv("RDS_MYSQL_PORT") + "/";
    private static final String DB_USER = System.getenv("RDS_MYSQL_USER");
    private static final String DB_PASSWORD = System.getenv("RDS_MYSQL_PASSWORD");
    /**
     * The name of the MySQL database to connect to, retrieved from the
     * 'RDS_MYSQL_DB_NAME' environment variable.
     */
    public static final String DB_NAME = System.getenv("RDS_MYSQL_DB_NAME");

    /**
     * Static initializer block that attempts to load the MySQL JDBC driver
     * when the {@code DatabaseUtils} class is loaded. If the driver is not found,
     * a {@link RuntimeException} is thrown, halting the application.
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to load MySQL JDBC Driver: {0}", e.getMessage());
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    /**
     * Establishes a connection to the MySQL database using the configuration
     * loaded from environment variables.
     *
     * @return A {@link Connection} object representing the database connection.
     * The caller is responsible for closing this connection after use.
     * @throws SQLException If a database access error occurs or the connection
     * cannot be established.
     */
    public static Connection getMySQLConnection() throws SQLException {
        try {
            Connection c = DriverManager.getConnection(JDBC_URL + DB_NAME, DB_USER, DB_PASSWORD);
            return c;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to database: {0}", JDBC_URL + DB_NAME + e.getMessage());
            throw e;
        }
    }

    /**
     * Tests the database connection by attempting to establish a connection
     * and validating it within a 5-second timeout.
     *
     * @return {@code true} if the connection is successfully established and valid,
     * {@code false} otherwise. Any {@link SQLException} during the
     * connection attempt or validation will be caught and logged.
     */
    public static boolean testMySQLConnection() {
        try (Connection connection = getMySQLConnection()) {
            return connection != null && connection.isValid(5); // Test connection with a 5-second timeout
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection test failed: {0}", e.getMessage());
            return false;
        }
    }
}