package pl.lodz.p.it.pas.model.user;

import javax.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Admin extends User {

    public Admin(String username) {
        super(username);
        this.setRole("CLIENT");
    }
}
