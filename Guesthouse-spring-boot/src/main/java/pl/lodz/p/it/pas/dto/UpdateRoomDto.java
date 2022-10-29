package pl.lodz.p.it.pas.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomDto implements Serializable {
    @Min(value = 1)
    private Integer roomNumber;

    @Min(value = 1)
    private Double price;

    @Min(value = 1)
    private Integer size;
}
