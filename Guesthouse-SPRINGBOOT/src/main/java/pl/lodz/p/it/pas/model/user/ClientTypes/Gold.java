package pl.lodz.p.it.pas.model.user.ClientTypes;

import pl.lodz.p.it.pas.common.MyValidator;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class Gold extends ClientType {
    public Gold() {
        this.setDiscount(0.15);
        MyValidator.validate(this);
    }
}
