package pl.lodz.pas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.pas.common.MyValidator;
import pl.lodz.pas.model.ClientTypes.ClientType;

//TODO implement different user level classes

@Entity
@NamedQueries({
        @NamedQuery(name = "Client.getAll",
                query = "SELECT c FROM Client c"),
        @NamedQuery(name = "Client.getByPersonalId",
                query = "SELECT c FROM Client c WHERE c.personalId = :personalId")
})
@Data
@NoArgsConstructor
public class Client extends User {

    @Id
    @GeneratedValue(generator = "clientId")
    @Column(name = "client_id")
    private Long id;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "personal_id", unique = true)
    private String personalId;

    //@NotNull
    @JoinColumn(name = "client_type")
    @NotNull
    @ManyToOne
    private ClientType clientType;

    @NotNull
    @Embedded
    private Address address;

    public Client(String firstName, String lastName, String personalId, Address address, ClientType clientType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.address = address;
        this.clientType = clientType;
        MyValidator.validate(this);
    }
}
