package Application.model;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private List<Loans> loans;

}
