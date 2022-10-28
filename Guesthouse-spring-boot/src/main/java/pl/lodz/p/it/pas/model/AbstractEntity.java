package pl.lodz.p.it.pas.model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import lombok.Data;
import org.springframework.data.annotation.Version;

@MappedSuperclass
@Data
public abstract class AbstractEntity implements Serializable {

    @Version
    private long version;

}
