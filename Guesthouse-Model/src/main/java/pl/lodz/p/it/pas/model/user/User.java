package pl.lodz.p.it.pas.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.json.bind.annotation.JsonbTransient;
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
import pl.lodz.p.it.pas.model.AbstractEntity;

import java.security.Principal;

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
                query = "SELECT u FROM User u WHERE u.username LIKE :username"),
    @NamedQuery(name = "User.getByRole",
                query = "SELECT u FROM User u WHERE u.role LIKE :role ORDER BY u.id"),
    @NamedQuery(name = "User.getByRoleMatchingName",
                query = """
                    SELECT u FROM User u
                    WHERE u.role LIKE :role AND u.username LIKE :username
                    ORDER BY u.id""")
})

@Data
@NoArgsConstructor
public abstract class User extends AbstractEntity implements Principal {

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

    @NotNull
    @Column(name = "password")
    @JsonbTransient
    private String password;

    @Override
    @JsonIgnore
    public String getName() {
        return getUsername();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
