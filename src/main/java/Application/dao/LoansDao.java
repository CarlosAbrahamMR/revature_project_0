package Application.dao;

import Application.advice.DatabaseException;
import Application.model.Loans;
import Application.model.User;
import Application.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoansDao {
    private static final Logger logger = LoggerFactory.getLogger(LoansDao.class);

    // CREATE LOAN
    public void insert(Loans loan) {
        String sql = "INSERT INTO LOANS (DESCRIPTION, AMOUNT, PAID, USER_ID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, loan.getDescription());
            pstmt.setDouble(2, loan.getAmount());
            pstmt.setBoolean(3, loan.isApproved());
            pstmt.setLong(4, loan.getUser().getId());
            pstmt.executeUpdate();

            // Obtener ID generado
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                loan.setId(generatedKeys.getInt(1));
                logger.info("Loan created successfully. ID: {}", loan.getId());
            }
        } catch (SQLException e) {
            logger.error("Error inserting loan: {}", loan, e);
            throw new DatabaseException("Error inserting loan", e);
        }
    }

    // READ ALL LOANS
    public List<Loans> findAll() {
        List<Loans> loans = new ArrayList<>();
        String sql = "SELECT * FROM LOANS";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            logger.info("Retrieved {} loans from database.", loans.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all loans", e);
            throw new DatabaseException("Error retrieving loans", e);
        }
        return loans;
    }

    // FIND LOANS BY USER ID
    public List<Loans> findByUserId(Long userId) {
        List<Loans> loans = new ArrayList<>();
        String sql = "SELECT * FROM LOANS WHERE USER_ID = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            if (loans.isEmpty()) {
                logger.warn("No loans found for user ID: {}", userId);
            } else {
                logger.info("Retrieved {} loans for user ID: {}", loans.size(), userId);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving loans for user ID: {}", userId, e);
            throw new DatabaseException("Error retrieving loans for user ID: " + userId, e);
        }
        return loans;
    }

    // FIND LOANS BY USER EMAIL
    public List<Loans> findByUserEmail(String email) {
        List<Loans> loans = new ArrayList<>();
        String sql = "SELECT * FROM LOANS WHERE USER_ID = (SELECT ID FROM USERS WHERE EMAIL = ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            if (loans.isEmpty()) {
                logger.warn("No loans found for user email: {}", email);
            } else {
                logger.info("Retrieved {} loans for user email: {}", loans.size(), email);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving loans for user email: {}", email, e);
            throw new DatabaseException("Error retrieving loans for user email: " + email, e);
        }
        return loans;
    }

    // FIND LOAN BY ID
    public Loans findById(int id) {
        String sql = "SELECT * FROM LOANS WHERE ID = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                logger.info("Loan found with ID: {}", id);
                return mapResultSetToLoan(rs);
            } else {
                logger.warn("No loan found with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving loan with ID: {}", id, e);
            throw new DatabaseException("Error retrieving loan with ID: " + id, e);
        }
        return null;
    }

    // DELETE LOAN
    public void delete(int id) {
        String sql = "DELETE FROM LOANS WHERE ID = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Loan deleted successfully. ID: {}", id);
            } else {
                logger.warn("Attempted to delete non-existent loan with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting loan ID: {}", id, e);
            throw new DatabaseException("Error deleting loan", e);
        }
    }

    // UPDATE LOAN
    public void update(Loans loan) {
        String sql = "UPDATE LOANS SET DESCRIPTION = ?, AMOUNT = ?, PAID = ?, USER_ID = ? WHERE ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loan.getDescription());
            pstmt.setDouble(2, loan.getAmount());
            pstmt.setBoolean(3, loan.isApproved());
            pstmt.setLong(4, loan.getUser().getId());
            pstmt.setInt(5, loan.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                logger.warn("Loan update failed, no rows affected. Loan ID: {}", loan.getId());
                throw new DatabaseException("Loan update failed, no rows affected.",null);
            }

            logger.info("Loan successfully updated. ID: {}", loan.getId());

        } catch (SQLException e) {
            logger.error("Error updating loan. Loan ID: {}", loan.getId(), e);
            throw new DatabaseException("Error updating loan.", e);
        }
    }

    // MAP RESULTSET TO LOANS OBJECT
    private Loans mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loans loan = new Loans();
        loan.setId(rs.getInt("ID"));
        loan.setDescription(rs.getString("DESCRIPTION"));
        loan.setAmount(rs.getDouble("AMOUNT"));
        loan.setApproved(rs.getBoolean("PAID"));

        User user = new User();
        user.setId(rs.getLong("USER_ID"));
        loan.setUser(user);

        return loan;
    }
}
