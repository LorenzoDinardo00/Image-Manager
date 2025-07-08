package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabaseConfig {
    private static Connection testConnection;
    private static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String TEST_DB_USER = "sa";
    private static final String TEST_DB_PASSWORD = "";

    public static Connection getTestConnection() throws SQLException {
        if (testConnection == null || testConnection.isClosed()) {
            testConnection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
            initializeDatabase();
        }
        return testConnection;
    }

    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            // Crea tabella users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "surname VARCHAR(100) NOT NULL," +
                    "dateofbirth DATE NOT NULL," +
                    "cellphone VARCHAR(20)," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(20) NOT NULL" +
                    ")");

            // Crea tabella posts
            stmt.execute("CREATE TABLE IF NOT EXISTS posts (" +
                    "post_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "author_username VARCHAR(50) NOT NULL," +
                    "image_data BLOB NOT NULL," +
                    "image_size BIGINT NOT NULL," +
                    "image_format VARCHAR(10) NOT NULL," +
                    "description TEXT," +
                    "likes_count INT DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (author_username) REFERENCES users(username)" +
                    ")");

            // Crea tabella comments
            stmt.execute("CREATE TABLE IF NOT EXISTS comments (" +
                    "comment_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "post_id INT NOT NULL," +
                    "commenter_username VARCHAR(50) NOT NULL," +
                    "comment_text TEXT NOT NULL," +
                    "commented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (post_id) REFERENCES posts(post_id)," +
                    "FOREIGN KEY (commenter_username) REFERENCES users(username)" +
                    ")");
        }
    }

    public static void clearDatabase() throws SQLException {
        try (Statement stmt = getTestConnection().createStatement()) {
            // Pulisce le tabelle nell'ordine corretto per rispettare le foreign key
            stmt.execute("DELETE FROM comments");
            stmt.execute("DELETE FROM posts");
            stmt.execute("DELETE FROM users");
            // Reset degli auto-increment
            stmt.execute("ALTER TABLE posts ALTER COLUMN post_id RESTART WITH 1");
            stmt.execute("ALTER TABLE comments ALTER COLUMN comment_id RESTART WITH 1");
        }
    }

    public static void closeTestConnection() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
            testConnection = null;
        }
    }
}