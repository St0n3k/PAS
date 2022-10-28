package pl.lodz.p.it.pas.model.user;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.pas.model.AbstractEntity;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
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
