package Application.service;

import Application.dao.UserDao;
import Application.model.User;
import Application.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("securePass123");
        user.setRole(UserRole.USER);
    }

    @Test
    void testCreateUserWithInvalidEmail_ShouldThrowException() {
        user.setEmail("invalid-email");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Mail is not valid.", exception.getMessage());
    }

    @Test
    void testCreateUserWithShortPassword_ShouldThrowException() {
        user.setPassword("123");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("password must have almost 6 characters.", exception.getMessage());
    }

}
