package pl.lodz.pas.model.user.ClientTypes;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import pl.lodz.pas.common.MyValidator;

@Entity
@Access(AccessType.FIELD)
public class Default extends ClientType {
    public Default() {
        this.setDiscount(0.0);
        MyValidator.validate(this);
    }
}
