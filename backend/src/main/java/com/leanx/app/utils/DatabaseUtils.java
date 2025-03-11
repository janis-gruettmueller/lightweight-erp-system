package com.leanx.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {

    private static final String JDBC_URL = "jdbc:mysql://" + System.getenv("RDS_MYSQL_ENDPOINT") + ":" + System.getenv("RDS_MYSQL_PORT") + "/";
    private static final String DB_USER = System.getenv("RDS_MYSQL_USER");
    private static final String DB_PASSWORD = System.getenv("RDS_MYSQL_PASSWORD");

    // Method to connect to database
    public static Connection getMySQLConnection(String DB_NAME) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found!", e);
        }
        return DriverManager.getConnection(JDBC_URL + DB_NAME, DB_USER, DB_PASSWORD);
    }
    
}
