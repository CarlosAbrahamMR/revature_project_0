package Application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static Connection connection;

    // Database connection details (Use environment variables or defaults)
    private static final String JDBC_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://localhost:5432/revatureLoans";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "oYVbxwdivOICAkIwh2DPxXsMltaaZm9EDSxYj03pAJT";

    public static void init() {
        try {
            // Load PostgreSQL Driver
            Class.forName("org.postgresql.Driver");

            // Establish connection
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            logger.info("Connected to PostgreSQL database successfully.");

        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL Driver not found.", e);
            throw new RuntimeException("PostgreSQL Driver not found.", e);
        } catch (SQLException e) {
            logger.error("Error connecting to PostgreSQL database.", e);
            throw new RuntimeException("Database connection error.", e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            init();
        }
        return connection;
    }
}
