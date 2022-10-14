package pl.lodz.nbd.model.ClientTypes;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import pl.lodz.nbd.common.MyValidator;

import java.util.UUID;

@Entity
@Access(AccessType.FIELD)
public class Default extends ClientType {
    public Default() {
        this.setDiscount(0.0);
        this.setUuid(UUID.randomUUID());
        MyValidator.validate(this);
    }
}
