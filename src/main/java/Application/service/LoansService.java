package Application.service;

import Application.dao.LoansDao;
import Application.dao.UserDao;
import Application.model.Loans;
import Application.model.User;

import java.util.List;

public class LoansService {
    private static final LoansDao loanDao = new LoansDao();
    private static final UserDao userDao = new UserDao();

    public static List<Loans> getAllLoans() {
        return loanDao.findAll();
    }

    public static Loans getLoanById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }
        Loans loan = loanDao.findById(id);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found.");
        }
        return loan;
    }

    public static List<Loans> getLoansByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive.");
        }
        return loanDao.findByUserId((long) userId);
    }

    public static List<Loans> getLoansByUserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        return loanDao.findByUserEmail(email);
    }

    public static void createLoan(Loans loan) {
        if (loan.getDescription() == null || loan.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Loan description is required.");
        }
        if (loan.getAmount() <= 0) {
            throw new IllegalArgumentException("Loan amount must be positive.");
        }
        if (loan.getUser() == null || loan.getUser().getId() <= 0) {
            throw new IllegalArgumentException("Valid User ID is required.");
        }

        // Verify if user exists
        User user = userDao.findById(Math.toIntExact(loan.getUser().getId()));
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }


        loanDao.insert(loan);
    }

    public static void deleteLoan(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }
        loanDao.delete(id);
    }


    public static void approveLoan(int loanId) {
        Loans loan = getLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found.");
        }

        loan.setApproved(true); // Assuming "approved" means it's marked as paid
        loanDao.update(loan);
    }

    public static void rejectLoan(int loanId) {
        Loans loan = getLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found.");
        }

        loan.setApproved(false); // Assuming rejected means it's marked as unpaid
        loanDao.update(loan);
    }


}
