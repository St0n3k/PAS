package pl.lodz.p.it.pas.model.user.ClientTypes;

import pl.lodz.p.it.pas.common.MyValidator;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class Bronze extends ClientType {
    public Bronze() {
        this.setDiscount(0.05);
        MyValidator.validate(this);
    }
}
