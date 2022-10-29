package pl.lodz.p.it.pas.model.user;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class Admin extends User {

    public Admin(String username) {
        super(username);
        this.setRole("CLIENT");
    }
}
