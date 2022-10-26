package pl.lodz.pas.model.user;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Admin extends User {

    public Admin(String username) {
        super(username);
        this.setRole("CLIENT");
    }
}
