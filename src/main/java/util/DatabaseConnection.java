package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/college_events_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Yogaraj12345@@"; // CHANGE THIS TO YOUR MYSQL PASSWORD

    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
    }

    /**
     * Get a connection to the database
     * 
     * @return Connection object
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Register JDBC driver (optional for newer JDBC, but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Open a connection
                System.out.println("Connecting to database at " + URL + " ...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to the database successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found. Ensure it is added to your module path/classpath.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Connection Failed! Check output console: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Close the connection securely
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}
