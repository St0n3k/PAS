package pl.lodz.p.it.pas.model;

import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
@Data
public abstract class AbstractEntity {

    @Version
    private long version;

}
