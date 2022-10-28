package pl.lodz.p.it.pas.model.user.ClientTypes;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import pl.lodz.p.it.pas.common.MyValidator;

@Entity
@Access(AccessType.FIELD)
public class Bronze extends ClientType {
    public Bronze() {
        this.setDiscount(0.05);
        MyValidator.validate(this);
    }
}
