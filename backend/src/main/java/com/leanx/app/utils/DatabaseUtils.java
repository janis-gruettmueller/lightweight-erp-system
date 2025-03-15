package com.leanx.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseUtils {
    private static final String RDS_MYSQL_ENDPOINT = "leanx-erp-db-server-eu-north-1b.cpkae4uasaxr.eu-north-1.rds.amazonaws.com";
    private static final String RDS_MYSQL_PORT = "3306";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin-mysqlDB";
    public static final String DB_NAME = "leanx_erp_prod";
    private static final String JDBC_URL = "jdbc:mysql://" + RDS_MYSQL_ENDPOINT + ":" + RDS_MYSQL_PORT + "/";

    /*
    private static final String JDBC_URL = "jdbc:mysql://" + System.getenv("RDS_MYSQL_ENDPOINT") + ":" + System.getenv("RDS_MYSQL_PORT") + "/";
    private static final String DB_USER = System.getenv("RDS_MYSQL_USER");
    private static final String DB_PASSWORD = System.getenv("RDS_MYSQL_PASSWORD");
    public static final String DB_NAME = System.getenv("RDS_MYSQL_DB_NAME");
    */

    // Method to connect to database
    public static Connection getMySQLConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found!", e);
        }
        return DriverManager.getConnection(JDBC_URL + DB_NAME, DB_USER, DB_PASSWORD);
    }
    
}
