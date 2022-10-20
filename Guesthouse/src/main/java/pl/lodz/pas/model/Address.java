package pl.lodz.pas.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.pas.common.MyValidator;

@Embeddable
@Access(AccessType.FIELD)
@Data
@NoArgsConstructor
public class Address {
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
