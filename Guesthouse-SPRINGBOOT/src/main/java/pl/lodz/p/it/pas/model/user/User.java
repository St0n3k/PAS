package pl.lodz.p.it.pas.model.user;


import lombok.Data;
import lombok.NoArgsConstructor;

import pl.lodz.p.it.pas.model.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@NamedQueries({
    @NamedQuery(name = "User.getAll",
                query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.getByUserId",
                query = "SELECT u FROM User u WHERE u.id = :userId"),
    @NamedQuery(name = "User.getByUsername",
                query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.matchByUsername",
                query = "SELECT u FROM User u WHERE u.username LIKE :username")
})

@Data
@NoArgsConstructor
public abstract class User extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "userId", strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(name = "username", unique = true)
    private String username;

    @NotNull
    @Column(name = "active")
    private boolean active = true;

    @NotNull
    @Column(name = "role")
    private String role = "CLIENT";

    public User(String username) {
        this.username = username;
    }
}
