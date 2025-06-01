package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    // Consider making these configurable (e.g., from a properties file)
    private final String url = "jdbc:postgresql://localhost:5432/ProgettoSWE";
    private final String username = "Caffettino";
    private final String password = "Polloallagriglia";

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            // Consider a more robust error handling/logging strategy
            System.err.println("Driver PostgreSQL non trovato.");
            ex.printStackTrace();
            throw new SQLException("PostgreSQL driver not found.", ex);
        }
    }

    public Connection getConnection() throws SQLException {
        // Check if connection is closed or null, and re-establish if necessary
        if (connection == null || connection.isClosed()) {
            try {
                this.connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                System.err.println("Failed to re-establish database connection.");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        // No need to check instance.getConnection().isClosed() here,
        // as getConnection() now handles re-establishing.
        // However, for a truly robust pool, you'd use a proper connection pool library.
        return instance;
    }

    // Optional: method to close the connection when application shuts down
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}