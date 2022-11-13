package pl.lodz.p.it.pas.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Employee extends User {

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;


    public Employee(String username, String firstName, String lastName) {
        super(username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.setRole("EMPLOYEE");
    }
}
