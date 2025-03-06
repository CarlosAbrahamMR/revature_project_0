package Application.dao;

import Application.advice.DatabaseException;
import Application.model.User;
import Application.model.Loans;
import Application.model.UserRole;
import Application.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // CREATE USER
    public void insert(User user) {
        String sql = "INSERT INTO USERS (NAME, EMAIL, PASSWORD, ROLE) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().name());
            pstmt.executeUpdate();

            // Obtener ID generado
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
                logger.info("User created successfully. ID: {}, Email: {}", user.getId(), user.getEmail());
            }
        } catch (SQLException e) {
            logger.error("Error inserting user: {}", user, e);
            throw new DatabaseException("Error inserting user", e);
        }
    }

    // READ ALL USERS
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            logger.info("Retrieved {} users from database.", users.size());
        } catch (SQLException e) {
            logger.error("Error retrieving users", e);
            throw new DatabaseException("Error retrieving users", e);
        }
        return users;
    }

    // READ USER BY ID (Including Loans)
    public User findById(int id) {
        User user = null;
        String sql = "SELECT * FROM USERS WHERE ID = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = mapResultSetToUser(rs);
                user.setLoans(new LoansDao().findByUserId(user.getId()));
                logger.info("User found with ID: {}", id);
            } else {
                logger.warn("No user found with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user with ID: {}", id, e);
            throw new DatabaseException("Error retrieving user with ID: " + id, e);
        }
        return user;
    }

    // UPDATE USER
    public void update(User user) {
        String sql = "UPDATE USERS SET NAME = ?, EMAIL = ?, PASSWORD = ?, ROLE = ? WHERE ID = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().name());
            pstmt.setLong(5, user.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                logger.warn("User update failed, no rows affected. User ID: {}", user.getId());
                throw new DatabaseException("User update failed, no rows affected.",null);
            }

            logger.info("User updated successfully. ID: {}, Email: {}", user.getId(), user.getEmail());
        } catch (SQLException e) {
            logger.error("Error updating user. User ID: {}", user.getId(), e);
            throw new DatabaseException("Error updating user", e);
        }
    }

    // DELETE USER
    public void delete(int id) {
        String sql = "DELETE FROM USERS WHERE ID = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("User deleted successfully. ID: {}", id);
            } else {
                logger.warn("Attempted to delete non-existent user with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting user ID: {}", id, e);
            throw new DatabaseException("Error deleting user", e);
        }
    }

    // FIND USER BY EMAIL
    public User findByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM USERS WHERE EMAIL = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = mapResultSetToUser(rs);
                user.setLoans(new LoansDao().findByUserId(user.getId()));
                logger.info("User found with email: {}", email);
            } else {
                logger.warn("No user found with email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user with email: {}", email, e);
            throw new DatabaseException("Error retrieving user with email: " + email, e);
        }
        return user;
    }

    // MAP RESULTSET TO USER OBJECT
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("ID"));
        user.setName(rs.getString("NAME"));
        user.setEmail(rs.getString("EMAIL"));
        user.setPassword(rs.getString("PASSWORD"));
        user.setRole(UserRole.valueOf(rs.getString("ROLE")));
        return user;
    }
}
