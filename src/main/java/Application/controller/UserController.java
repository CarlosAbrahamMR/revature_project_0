package Application.controller;

import Application.advice.NotFoundException;
import Application.model.User;
import Application.model.UserRole;
import Application.service.UserService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class UserController {

    // GET USER INFO (User can see their own, Manager can see any)
    public static Handler getUserById = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String loggedInEmail = ctx.sessionAttribute("user");
        UserRole role = ctx.sessionAttribute("role");

        User user = UserService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("User not found.");

        }

        // User can only see their own info, Manager can see anyone
        if (!(role == UserRole.ADMIN) && !user.getEmail().equals(loggedInEmail)) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied.");
            return;
        }

        ctx.status(HttpStatus.OK).json(user);
    };

    // UPDATE USER PROFILE (Only if it's the same user or a Manager)
    public static Handler updateUser = ctx -> {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String loggedInEmail = ctx.sessionAttribute("user");
        UserRole role = ctx.sessionAttribute("role");

        User existingUser = UserService.getUserById(id);
        if (existingUser == null) {
            ctx.status(HttpStatus.NOT_FOUND).json("User not found.");
            return;
        }

        if (!(role==UserRole.ADMIN) && !existingUser.getEmail().equals(loggedInEmail)) {
            ctx.status(HttpStatus.FORBIDDEN).json("Access denied.");
            return;
        }

        User updatedUser = ctx.bodyAsClass(User.class);
        updatedUser.setId((long) id);
        UserService.updateUser(updatedUser);
        ctx.status(HttpStatus.OK).json("User updated successfully.");
    };
}
