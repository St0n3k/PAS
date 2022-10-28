package pl.lodz.p.it.pas.model.user.ClientTypes;

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
import pl.lodz.p.it.pas.model.AbstractEntity;

@Entity
@Table(name = "client_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
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
