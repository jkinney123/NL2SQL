package com.myproject;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/parties_db";
    private static final String USER = "root";
    private static final String PASS = "password";
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

        static {
            try {
                FileHandler fileHandler = new FileHandler("application.log", true);
                logger.addHandler(fileHandler);
            } catch (IOException e) {
                e.printStackTrace();  // Print directly to system output for debugging
            }
        }

    public static Map<String, List<String>> fetchDatabaseSchema() {
        Connection conn = null;
        Map<String, List<String>> schema = new HashMap<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            if (conn == null) {
                logger.log(Level.WARNING, "Connection object is null.");
                return schema;  // Return an empty map
            }

            DatabaseMetaData metaData = conn.getMetaData();

            // Fetching Table Names
            ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});
            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                tableNames.add(tables.getString(3));  // Adding table names to the list
            }

            // Fetching Column Names for Each Table
            for (String tableName : tableNames) {
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                List<String> columnNames = new ArrayList<>();
                while (columns.next()) {
                    columnNames.add(columns.getString("COLUMN_NAME"));
                }
                schema.put(tableName, columnNames);  // Storing columns per table
                // For tables

            }

        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "An exception was thrown", e);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "An exception was thrown", e);
            }
        }
        logger.log(Level.INFO, "Fetched Database Schema: " + schema);
        return schema;  // Return the fetched schema
    }

    public static void executeSQLQuery(String sqlQuery) {
        System.out.println("Received SQL: " + sqlQuery);
        if (sqlQuery.trim().toUpperCase().startsWith("SELECT")) {
            executeReadQuery(sqlQuery);
        } else {
            executeWriteQuery(sqlQuery);
        }
    }

    private static void executeReadQuery(String sqlQuery) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            if (conn != null) {
                System.out.println("Connected to the database");
            } else {
                logger.log(Level.WARNING, "Connection object is null.");
                return;
            }

            stmt = conn.createStatement();
            if (stmt != null) {
                System.out.println("Executing SQL Query: " + sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
            } else {
                logger.log(Level.WARNING, "Statement object is null.");
                return;
            }

            int rowCount = 0;
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    System.out.println(columnName + ": " + rs.getString(i));
                }
                rowCount++;
            }

            System.out.println("Rows returned: " + rowCount);

        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "An exception was thrown", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "An exception was thrown", e);
            }
        }
    }

    private static void executeWriteQuery(String sqlQuery) {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            if (conn != null) {
                System.out.println("Connected to the database");
            } else {
                logger.log(Level.WARNING, "Connection object is null.");
                return;
            }

            stmt = conn.createStatement();
            if (stmt != null) {
                System.out.println("Executing SQL Query: " + sqlQuery);
                int affectedRows = stmt.executeUpdate(sqlQuery);
                System.out.println("Affected rows: " + affectedRows);
            } else {
                logger.log(Level.WARNING, "Statement object is null.");
            }

        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "An exception was thrown", e);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "An exception was thrown", e);
            }
        }
    }
}
