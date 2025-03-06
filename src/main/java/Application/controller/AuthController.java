package Application.controller;

import Application.model.User;
import Application.service.UserService;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class AuthController {
    private static final UserService userService = new UserService();


    public static void register(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        try {
            UserService.createUser(user);
            ctx.status(HttpStatus.CREATED).json("User registered successfully.");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(e.getMessage());
        }
    }
    // LOGIN METHOD
    public static void login(Context ctx) {
        User loginRequest = ctx.bodyAsClass(User.class);
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            ctx.sessionAttribute("user", user.getEmail());  // Store user session
            ctx.sessionAttribute("role", user.getRole());   // Store role session
            ctx.status(HttpStatus.OK).json("Login successful");
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED).json("Invalid credentials");
        }
    }

    // LOGOUT METHOD
    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate(); // Invalidate the session
        ctx.status(HttpStatus.OK).json("Logged out successfully");
    }

    // Middleware to restrict access based on role
    public static Handler requireRole(String requiredRole) {
        return ctx -> {
            String role = ctx.sessionAttribute("role");
            if (role == null || !role.equals(requiredRole)) {
                throw new ForbiddenResponse("Access denied. Requires role: " + requiredRole);

            }
        };
    }
}
