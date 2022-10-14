package pl.lodz.nbd.model.ClientTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.lodz.nbd.model.AbstractEntity;

@Entity
@Table(name = "client_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@NamedQueries({
        @NamedQuery(name = "ClientType.getAll",
                query = "SELECT ct FROM ClientType ct"),
        @NamedQuery(name = "ClientType.getByType",
                query = "SELECT ct FROM ClientType ct WHERE ct.name LIKE :type")
})
@Data
public abstract class ClientType extends AbstractEntity {

    @Column
    @Id
    @GeneratedValue(generator = "clientTypeId", strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    @Column
    @NotNull
    private double discount;

    public ClientType() {
        this.name = this.getClass().getSimpleName();
    }

    public double applyDiscount(double price) {
        return price * (1.0 - discount);
    }

}
