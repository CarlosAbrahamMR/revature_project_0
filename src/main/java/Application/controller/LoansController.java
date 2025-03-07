package Application.controller;

import Application.dao.UserDao;
import Application.model.Loans;
import Application.model.User;
import Application.model.UserRole;
import Application.service.LoansService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.List;


public class LoansController {
    private static UserDao userDao = new UserDao(); // Ensure it's initialized


    // CREATE A LOAN (Only for logged-in users)
    public static Handler createLoan = ctx -> {
        Loans loan = ctx.bodyAsClass(Loans.class);

        String loggedInEmail = ctx.sessionAttribute("user");

        if (loggedInEmail == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("User is not logged in.");
            return;
        }

        // Fetch the user object from the database
        User user = userDao.findByEmail(loggedInEmail);
        if (user == null) {
            ctx.status(HttpStatus.BAD_REQUEST).json("User not found.");
            return;
        }

        // Set the user in the loan object
        loan.setUser(user);
        try {
            LoansService.createLoan(loan);
            ctx.status(HttpStatus.CREATED).json("Loan created successfully.");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(e.getMessage());
        }
    };

    // GET ALL LOANS (Manager sees all, User sees their own)
    public static Handler getAllLoans = ctx -> {
        UserRole role = ctx.sessionAttribute("role");
        String loggedInEmail = ctx.sessionAttribute("user");

        if (loggedInEmail == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("User is not logged in.");
            return;
        }

        // Fetch the user object from the database
        User user = userDao.findByEmail(loggedInEmail);
        if (user == null) {
            ctx.status(HttpStatus.BAD_REQUEST).json("User not found.");
            return;
        }
        List<Loans> loans;
        if (UserRole.ADMIN == role) {
            loans = LoansService.getAllLoans();
        } else {
            loans = LoansService.getLoansByUserEmail(loggedInEmail);
        }

        ctx.status(HttpStatus.OK).json(loans);
    };

    // GET A SPECIFIC LOAN (Only owner or Manager)
    public static Handler getLoanById = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("loanId"));
        UserRole role = ctx.sessionAttribute("role");
        String loggedInEmail = ctx.sessionAttribute("user");

        Loans loan = LoansService.getLoanById(id);
        if (loggedInEmail == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("User is not logged in.");
            return;
        }
        if (loan == null) {
            ctx.status(HttpStatus.NOT_FOUND).json("Loan not found.");
            return;
        }

        if (!(UserRole.ADMIN==role) && !loan.getUser().getEmail().equals(loggedInEmail)) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied.");
            return;
        }

        ctx.status(HttpStatus.OK).json(loan);
    };

    // APPROVE LOAN (Only Manager)
    public static Handler approveLoan = ctx -> {
        int loanId = Integer.parseInt(ctx.pathParam("loanId"));
        UserRole role = ctx.sessionAttribute("role");
        String loggedInEmail = ctx.sessionAttribute("user");

        if (loggedInEmail == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("User is not logged in.");
            return;
        }

        if (!(role==UserRole.ADMIN)) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied. Only managers can approve loans.");
            return;
        }

        LoansService.approveLoan(loanId);
        ctx.status(HttpStatus.OK).json("Loan approved.");
    };

    // REJECT LOAN (Only Manager)
    public static Handler rejectLoan = ctx -> {
        int loanId = Integer.parseInt(ctx.pathParam("loanId"));
        UserRole role = ctx.sessionAttribute("role");
        String loggedInEmail = ctx.sessionAttribute("user");

        if (loggedInEmail == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("User is not logged in.");
            return;
        }

        if (!(UserRole.ADMIN==role)) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied. Only managers can reject loans.");
            return;
        }

        LoansService.rejectLoan(loanId);
        ctx.status(HttpStatus.OK).json("Loan rejected.");
    };
}
