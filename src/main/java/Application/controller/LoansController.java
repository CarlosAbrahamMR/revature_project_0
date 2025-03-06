package Application.controller;

import Application.model.Loans;
import Application.service.LoansService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.List;

public class LoansController {

    // CREATE A LOAN (Only for logged-in users)
    public static Handler createLoan = ctx -> {
        Loans loan = ctx.bodyAsClass(Loans.class);
        String loggedInEmail = ctx.sessionAttribute("user");

        // Ensure the loan is assigned to the logged-in user
        loan.getUser().setEmail(loggedInEmail);
        try {
            LoansService.createLoan(loan);
            ctx.status(HttpStatus.CREATED).json("Loan created successfully.");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(e.getMessage());
        }
    };

    // GET ALL LOANS (Manager sees all, User sees their own)
    public static Handler getAllLoans = ctx -> {
        String role = ctx.sessionAttribute("role");
        String loggedInEmail = ctx.sessionAttribute("user");

        List<Loans> loans;
        if ("ADMIN".equals(role)) {
            loans = LoansService.getAllLoans();
        } else {
            loans = LoansService.getLoansByUserEmail(loggedInEmail);
        }

        ctx.status(HttpStatus.OK).json(loans);
    };

    // GET A SPECIFIC LOAN (Only owner or Manager)
    public static Handler getLoanById = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("loanId"));
        String role = ctx.sessionAttribute("role");
        String loggedInEmail = ctx.sessionAttribute("user");

        Loans loan = LoansService.getLoanById(id);
        if (loan == null) {
            ctx.status(HttpStatus.NOT_FOUND).json("Loan not found.");
            return;
        }

        if (!role.equals("ADMIN") && !loan.getUser().getEmail().equals(loggedInEmail)) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied.");
            return;
        }

        ctx.status(HttpStatus.OK).json(loan);
    };

    // APPROVE LOAN (Only Manager)
    public static Handler approveLoan = ctx -> {
        int loanId = Integer.parseInt(ctx.pathParam("loanId"));
        String role = ctx.sessionAttribute("role");

        if (!role.equals("ADMIN")) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied. Only managers can approve loans.");
            return;
        }

        LoansService.approveLoan(loanId);
        ctx.status(HttpStatus.OK).json("Loan approved.");
    };

    // REJECT LOAN (Only Manager)
    public static Handler rejectLoan = ctx -> {
        int loanId = Integer.parseInt(ctx.pathParam("loanId"));
        String role = ctx.sessionAttribute("role");

        if (!role.equals("ADMIN")) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied. Only managers can reject loans.");
            return;
        }

        LoansService.rejectLoan(loanId);
        ctx.status(HttpStatus.OK).json("Loan rejected.");
    };
}
