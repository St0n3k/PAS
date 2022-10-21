package pl.lodz.pas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@NamedQueries({
        @NamedQuery(name = "User.getAll",
                query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.getByUserId",
                query = "SELECT u FROM User u WHERE u.id = :userId"),
        @NamedQuery(name = "User.getByUsername",
                query = "SELECT u FROM User u WHERE u.username = :username")
})

@Data
@NoArgsConstructor
public class User extends AbstractEntity{

    @Id
    @GeneratedValue(generator = "userId")
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(name = "username", unique = true)
    private String username;

    @NotNull
    @Column(name = "active")
    private boolean active = true;

    public User(String username) {
        this.username = username;
    }
}
