package pl.lodz.p.it.pas.model.user;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.pas.common.MyValidator;
import pl.lodz.p.it.pas.model.Address;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;

@Entity
@Data
@NoArgsConstructor
public class Client extends User {

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "personal_id", unique = true)
    private String personalId;

    @JoinColumn(name = "client_type")
    @NotNull
    @ManyToOne
    private ClientType clientType;

    @NotNull
    @Embedded
    private Address address;


    public Client(String username, String firstName, String lastName, String personalId, Address address,
                  ClientType clientType) {
        super(username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.address = address;
        this.clientType = clientType;
        this.setRole("CLIENT");
        MyValidator.validate(this);
    }
}
