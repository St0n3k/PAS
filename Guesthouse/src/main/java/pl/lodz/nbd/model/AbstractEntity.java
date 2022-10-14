package pl.lodz.nbd.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class AbstractEntity implements Serializable {

    @NotNull
    private UUID uuid;

    @Version
    private long version;
}
