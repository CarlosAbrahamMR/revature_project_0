package Application.service;

import Application.model.User;
import Application.dao.UserDao;
import Application.model.UserRole;

import java.util.List;

public class UserService {
    private static final UserDao userDAO = new UserDao();

    public static List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public static User getUserById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }
        return userDAO.findById(id);
    }

    public static void createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is obligatory.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Mail is not valid.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("password must have almost 6 characters.");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("role is obligatory.");
        }
        try {
            user.setRole(UserRole.valueOf(user.getRole().name().toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role: " + user.getRole() + ". Must be ADMIN or USER.", e);
        }


        userDAO.insert(user);
    }

    public static void updateUser(User user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }
        userDAO.update(user);
    }

    public static void deleteUser(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }
        userDAO.delete(id);
    }

    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);  // Retrieve user for authentication
    }

   /* public User authenticate(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }*/
}
