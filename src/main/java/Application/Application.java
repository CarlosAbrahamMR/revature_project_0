package Application;

import Application.controller.AuthController;
import Application.controller.UserController;
import Application.controller.LoansController;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class Application {
    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7000);

        // Authentication
        app.post("/auth/register", AuthController::register);
        app.post("/auth/login", AuthController::login);
        app.post("/auth/logout", AuthController::logout);

        // Users
        app.get("/users/{id}", UserController.getUserById);
        app.put("/users/{id}", UserController.updateUser);

        // Loans
        app.post("/loans", LoansController.createLoan);
        app.get("/loans", LoansController.getAllLoans);
        app.get("/loans/{loanId}", LoansController.getLoanById);
        app.put("/loans/{loanId}/approve", LoansController.approveLoan);
        app.put("/loans/{loanId}/reject", LoansController.rejectLoan);

        System.out.println("🚀 Server running on http://localhost:7000/");
    }
}
