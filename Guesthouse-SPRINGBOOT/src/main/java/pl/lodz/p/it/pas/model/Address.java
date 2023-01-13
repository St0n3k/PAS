package pl.lodz.p.it.pas.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.pas.common.MyValidator;

@Embeddable
@Access(AccessType.FIELD)
@Data
@NoArgsConstructor
public class Address implements Serializable {
    @NotNull
    @Column
    private String city;

    @NotNull
    @Column
    private String street;

    @NotNull
    @Column(name = "house_number")
    private int houseNumber;

    public Address(String city, String street, int houseNumber) {
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        MyValidator.validate(this);
    }
}
