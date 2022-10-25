package pl.lodz.pas.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.pas.model.AbstractEntity;

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

    public String getRole() {
        return "CLIENT";
    }

    public User(String username) {
        this.username = username;
    }
}
