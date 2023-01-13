package pl.lodz.p.it.pas.model;

import java.io.Serializable;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractEntity implements Serializable {

    @Version
    private long version;

}
