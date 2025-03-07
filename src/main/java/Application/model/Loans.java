package Application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loans {
    private int id;
    private String description;
    private double amount;
    private boolean isApproved;
    private User user;


}
