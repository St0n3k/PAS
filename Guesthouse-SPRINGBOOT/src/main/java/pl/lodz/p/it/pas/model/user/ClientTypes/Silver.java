package pl.lodz.p.it.pas.model.user.ClientTypes;

import pl.lodz.p.it.pas.common.MyValidator;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class Silver extends ClientType {
    public Silver() {
        this.setDiscount(0.1);
        MyValidator.validate(this);
    }
}
